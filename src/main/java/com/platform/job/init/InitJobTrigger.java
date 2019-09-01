package com.platform.job.init;

import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.platform.job.bean.SxTriggersDO;
import com.platform.job.constants.Param;
import com.platform.job.core.MyJob;
import com.platform.job.dao.SxTriggersDao;
import com.platform.job.scheduler.JobScheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class InitJobTrigger implements InitializingBean{
	
	@Autowired
	private SxTriggersDao sxTriggersDao;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("-----------------初始化加载job任务开始---------------------");
		Scheduler scheduler =  JobScheduler.schedulerFactory.getScheduler();
		//handleSchedulerAll(scheduler);
        scheduler.start();//默认开启任务调度器
        log.info("-----------------初始化加载job任务结束---------------------");
	}

	private void handleSchedulerAll(Scheduler scheduler) throws SchedulerException {
		List<SxTriggersDO> sxTriggers = sxTriggersDao.findAll();
		if(CollectionUtils.isEmpty(sxTriggers)) {
			return;
		}
		for(SxTriggersDO sx:sxTriggers) {
			// 1、job key
			String jobName = sx.getJOB_NAME();
			String jobGroup = sx.getJOB_GROUP();
			String cronExpression = sx.getCRON_EXPRESSION();
			String url = sx.getREQUEST_URL();
			String body = sx.getREQUEST_BODY();
	        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
	        JobKey jobKey = new JobKey(jobName, jobGroup);

	        // 2、valid
	        if (scheduler.checkExists(triggerKey)) {
	            continue;    // PASS
	        }
	        log.info("加载 jobName:{},jobGroup:{}", jobName,jobGroup);
	        // 3、corn trigger
	        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();   // withMisfireHandlingInstructionDoNothing 忽略掉调度终止过程中忽略的调度
	        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

	        // 4、job detail
	        JobDetail jobDetail = JobBuilder.newJob(MyJob.class).usingJobData(Param.REQUEST_URL, url).
	        		usingJobData(Param.REQUEST_BODY, body).withIdentity(jobKey).build();
			scheduler.scheduleJob(jobDetail, cronTrigger);
		}
	}
	
}
