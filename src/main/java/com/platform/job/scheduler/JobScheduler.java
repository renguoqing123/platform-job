package com.platform.job.scheduler;

import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class JobScheduler{
	// 1、创建调度器Scheduler
	public static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
}
