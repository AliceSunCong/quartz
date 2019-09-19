package com.sun.quartz.service;


import com.github.pagehelper.PageInfo;
import com.sun.quartz.pojo.JobInfo;
import com.sun.quartz.pojo.QuartzJobInfo;

import java.util.List;

public interface JobService {

    void addCronJob(JobInfo jobInfo) throws Exception;

    void jobdelete(String jobName, String jobGroupName) throws Exception;

    List<QuartzJobInfo> queryjob();

    void deleteAllJob();

    PageInfo<QuartzJobInfo> getJobAndTriggerDetails(Integer pageNum, Integer pageSize);

}
