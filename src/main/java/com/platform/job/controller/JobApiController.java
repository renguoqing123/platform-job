package com.platform.job.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.platform.job.bean.SxTriggersDO;
import com.platform.job.constants.Param;
import com.platform.job.core.MyJob;
import com.platform.job.dao.SxTriggersDao;
import com.platform.job.scheduler.JobScheduler;
import com.platform.job.util.ResponseResult;
import com.platform.job.util.ResultPage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class JobApiController {
	
	@Autowired
	private SxTriggersDao sxTriggersDao;
	
	@RequestMapping(value = "/addJob", method = RequestMethod.GET)
	public void addJob(@RequestParam String jobName, @RequestParam String jobGroup, 
			 @RequestParam String cronExpression,@RequestParam String url,
			 @RequestParam(required = false) String body,@RequestParam(required = false) String description) throws SchedulerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    	// 1、job key
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);

        Scheduler scheduler = JobScheduler.schedulerFactory.getScheduler();
        // 2、valid
        if (scheduler.checkExists(triggerKey)) {
            return;    // PASS
        }

        // 3、corn trigger
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();   // withMisfireHandlingInstructionDoNothing 忽略掉调度终止过程中忽略的调度
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

        // 4、job detail
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class).usingJobData(Param.REQUEST_URL, url).
        		usingJobData(Param.REQUEST_BODY, body).withIdentity(jobKey).build();
        
        // 5、schedule job
        Date date = scheduler.scheduleJob(jobDetail, cronTrigger);

        // 6、add db
        SxTriggersDO sxDo=new SxTriggersDO();
        sxDo.setJOB_NAME(jobName);
        sxDo.setJOB_GROUP(jobGroup);
        sxDo.setDESCRIPTION(description);
        sxDo.setCRON_EXPRESSION(cronExpression);
        sxDo.setREQUEST_URL(url);
        sxDo.setREQUEST_BODY(body);
        sxDo.setREMARK("直接执行任务");
        sxDo.setCREATE_DATE(new Date());
        sxTriggersDao.save(sxDo);
        
        log.info(">>>>>>>>>>> addJob success, jobDetail:{}, cronTrigger:{}, date:{}", jobDetail, cronTrigger, date);
    }
	
	@RequestMapping(value = "/updateJob", method = RequestMethod.GET)
	public boolean updateJobCron(@RequestParam String jobGroup, @RequestParam String jobName, @RequestParam(required = false)String cronExpression,
			@RequestParam(required = false) String url,
			 @RequestParam(required = false) String body,@RequestParam(required = false) String description) throws SchedulerException {

        // 1、job key
//        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
//        Scheduler scheduler = JobScheduler.schedulerFactory.getScheduler();
//        // 2、valid
//        if (!scheduler.checkExists(triggerKey)) {
//            return true;    // PASS
//        }
//
//        CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
//
//        // 3、avoid repeat cron
//        String oldCron = oldTrigger.getCronExpression();
//        if (oldCron.equals(cronExpression)){
//            return true;    // PASS
//        }
//
//        // 4、new cron trigger
//        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
//        oldTrigger = oldTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
//
//        // 5、rescheduleJob
//        scheduler.rescheduleJob(triggerKey, oldTrigger);
		Long id = sxTriggersDao.findOne(jobName);
		if(null != url) {
			SxTriggersDO sx = sxTriggersDao.findById(id).get();
			jobName = sx.getJOB_NAME();
			jobGroup = sx.getJOB_GROUP();
			cronExpression = sx.getCRON_EXPRESSION();
			url = sx.getREQUEST_URL();
			body = sx.getREQUEST_BODY();
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
	        Scheduler scheduler = JobScheduler.schedulerFactory.getScheduler();
	        // 2、del
	        if (scheduler.checkExists(triggerKey)) {
	        	scheduler.unscheduleJob(triggerKey); 
	        }
			JobKey jobKey = new JobKey(jobName, jobGroup);
	        // 3、corn trigger
	        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();   // withMisfireHandlingInstructionDoNothing 忽略掉调度终止过程中忽略的调度
	        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

	        // 4、job detail
	        JobDetail jobDetail = JobBuilder.newJob(MyJob.class).usingJobData(Param.REQUEST_URL, url).
	        		usingJobData(Param.REQUEST_BODY, body).withIdentity(jobKey).build();
	        
	        // 5、schedule job
	        Date date = scheduler.scheduleJob(jobDetail, cronTrigger);
	        log.info(">>>>>>>>>>> resumeJob success, JobGroup:{}, JobName:{}, date:{}", jobGroup, jobName, date);
		}
        sxTriggersDao.updateOne(jobName, description, cronExpression, url, body, "直接执行任务", new Date(), id);
        return true;
    }
	
	@RequestMapping(value = "/removeJob", method = RequestMethod.GET)
	public static boolean removeJob(@RequestParam String jobName, @RequestParam String jobGroup) throws SchedulerException {

        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        Scheduler scheduler = JobScheduler.schedulerFactory.getScheduler();
        if (scheduler.checkExists(triggerKey)) {
            scheduler.unscheduleJob(triggerKey);    // trigger + job
        }

        log.info(">>>>>>>>>>> removeJob success, triggerKey:{}", triggerKey);
        return true;
    }
	
	
	@RequestMapping(value = "/startJob", method = RequestMethod.GET)
	public void startJob(@RequestParam String jobName) throws SchedulerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Long id = sxTriggersDao.findOne(jobName);
		SxTriggersDO sx = sxTriggersDao.findById(id).get();
		jobName = sx.getJOB_NAME();
		String jobGroup = sx.getJOB_GROUP();
		String cronExpression = sx.getCRON_EXPRESSION();
		String url = sx.getREQUEST_URL();
		String body = sx.getREQUEST_BODY();
		TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        Scheduler scheduler = JobScheduler.schedulerFactory.getScheduler();
        // 2、del
        if (scheduler.checkExists(triggerKey)) {
        	scheduler.unscheduleJob(triggerKey); 
        }
		JobKey jobKey = new JobKey(jobName, jobGroup);
        // 3、corn trigger
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();   // withMisfireHandlingInstructionDoNothing 忽略掉调度终止过程中忽略的调度
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

        // 4、job detail
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class).usingJobData(Param.REQUEST_URL, url).
        		usingJobData(Param.REQUEST_BODY, body).withIdentity(jobKey).build();
        
        // 5、schedule job
        Date date = scheduler.scheduleJob(jobDetail, cronTrigger);
        log.info(">>>>>>>>>>> startJob success, jobName:{},date:{}", jobName,date);
    }
	
	@RequestMapping(value = "/stopJob", method = RequestMethod.GET)
	public boolean stopJob(@RequestParam String jobName, @RequestParam String jobGroup) throws SchedulerException {

        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        Scheduler scheduler = JobScheduler.schedulerFactory.getScheduler();
        if (scheduler.checkExists(triggerKey)) {
            scheduler.unscheduleJob(triggerKey);    // trigger + job
        }
        log.info(">>>>>>>>>>> stopJob success, triggerKey:{}", triggerKey);
        return true;
    }
	
	@RequestMapping(value = "/getDistinctJobList", method = RequestMethod.GET)
	public List<String> getDistinctJobList() {
		List<String> groupList = sxTriggersDao.findDistinctJobList();
        return groupList;
    }
	
	@RequestMapping(value = "/getDistinctGroupList", method = RequestMethod.GET)
	public List<String> getDistinctGroupList() {
		List<String> groupList = sxTriggersDao.findDistinctGroupList();
        return groupList;
    }
	
	@RequestMapping(value = "/getJobByGroupNameList", method = RequestMethod.GET)
	public List<String> getJobByGroupNameList(@RequestParam String groupName) {
		List<String> jobList = sxTriggersDao.findJobByGroupNameList(groupName);
        return jobList;
    }
	
	@RequestMapping(value = "/queryJobTable/{jobName}/{groupName}", method = RequestMethod.GET)
	public ResponseResult<ResultPage<List<SxTriggersDO>>> queryTable(@PathVariable("jobName") String jobName,@PathVariable("groupName") String groupName,
			@RequestParam Integer pageNum, @RequestParam Integer pageSize,@RequestParam(value = "sort", required = false) String sort){
		List<SxTriggersDO> list = Lists.newArrayListWithCapacity(10);
		if(null!=jobName && null!=groupName) {
			list = sxTriggersDao.findByJobList(jobName, groupName,pageNum,pageSize);
		}else if(null!=jobName) {
			list = sxTriggersDao.findByJobList(jobName,pageNum,pageSize);
		}else if(null!=groupName) {
			list = sxTriggersDao.findByGroupList(jobName,pageNum,pageSize);
		}
		ResultPage<List<SxTriggersDO>> resPage = new ResultPage<>();
		resPage.setData(list);
		ResponseResult<ResultPage<List<SxTriggersDO>>> result = new ResponseResult<>();
		result.setData(resPage);
		return result;
	}
	
	@RequestMapping(value = "/jobTableParamList/{jobName}/{groupName}", method = RequestMethod.GET)
	public List<String> queryTableParamList(@PathVariable("jobName") String jobName,@PathVariable("groupName") String groupName){
		List<String> list = Lists.newArrayListWithCapacity(0);
		list.add("主键");
		list.add("任务名");
		list.add("任务组");
		list.add("请求地址");
		list.add("请求参数");
		list.add("任务描述");
		list.add("备注");
		list.add("创建人");
		list.add("创建时间");
		return list;
	}
	
	
}
