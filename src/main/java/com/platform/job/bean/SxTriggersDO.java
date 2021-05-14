package com.platform.job.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "sx_triggers")
public class SxTriggersDO {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long ID;
	@Column(length = 32)
    private String JOB_NAME;
	@Column(length = 32)
    private String JOB_GROUP;
	@Column(length = 32)
    private Date JOB_START_DATE;
	@Column(length = 32)
    private Date JOB_END_DATE;
	@Column(length = 32)
    private String DESCRIPTION;
	@Column(length = 32)
    private String CRON_EXPRESSION;
	@Column(length = 256)
    private String REQUEST_URL;
	@Column(length = 32)
    private String REQUEST_BODY;
	@Column(length = 1)
    private Boolean JOB_STATUS;
	@Column(length = 500)
    private String REMARK;
	@Column(length = 32)
    private String CREATE_USER;
	@Column(length = 32)
    private Date CREATE_DATE;
	@Column(length = 32)
    private String MODIFY_USER;
	@Column(length = 32)
    private Date MODIFY_DATE;
}
