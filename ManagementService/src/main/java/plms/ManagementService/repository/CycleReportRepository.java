package plms.ManagementService.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import plms.ManagementService.repository.entity.CycleReport;
import plms.ManagementService.repository.entity.Group;

@Repository
public interface CycleReportRepository extends JpaRepository<CycleReport, Integer> {
    Set<CycleReport> findByGroup(Group group);

    @Query(nativeQuery = true, value = "select id from CYCLE_REPORT where id = ?2 and GROUP_id = ?1")
    Integer existsByIdAndGroupId(Integer groupId, Integer reportId);

    Boolean existsByGroupAndCycleNumber(Group group, Integer cycleNumber);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update CYCLE_REPORT set feedback = ?2,mark = ?3 where id = ?1")
    void addFeedback(Integer reportId, String feedback, Float mark);

}
