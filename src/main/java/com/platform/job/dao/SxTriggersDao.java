package com.platform.job.dao;

import java.util.Date;
import java.util.List;

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
	
	@Query(value = "SELECT JOB_NAME FROM SX_TRIGGERS WHERE JOB_GROUP=?1 ", nativeQuery = true)
	public List<String> findJobByGroupNameList(String groupName);
	
	@Query(value = "SELECT DISTINCT JOB_GROUP FROM SX_TRIGGERS ", nativeQuery = true)
	public List<String> findDistinctGroupList();
	
	@Query(value = "SELECT DISTINCT JOB_NAME FROM SX_TRIGGERS ", nativeQuery = true)
	public List<String> findDistinctJobList();
	
	@Query(value = "SELECT ID,JOB_NAME,JOB_GROUP,DESCRIPTION,CRON_EXPRESSION,REQUEST_URL,REQUEST_BODY,REMARK,CREATE_USER,CREATE_DATE,MODIFY_USER,MODIFY_DATE "
			+ "FROM SX_TRIGGERS WHERE JOB_GROUP=?1", nativeQuery = true)
	public List<String> findByGroupList(String groupName);
	
	@Query(value = "SELECT ID,JOB_NAME,JOB_GROUP,DESCRIPTION,CRON_EXPRESSION,REQUEST_URL,REQUEST_BODY,REMARK,CREATE_USER,CREATE_DATE,MODIFY_USER,MODIFY_DATE "
			+ "FROM SX_TRIGGERS WHERE JOB_NAME=?1", nativeQuery = true)
	public List<String> findByJobList(String jobName);
}
