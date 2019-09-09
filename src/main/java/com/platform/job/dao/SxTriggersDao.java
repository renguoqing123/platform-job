package com.platform.job.dao;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.platform.job.bean.SxTriggersDO;

@Repository
public interface SxTriggersDao extends JpaRepository<SxTriggersDO, Long>{

	@Transactional
	@Modifying  
	@Query(value = "UPDATE SX_TRIGGERS SET JOB_NAME=?1,DESCRIPTION=?2,CRON_EXPRESSION=?3,REQUEST_URL=?4,REQUEST_BODY=?5,REMARK=?6,MODIFY_DATE=?7,MODIFY_USER=?8 WHERE id=?9 ", nativeQuery = true)  
	public void updateOne(String jobName, String description, String cronExpression, String url, String body, String remark, Date modifyDate,String modifyUser, Long id); 
	
	@Transactional
	@Modifying  
	@Query(value = "UPDATE SX_TRIGGERS SET JOB_STATUS=?1,MODIFY_DATE=?2,MODIFY_USER=?3 WHERE id=?4 ", nativeQuery = true)  
	public void updateOneJobStatus(Boolean jobStatus, Date modifyDate,String modifyUser, Long id); 
	
	@Query(value = "SELECT ID FROM SX_TRIGGERS WHERE JOB_NAME=?1 ", nativeQuery = true)
	public Long findOne(String jobName);
	
	@Query(value = "SELECT JOB_NAME FROM SX_TRIGGERS WHERE JOB_GROUP=?1 ", nativeQuery = true)
	public List<String> findJobByGroupNameList(String groupName);
	
	@Query(value = "SELECT DISTINCT JOB_GROUP FROM SX_TRIGGERS ", nativeQuery = true)
	public List<String> findDistinctGroupList();
	
	@Query(value = "SELECT DISTINCT JOB_NAME FROM SX_TRIGGERS ", nativeQuery = true)
	public List<String> findDistinctJobList();
	
	@Query(value = "SELECT ID,JOB_NAME,JOB_GROUP,REQUEST_URL,REQUEST_BODY,CRON_EXPRESSION,DESCRIPTION,REMARK,CREATE_USER,CREATE_DATE "
			+ "FROM SX_TRIGGERS WHERE ID=?1 ", nativeQuery = true)
	public Object findOneById(String id);
	
	@Query(value = "SELECT ID,JOB_NAME,JOB_GROUP,REQUEST_URL,REQUEST_BODY,CRON_EXPRESSION,DESCRIPTION,JOB_STATUS,REMARK,CREATE_USER,CREATE_DATE "
			+ "FROM SX_TRIGGERS WHERE JOB_GROUP=:groupName", nativeQuery = true)
	public Page<Object[]> findByGroupList(@Param("groupName") String groupName,Pageable pageable);
	
	@Query(value = "SELECT ID,JOB_NAME,JOB_GROUP,REQUEST_URL,REQUEST_BODY,CRON_EXPRESSION,DESCRIPTION,JOB_STATUS,REMARK,CREATE_USER,CREATE_DATE "
			+ "FROM SX_TRIGGERS WHERE JOB_NAME=:jobName", nativeQuery = true)
	public Page<Object[]> findByJobList(@Param("jobName") String jobName,Pageable pageable);
	
	@Query(value = "SELECT ID,JOB_NAME,JOB_GROUP,REQUEST_URL,REQUEST_BODY,CRON_EXPRESSION,DESCRIPTION,JOB_STATUS,REMARK,CREATE_USER,CREATE_DATE "
			+ "FROM SX_TRIGGERS WHERE JOB_NAME=:jobName AND JOB_GROUP=:groupName", nativeQuery = true)
	public Page<Object[]> findByJobList(@Param("jobName") String jobName,@Param("groupName") String groupName,Pageable pageable);
	
	@Query(value = "SELECT ID,JOB_NAME,JOB_GROUP,REQUEST_URL,REQUEST_BODY,CRON_EXPRESSION,DESCRIPTION,JOB_STATUS,REMARK,CREATE_USER,CREATE_DATE "
			+ "FROM SX_TRIGGERS", nativeQuery = true)
	public Page<Object[]> findByJobList(Pageable pageable);
}
