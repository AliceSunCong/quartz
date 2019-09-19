package com.sun.quartz.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.sun.quartz.commons.Constant;
import com.sun.quartz.pojo.JobInfo;
import com.sun.quartz.pojo.QuartzJobInfo;
import com.sun.quartz.service.JobService;
import com.sun.quartz.service.SelfJobService;
import com.sun.quartz.utils.CommonUtil;
import com.sun.quartz.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.quartz.DateBuilder.futureDate;

/**
 * @author SunCongCong
 * @date 2019/9/3 10:16
 * @intro
 */
@Slf4j
@RestController
@RequestMapping(value = "job")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private SelfJobService selfJobService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private DateUtil dateUtil;

    /**
     * 添加任务
     *
     * @param jobInfo
     * @throws Exception
     */
    @PostMapping(value = "/addjob")
    public JSONObject addjob(@RequestBody JobInfo jobInfo) throws Exception {
        if ("".equals(jobInfo.getJobName()) || "".equals(jobInfo.getJobGroup()) || "".equals(jobInfo.getCronExpression())) {
            return null;
        }
        if (jobInfo.getTimeType() == null) {
            addCronJob(jobInfo);
            return CommonUtil.successJson();
        }
        addSimpleJob(jobInfo);
        return CommonUtil.successJson();
    }

    //CronTrigger
    public void addCronJob(JobInfo jobInfo) throws Exception {
        if (!CronExpression.isValidExpression(jobInfo.getCronExpression())) {
            log.error("corn is invalid");
            return;
        }

        // 启动调度器
        scheduler.start();

        //构建job信息
        JobDetail jobDetail = JobBuilder.newJob(getClass(jobInfo.getJobName()).getClass()).
                withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                .build();

        //表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression());
        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().
                withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                .withSchedule(scheduleBuilder)
                .build();

        trigger.getJobDataMap().put(Constant.JOB_PARAM, jobInfo.getJobGroup());

        try {
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            log.info("创建定时任务失败" + e);
            throw new Exception("创建定时任务失败");
        }
    }

    //Simple Trigger
    public void addSimpleJob(JobInfo jobInfo) throws Exception {
        // 启动调度器
        scheduler.start();

        //构建job信息
        JobDetail jobDetail = JobBuilder.newJob(getClass(jobInfo.getJobName()).getClass())
                .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                .build();

        DateBuilder.IntervalUnit verDate = dateUtil.verification(jobInfo.getTimeType());
        SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                .startAt(futureDate(Integer.parseInt(jobInfo.getCronExpression()), verDate))
                .forJob(jobInfo.getJobName(), jobInfo.getJobGroup())
                .build();

        try {
            scheduler.scheduleJob(jobDetail, simpleTrigger);

        } catch (SchedulerException e) {
            log.info("创建定时任务失败" + e);
            throw new Exception("创建定时任务失败");
        }
    }

    /**
     * 暂停任务
     *
     * @param jobClassName
     * @param jobGroupName
     * @throws Exception
     */
    @PostMapping(value = "/pausejob")
    public void pausejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        jobPause(jobClassName, jobGroupName);
    }

    public void jobPause(String jobClassName, String jobGroupName) throws Exception {
        scheduler.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
    }

    /**
     * 恢复任务
     *
     * @param jobClassName
     * @param jobGroupName
     * @throws Exception
     */
    @PostMapping(value = "/resumejob")
    public void resumejob(@RequestParam(value = "jobClassName") String jobClassName, @RequestParam(value = "jobGroupName") String jobGroupName) throws Exception {
        jobresume(jobClassName, jobGroupName);
    }

    public void jobresume(String jobClassName, String jobGroupName) throws Exception {
        scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
    }

    /**
     * 更新任务
     */
    @PostMapping(value = "/reschedulejob")
    public JSONObject rescheduleJob(@RequestBody JSONObject requestJson) throws Exception {
        String jobName = requestJson.getString("jobName");
        String jobGroupName = requestJson.getString("jobGroup");
        String cronExpression = requestJson.getString("cronExpression");

        jobreschedule(jobName, jobGroupName, cronExpression);
        return CommonUtil.successJson();
    }

    public void jobreschedule(String jobClassName, String jobGroupName, String cronExpression) throws Exception {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            log.info("更新定时任务失败" + e);
            throw new Exception("更新定时任务失败");
        }
    }

    /**
     * 删除任务
     * 删除操作前应该暂停该任务的触发器，并且停止该任务的执行
     *
     * @throws Exception
     */
    @PostMapping(value = "/deletejob")
    public JSONObject deletejob(@RequestBody JobInfo jobInfo) throws Exception {
        String jobName = jobInfo.getJobName();
        String jobGroup = jobInfo.getJobGroup();
        jobdelete(jobName, jobGroup);

        return CommonUtil.successJson();
    }

    public void jobdelete(String jobName, String jobGroupName) throws Exception {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroupName));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroupName));
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
    }

    /**
     * 查询任务
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/queryjob")
    public JSONObject queryjob(@RequestParam(value = "pageNum") Integer pageNum, @RequestParam(value = "pageRow") Integer pageSize) {
        PageInfo<QuartzJobInfo> jobAndTrigger = jobService.getJobAndTriggerDetails(pageNum, pageSize);
        List<QuartzJobInfo> list = jobAndTrigger.getList();

        JSONObject result = CommonUtil.successJson();
        JSONObject info = new JSONObject();
        info.put("list", list);
        info.put("totalCount", jobAndTrigger.getTotal());
        info.put("totalPage", jobAndTrigger.getPageSize());
        result.put("info", info);
        return result;
    }

    /**
     * 根据类名称，通过反射得到该类，然后创建一个实例。
     *
     * @param classname
     * @return
     * @throws Exception
     */
    public static Job getClass(String classname) throws Exception {
        Class<?> class1 = Class.forName(classname);
        return (Job) class1.newInstance();
    }

    @GetMapping("/addAllJob")
    public JSONObject addAllJob(){
        selfJobService.init();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "success");
        jsonObject.put("code",200);
        return jsonObject;
    }
}
