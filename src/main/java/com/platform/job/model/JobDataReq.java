package com.platform.job.model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class JobDataReq implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5267298578296832862L;
	private String jobName;
	private String jobGroup;
	private String url;
	private String body;
	private String cronExpression;
	private String description;
	private String remark;
	private String createUser;
	private String modifyUser;
}
