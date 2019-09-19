package com.sun.quartz.service.impl;

import com.sun.quartz.commons.Constant;
import com.sun.quartz.pojo.JobInfo;
import com.sun.quartz.service.JobService;
import com.sun.quartz.service.SelfJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author SunCongCong
 * @date 2019/9/19 9:57
 * @intro
 */
@Slf4j
@Service
public class SelfJobServiceImpl implements SelfJobService {
    @Autowired
    private JobService jobService;

    @PostConstruct
    @Override
    public void init() {
        jobService.deleteAllJob();//重启项目时，删除所有的定时任务
        log.info("start quartz backup task.");
        createTestJob();//将测试job添加入初始化--自己业务的定时任务都需要进行初始化
    }

    private void createTestJob(){
        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobName("TestJob");//测试job类名--与定时任务类名一致，类反射时会进行检查，不一致时会报错
        jobInfo.setJobGroup("TestJob");
        jobInfo.setCronExpression(Constant.TEST_JOB_CORN);
        try {
            //将业务的定时任务加入QUARTZ框架
            jobService.addCronJob(jobInfo);
            log.info("------------添加TestJob---------");
        } catch (Exception e) {
            log.error("e", e);
        }
    }
}
