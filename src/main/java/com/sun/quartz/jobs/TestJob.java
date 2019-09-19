package com.sun.quartz.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author SunCongCong
 * @date 2019/9/3 10:35
 * @intro 自己业务的定时任务test ，必须继承QuartzJobBean
 *        在executeInternal（）方法中编写自己的任务
 */
public class TestJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("-----------------------定时任务开始-------------------------");
    }
}
