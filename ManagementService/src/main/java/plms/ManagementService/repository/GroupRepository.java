package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import plms.ManagementService.repository.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group,Integer> {
	
	@Query(nativeQuery = true, value = "select GROUP_id from STUDENT_GROUP where STUDENT_id = ?1 and CLASS_id = ?2")
    Integer findGroupByStudentIdAndClassId(Integer studentId, Integer classId);
	
	@Query(nativeQuery = true, value = "select id from `GROUP` where id = ?1 and CLASS_id = ?2")
	Integer isGroupExistsInClass(Integer groupId, Integer classId);
	
	
	@Query(nativeQuery = true, value = "select member_quantity from `group` where id = ?1")
	Integer getGroupLimitNumber(Integer groupId);
	
}
