QUARTZ定时任务demo

**1.pom文件引入QUARTZ依赖**

<dependency>
     
     <groupId>org.springframework.boot</groupId>
     
     <artifactId>spring-boot-starter-quartz</artifactId>

</dependency>

**2.实现定时任务框架本身的增删改查（使定时任务可以运行起来）**

_pojo：_

jobinfo--定时任务具体信息（任务名/任务组/cron表达式/任务类型/时间类型）

QuartzJonInfo--Quartz框架需要的信息

_mapper：_

JobAndTriggerMapper--查询获取数据库的定时任务表数据信息

_service：_

JobService & JobServiceImpl--添加/删除/查看/分页定时任务信息（具体Quartz增删改查的业务）

_conrtroller:_

JobController--页面配置定时任务接口，实现定时任务可视化配置

**3.添加自己的业务定时任务**

SelfJobService & SelfJobServiceImpl--初始化定时任务，添加Job信息到Quartz

TestJob--测试定时任务（具体业务类）

继承QuartzJobBean，在executeInternal（）中实现自己的业务












