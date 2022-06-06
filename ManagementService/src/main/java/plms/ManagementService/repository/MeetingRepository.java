package plms.ManagementService.repository;

import java.sql.Timestamp;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import plms.ManagementService.repository.entity.Group;
import plms.ManagementService.repository.entity.Meeting;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
	Set<Meeting> findByGroup(Group group);
	
	@Query(nativeQuery = true, value = "select * from MEETING where GROUP_id = ?1 and schedule_time >= ?2 and schedule_time <= ?3")
	Set<Meeting> findbyGroupId(Integer groupId, Timestamp startDate, Timestamp endDate);
	
	@Query(nativeQuery = true, value = "select * from MEETING where GROUP_id in (select id from `GROUP` where CLASS_id = ?1) and schedule_time >= ?2 and schedule_time <= ?3")
	Set<Meeting> findByClassId(Integer classId, Timestamp startDate, Timestamp endDate);
	
	@Query(nativeQuery = true, value = "select * from MEETING where LECTURER_id = ?1 and schedule_time >= ?2 and schedule_time <= ?3")
	Set<Meeting> findByLecturerId(Integer lecturerId, Timestamp startDate, Timestamp endDate);
	
	@Query(nativeQuery = true, value = "select * from MEETING where GROUP_id in (select GROUP_id from STUDENT_GROUP where STUDENT_id = ?1) and schedule_time >= ?2 and schedule_time <= ?3")
	Set<Meeting> findByStudentId(Integer studentId, Timestamp startDate, Timestamp endDate);


	
}
