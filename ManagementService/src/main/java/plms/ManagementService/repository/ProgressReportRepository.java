package plms.ManagementService.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import plms.ManagementService.repository.entity.Group;
import plms.ManagementService.repository.entity.ProgressReport;

@Repository
public interface ProgressReportRepository extends JpaRepository<ProgressReport, Integer> {
	Set<ProgressReport> findByGroup(Group group);

	@Query(nativeQuery = true, value = "select id from PROGRESS_REPORT where id = ?2 and GROUP_id = ?1 and STUDENT_id = ?3")
	Integer existsByIdAndGroupIdAndStudentId(Integer groupId, Integer reportId, Integer studentId);
}
