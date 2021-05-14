package com.platform.job.config;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.platform.job.api.JobApi;
import com.platform.job.model.JobDataReq;
import com.platform.job.util.ResponseResult;

@Component
public class JobUtil {
	
	private static JobUtil jobUtil = new JobUtil();
	
	@Autowired
	public JobApi api;
	
	public static JobUtil getInstance() {
		return jobUtil;
	}
	
	public JobUtil() {}
	
	public void addJob(JobDataReq req){
		try {
			api.addJob(req);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void updateJob(JobDataReq req){
		try {
			api.updateJob(req);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void startJob(String id){
		try {
			api.startJob(id);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void stopJob(String id){
		try {
			api.stopJob(id);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public ResponseResult<Long> findJob(String jobName, String groupName){
		return api.findJob(jobName, groupName);
	}
	
}
