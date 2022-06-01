package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import plms.ManagementService.repository.entity.ProgressReport;

@Repository
public interface ProgressReportRepository extends JpaRepository<ProgressReport, Integer> {

}
