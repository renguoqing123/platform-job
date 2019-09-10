package com.platform.job.core;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.util.StringUtils;

import com.platform.job.constants.Param;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobKey key = context.getJobDetail().getKey();
		log.info("============job任务开始执行===========");
		log.info("任务名称 :{},任务组:{}" , key.getName() , key.getGroup());
        TriggerKey trkey = context.getTrigger().getKey();
        log.info("触发器名称 {}, 触发器组 {}:" , trkey.getName() , trkey.getGroup());
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
//        JobDataMap tdataMap = context.getTrigger().getJobDataMap();
        log.info("解析入参:{}", dataMap);
//        log.info("tdataMap:{}", tdataMap);
        Object url = dataMap.get(Param.REQUEST_URL);
        Object body = dataMap.get(Param.REQUEST_BODY);
        log.info("请求地址:{}", url);
        if(!StringUtils.isEmpty(body)) {
        	log.info("请求报文:{}", body);
        	String result = HttpClientUtil.doPostJson(url.toString(), body.toString());
        	log.info("返回结果:{}", result);
        }else if(!StringUtils.isEmpty(url)) {
        	String result = HttpClientUtil.doGet(url.toString());
        	log.info("返回结果:{}", result);
        }
        log.info("============job任务结束执行===========");
	}

}
