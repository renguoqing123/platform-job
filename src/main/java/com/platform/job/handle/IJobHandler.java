package com.platform.job.handle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import lombok.extern.slf4j.Slf4j;

/**
 * job handler
 *
 */
@Slf4j
public abstract class IJobHandler implements Job{

	/**
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public abstract void execute(String param) throws Exception;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String printTime = new SimpleDateFormat("yy-MM-dd HH-mm-ss").format(new Date());
		log.info("MyJob start at:" + printTime + ", prints: Hello Job-" + new Random().nextInt(100));
        System.out.println("MyJob start at:" + printTime + ", prints: Hello Job-" + new Random().nextInt(100));
        try {
        	JobKey key = context.getJobDetail().getKey();
            System.out.println("My Job name and group are:" + key.getName() + ":" + key.getGroup());
            TriggerKey trkey = context.getTrigger().getKey();
            System.out.println("My Trigger name and group are:" + trkey.getName() + ":" + trkey.getGroup());
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            JobDataMap tdataMap = context.getTrigger().getJobDataMap();
            log.info("dataMap:{}", dataMap);
            log.info("tdataMap:{}", tdataMap);
            String name = key.getName();
			execute(name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
