package com.platform.job.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
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
	public boolean addJob(@RequestParam String jobName, @RequestParam String jobGroup, 
			 @RequestParam String cronExpression,@RequestParam String url,
			 @RequestParam(required = false) String body,@RequestParam(required = false) String description,
			 @RequestParam(required = false) String remark,@RequestParam(required = false) String createUser) throws SchedulerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    	// 1、job key
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);

        Scheduler scheduler = JobScheduler.schedulerFactory.getScheduler();
        // 2、valid
        if (scheduler.checkExists(triggerKey)) {
            return false;    // PASS
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
        sxDo.setDESCRIPTION(StringUtils.isEmpty(description)?null:description);
        sxDo.setCRON_EXPRESSION(cronExpression);
        sxDo.setREQUEST_URL(url);
        sxDo.setREQUEST_BODY(StringUtils.isEmpty(body)?null:body);
        sxDo.setREMARK(StringUtils.isEmpty(remark)?null:remark);
        sxDo.setCREATE_USER(StringUtils.isEmpty(createUser)?null:createUser);
        sxDo.setCREATE_DATE(new Date());
        sxTriggersDao.save(sxDo);
        
        log.info(">>>>>>>>>>> addJob success, jobDetail:{}, cronTrigger:{}, date:{}", jobDetail, cronTrigger, date);
        return true; 
    }
	
	@RequestMapping(value = "/updateJob", method = RequestMethod.GET)
	public boolean updateJobCron(@RequestParam String jobGroup, @RequestParam String jobName, @RequestParam(required = false)String cronExpression,
			@RequestParam(required = false) String url,@RequestParam(required = false) String body,@RequestParam(required = false) String description,
			@RequestParam(required = false) String remark,@RequestParam(required = false) String modifyUser) throws SchedulerException {
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
		description = StringUtils.isEmpty(description)?null:description;
		body = StringUtils.isEmpty(body)?null:body;
		remark = StringUtils.isEmpty(remark)?null:remark;
		modifyUser = StringUtils.isEmpty(modifyUser)?null:modifyUser;
        sxTriggersDao.updateOne(jobName, description, cronExpression, url, body, remark, new Date(),modifyUser, id);
        return true;
    }
	
	@RequestMapping(value = "/updateJobData", method = RequestMethod.GET)
	public boolean updateJobData(@RequestParam String jobGroup, @RequestParam String jobName, @RequestParam(required = false)String cronExpression,
			@RequestParam(required = false) String url,@RequestParam(required = false) String body,@RequestParam(required = false) String description,
			@RequestParam(required = false) String remark,@RequestParam(required = false) String modifyUser) throws SchedulerException {
		Long id = sxTriggersDao.findOne(jobName);
		if(null == id) {
			return false;
		}
		description = StringUtils.isEmpty(description)?null:description;
		body = StringUtils.isEmpty(body)?null:body;
		remark = StringUtils.isEmpty(remark)?null:remark;
		modifyUser = StringUtils.isEmpty(modifyUser)?null:modifyUser;
        sxTriggersDao.updateOne(jobName, description, cronExpression, url, body, remark, new Date(),modifyUser, id);
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
	public boolean startJob(@RequestParam String id) throws SchedulerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Optional<SxTriggersDO> opt = sxTriggersDao.findById(Long.parseLong(id));
		SxTriggersDO dto= opt.get();
		String jobName = dto.getJOB_NAME();
		String jobGroup = dto.getJOB_GROUP();
		String cronExpression = dto.getCRON_EXPRESSION();
		String url = dto.getREQUEST_URL();
		String body = dto.getREQUEST_BODY();
		
		// 1、job key
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);

        Scheduler scheduler = JobScheduler.schedulerFactory.getScheduler();
        // 2、valid
        if (scheduler.checkExists(triggerKey)) {
            return false;    // PASS
        }

        // 3、corn trigger
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();   // withMisfireHandlingInstructionDoNothing 忽略掉调度终止过程中忽略的调度
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

        // 4、job detail
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class).usingJobData(Param.REQUEST_URL, url).
        		usingJobData(Param.REQUEST_BODY, body).withIdentity(jobKey).build();
        
        // 5、schedule job
        Date date = scheduler.scheduleJob(jobDetail, cronTrigger);
        log.info(">>>>>>>>>>> startJob success, jobDetail:{}, cronTrigger:{}, date:{}", jobDetail, cronTrigger, date);
		return true;
    }
	
	@RequestMapping(value = "/stopJob", method = RequestMethod.GET)
	public boolean stopJob(@RequestParam String id) throws SchedulerException {
		Optional<SxTriggersDO> opt = sxTriggersDao.findById(Long.parseLong(id));
		SxTriggersDO dto= opt.get();
		boolean bool = removeJob(dto.getJOB_NAME(),dto.getJOB_GROUP());
		if(!bool) {
			return false;
		}
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
	
	@RequestMapping(value = "/getJobById", method = RequestMethod.GET)
	public Object getJobById(@RequestParam String id) {
        return sxTriggersDao.findOneById(id);
    }
	
	@RequestMapping(value = "/getJobNameExist", method = RequestMethod.GET)
	public boolean getJobNameExist(@RequestParam String jobName) {
		Long id = sxTriggersDao.findOne(jobName);
		if(null != id) {
			return true;
		}
		return false;
    }
	
	
	@RequestMapping(value = "/delJobById", method = RequestMethod.GET)
	public boolean delJobById(@RequestParam String id) throws SchedulerException {
		Optional<SxTriggersDO> opt = sxTriggersDao.findById(Long.parseLong(id));
		SxTriggersDO dto= opt.get();
		boolean bool = removeJob(dto.getJOB_NAME(),dto.getJOB_GROUP());
		if(!bool) {
			return false;
		}
		sxTriggersDao.deleteById(Long.parseLong(id));
        return true;
    }
	
	@RequestMapping(value = "/queryJobTable", method = RequestMethod.GET)
	public ResponseResult<ResultPage<List<Object[]>>> queryTable(@RequestParam(value="jobName", required = false) String jobName,@RequestParam(value="groupName",required = false) String groupName,
			@RequestParam Integer pageNum, @RequestParam Integer pageSize,@RequestParam(value = "sort", required = false) String sort){
		List<Object[]> li = Lists.newArrayListWithCapacity(10);
		Page<Object[]> list = null;
//		SxTriggersDO dto=new SxTriggersDO();
//		dto.setJOB_NAME(jobName);
//		dto.setJOB_GROUP(groupName);
//		ExampleMatcher matcher = ExampleMatcher.matching();
//		Example<SxTriggersDO> ex = Example.of(dto, matcher); 
		Pageable pageable = PageRequest.of(pageNum-1, pageSize);
		if(!StringUtils.isEmpty(jobName) && !StringUtils.isEmpty(groupName)) {
//			list = sxTriggersDao.findAll(ex,pageable);
			list = sxTriggersDao.findByJobList(jobName,groupName,pageable);
			li = list.getContent();
		}else if(!StringUtils.isEmpty(jobName)) {
			list = sxTriggersDao.findByJobList(jobName,pageable);
			li = list.getContent();
		}else if(!StringUtils.isEmpty(groupName)) {
			list = sxTriggersDao.findByGroupList(groupName,pageable);
			li = list.getContent();
		}else {
			list = sxTriggersDao.findByJobList(pageable);
			li = list.getContent();
		}
		ResultPage<List<Object[]>> resPage = new ResultPage<>();
		resPage.setData(li);
		ResponseResult<ResultPage<List<Object[]>>> result = new ResponseResult<>();
		result.setData(resPage);
		return result;
	}
	
	@RequestMapping(value = "/jobTableParamList", method = RequestMethod.GET)
	public List<String> queryTableParamList(){
		List<String> list = Lists.newArrayListWithCapacity(0);
		list.add("主键");
		list.add("任务名");
		list.add("任务组");
		list.add("请求地址");
		list.add("请求参数");
		list.add("表达式");
		list.add("任务描述");
		list.add("备注");
		list.add("创建人");
		list.add("创建时间");
		list.add("操作");
		return list;
	}
	
	
}
