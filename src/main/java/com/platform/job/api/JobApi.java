package com.platform.job.api;

import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.platform.job.model.JobDataReq;

public interface JobApi {
	
	@RequestMapping(value = "/addJob", method = RequestMethod.POST)
	public boolean addJob(@RequestBody JobDataReq req) throws SchedulerException, ClassNotFoundException, InstantiationException, IllegalAccessException;
	
}
