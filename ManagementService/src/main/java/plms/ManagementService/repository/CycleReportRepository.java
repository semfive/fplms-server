package plms.ManagementService.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import plms.ManagementService.repository.entity.CycleReport;
import plms.ManagementService.repository.entity.Group;
@Repository
public interface CycleReportRepository extends JpaRepository<CycleReport, Integer> {
	Set<CycleReport> findByGroup(Group group);
}
