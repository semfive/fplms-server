package plms.ManagementService.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import plms.ManagementService.repository.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {

    Group findOneById(Integer groupId);

    @Query(nativeQuery = true, value = "select GROUP_id from STUDENT_GROUP where STUDENT_id = ?1 and CLASS_id = ?2")
    Integer findGroupByStudentIdAndClassId(Integer studentId, Integer classId);

    @Query(nativeQuery = true, value = "select id from `GROUP` where id = ?1 and CLASS_id = ?2")
    Integer isGroupExistsInClass(Integer groupId, Integer classId);
    
    @Query(nativeQuery = true, value = "select member_quantity from `group` where id = ?1")
    Integer getGroupLimitNumber(Integer groupId);

    @Query(nativeQuery = true, value = "select max(`number`) FROM `GROUP` where `CLASS_id` = ?1")
    Integer getMaxGroupNumber(Integer classId);
    
    @Modifying
	@Transactional
	@Query(nativeQuery = true, value = "update `GROUP` set PROJECT_id = ?2 where id = ?1")
	void updateProjectinGroup(Integer groupId, Integer projectId);
    
    @Query(nativeQuery = true, value = "select id from `GROUP` where id = ?1 and enroll_time > ?2")
    Integer isEnrollTimeOver(Integer groupId, Timestamp currentTime);
    

}