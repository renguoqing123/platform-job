package com.platform.job.dao;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.platform.job.bean.SxTriggersDO;

@Repository
public interface SxTriggersDao extends JpaRepository<SxTriggersDO, Long>{

	@Transactional
	@Modifying  
	@Query(value = "UPDATE SX_TRIGGERS SET JOB_NAME=?1,DESCRIPTION=?2,CRON_EXPRESSION=?3,REQUEST_URL=?4,REQUEST_BODY=?5,REMARK=?6,MODIFY_DATE=?7 WHERE id=?8 ", nativeQuery = true)  
	public void updateOne(String jobName, String description, String cronExpression, String url, String body, String remark, Date modifyDate, Long id); 
	
	@Query(value = "SELECT ID FROM SX_TRIGGERS WHERE JOB_NAME=?1 ", nativeQuery = true)
	public Long findOne(String jobName);
}
