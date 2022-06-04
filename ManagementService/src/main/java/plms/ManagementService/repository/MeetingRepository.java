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
	Set<Meeting> findbyGroupIdAndTimeFilter(Integer groupId, Timestamp startDate, Timestamp endDate);
}
