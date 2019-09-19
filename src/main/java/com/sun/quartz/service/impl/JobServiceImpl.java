package com.sun.quartz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.quartz.commons.Constant;
import com.sun.quartz.mapper.JobAndTriggerMapper;
import com.sun.quartz.pojo.JobInfo;
import com.sun.quartz.pojo.QuartzJobInfo;
import com.sun.quartz.service.JobService;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobAndTriggerMapper jobAndTriggerMapper;

    @Override
    public void addCronJob(JobInfo jobInfo) throws Exception {
        if (!CronExpression.isValidExpression(jobInfo.getCronExpression())) {
            logger.error("corn is invalid");
            throw new Exception("创建定时任务失败");
        }

        // 启动调度器
//        scheduler.start();

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
            logger.info("创建定时任务{}-{}成功",jobInfo.getJobGroup(), jobInfo.getJobName()  );
        } catch (SchedulerException e) {
            logger.error("创建定时任务失败", e);
            throw new Exception("创建定时任务失败");
        }
    }

    @Override
    public void jobdelete(String jobName, String jobGroupName) throws Exception {
        logger.info("删除任务{}-{}...", jobName, jobGroupName);
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroupName));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroupName));
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
    }

    @Override
    public List<QuartzJobInfo> queryjob() {
        return jobAndTriggerMapper.getJobAndTriggerDetails();
    }

    @Override
    public void deleteAllJob() {
        List<QuartzJobInfo> jobInfoList = queryjob();
        for (QuartzJobInfo quartzJobInfo : jobInfoList) {
            String jobName = quartzJobInfo.getJobName();
            String jobGroup = quartzJobInfo.getJobGroup();
            try {
                jobdelete(jobName, jobGroup);
            } catch (Exception e) {
                logger.error("e", e);
            }
        }
    }

    @Override
    public PageInfo<QuartzJobInfo> getJobAndTriggerDetails(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<QuartzJobInfo> list = jobAndTriggerMapper.getJobAndTriggerDetails();
        PageInfo<QuartzJobInfo> page = new PageInfo<>(list);
        return page;
    }

    /**
     * 根据类名称，通过反射得到该类，然后创建一个实例。
     *
     * @param classname
     * @return
     * @throws Exception
     */
    private static Job getClass(String classname) throws Exception {
        Class<?> class1 = Class.forName(classname);
        return (Job) class1.newInstance();
    }
}
