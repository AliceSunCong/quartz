package com.sun.quartz.mapper;

import com.sun.quartz.pojo.QuartzJobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JobAndTriggerMapper {

    @Select("SELECT \n" +
            "        QRTZ_JOB_DETAILS.JOB_NAME,\n" +
            "        QRTZ_JOB_DETAILS.JOB_GROUP,\n" +
            "        QRTZ_JOB_DETAILS.JOB_CLASS_NAME,\n" +
            "        QRTZ_TRIGGERS.TRIGGER_NAME,\n" +
            "        QRTZ_TRIGGERS.TRIGGER_GROUP,\n" +
            "        QRTZ_CRON_TRIGGERS.CRON_EXPRESSION,\n" +
            "        QRTZ_CRON_TRIGGERS.TIME_ZONE_ID\n" +
            "        FROM\n" +
            "        QRTZ_JOB_DETAILS\n" +
            "        INNER JOIN QRTZ_TRIGGERS ON QRTZ_TRIGGERS.TRIGGER_GROUP=QRTZ_JOB_DETAILS.JOB_GROUP\n" +
            "        INNER JOIN QRTZ_CRON_TRIGGERS ON QRTZ_JOB_DETAILS.JOB_NAME = QRTZ_TRIGGERS.JOB_NAME\n" +
            "        and QRTZ_TRIGGERS.TRIGGER_NAME = QRTZ_CRON_TRIGGERS.TRIGGER_NAME\n" +
            "        and QRTZ_TRIGGERS.TRIGGER_GROUP = QRTZ_CRON_TRIGGERS.TRIGGER_GROUP")
    List<QuartzJobInfo> getJobAndTriggerDetails();
}
