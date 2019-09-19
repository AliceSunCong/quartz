package com.sun.quartz.commons;

import org.springframework.context.annotation.Configuration;

/**
 * @author SunCongCong
 * @date 2019/9/3 10:23
 * @intro
 */
@Configuration
public class Constant {
    public static final String JOB_PACAGE_NAME = "com.sun.quartz.jobs.";
    public static final String JOB_PARAM = "sunId";

    public static final String TEST_JOB_CORN = "*/5 * * * * ?";//每隔5秒执行一次
}
