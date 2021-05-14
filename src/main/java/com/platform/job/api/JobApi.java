package com.platform.job.api;

import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.platform.job.model.JobDataReq;
import com.platform.job.util.ResponseResult;

public interface JobApi {
	
	@RequestMapping(value = "/addJob", method = RequestMethod.POST)
	public boolean addJob(@RequestBody JobDataReq req) throws SchedulerException, ClassNotFoundException, InstantiationException, IllegalAccessException;
	
	@RequestMapping(value = "/updateJob", method = RequestMethod.POST)
	public boolean updateJobData(@RequestBody JobDataReq req) throws SchedulerException;
	
	@RequestMapping(value = "/startJob", method = RequestMethod.GET)
	public boolean startJob(@RequestParam String id);
	
	@RequestMapping(value = "/stopJob", method = RequestMethod.GET)
	public boolean stopJob(@RequestParam String id) throws SchedulerException;
	
	@RequestMapping(value = "/findJob", method = RequestMethod.GET)
	public ResponseResult<Long> findJob(@RequestParam(value = "jobName") String jobName,
			@RequestParam(value = "groupName") String groupName);
}
