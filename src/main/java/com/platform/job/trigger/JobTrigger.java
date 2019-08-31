package com.platform.job.trigger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JobTrigger{
	
	public void afterPropertiesSet() throws Exception {
		 // 2、创建JobDetail实例，并与PrintWordsJob类绑定(Job执行内容)
//	    JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
//	                                    .withIdentity("job1", "sx_group1").build();
	    // 3、构建Trigger实例,每隔1s执行一次
//	    Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
//	            .startNow()//立即生效
//	            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//	            .withIntervalInSeconds(1)//每隔1s执行一次
//	            .repeatForever()).build();//一直执行
		
//		JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
//                .withIdentity("sx_job", "sx_group").build();
//	    
//	    Date startDate = new Date();
//        startDate.setTime(startDate.getTime() + 5000);
//
//        Date endDate = new Date();
//        endDate.setTime(startDate.getTime() + 5000);
//        // 3、构建CronTrigger实例,通过表达式进行处理
//        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("sx_trigger", "sx_triggerGroup")
//                .startAt(startDate)
//                .endAt(endDate)
//                .startNow()//立即生效
//                .withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ? "))
//                .build();
//	    
//	    Scheduler scheduler =  JobScheduler.schedulerFactory.getScheduler();
//	    Date date = scheduler.scheduleJob(jobDetail, cronTrigger);
//	    scheduler.start();
//	    log.info(">>>>>>>>>>> addJob success, jobDetail:{}, cronTrigger:{}, date:{}", jobDetail, cronTrigger, date);
//		Scheduler scheduler =  JobScheduler.schedulerFactory.getScheduler();
//		handleSchedulerAll(scheduler);
//      scheduler.start();//默认开启任务调度器
	}

}
