package com.sun.quartz.pojo;


import com.sun.quartz.commons.Constant;

import java.util.Objects;

public class JobInfo {

    private String jobName;

    private String jobGroup;

    private String cronExpression;

    private String jobType;

    private Integer timeType;

    public String getJobName() {
        if (!jobName.trim().contains(Constant.JOB_PACAGE_NAME)) {
            return Constant.JOB_PACAGE_NAME.concat(jobName.trim());
        }
        return jobName.trim();
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Integer getTimeType() {
        return timeType;
    }

    public void setTimeType(Integer timeType) {
        this.timeType = timeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobInfo jobInfo = (JobInfo) o;
        return Objects.equals(jobName, jobInfo.jobName) &&
                Objects.equals(jobGroup, jobInfo.jobGroup) &&
                Objects.equals(cronExpression, jobInfo.cronExpression) &&
                Objects.equals(jobType, jobInfo.jobType) &&
                Objects.equals(timeType, jobInfo.timeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobName, jobGroup, cronExpression, jobType, timeType);
    }

    @Override
    public String toString() {
        return "JobInfo{" +
                "jobName='" + jobName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", jobType='" + jobType + '\'' +
                ", timeType=" + timeType +
                '}';
    }
}
