package plms.ManagementService.repository;

import java.sql.Timestamp;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import plms.ManagementService.repository.entity.CycleReport;
import plms.ManagementService.repository.entity.Group;
@Repository
public interface CycleReportRepository extends JpaRepository<CycleReport, Integer> {
	Set<CycleReport> findByGroup(Group group);
	
	@Query(nativeQuery = true, value = "select id from CYCLE_REPORT where id = ?2 and GROUP_id = ?1")
	Integer existsByIdAndGroupId(Integer groupId, Integer reportId);
	
	@Query(nativeQuery = true, value = "select * from CYCLE_REPORT where GROUP_id = ?1 and report_time >= ?2 and report_time <= ?3")
	Set<CycleReport> findByGroupIdAndTimeFilter(Integer groupId, Timestamp startDate, Timestamp endDate);
}
