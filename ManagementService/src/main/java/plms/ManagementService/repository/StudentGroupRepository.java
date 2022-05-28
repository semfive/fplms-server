package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import plms.ManagementService.repository.entity.Group;
import plms.ManagementService.repository.entity.Student;
import plms.ManagementService.repository.entity.StudentGroup;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM STUDENT_GROUP WHERE STUDENT_id = ?1 AND CLASS_id = ?2")
    void deleteStudentInGroup(Integer studentId, Integer classId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE `STUDENT_GROUP` SET GROUP_id = (SELECT id FROM `GROUP` WHERE CLASS_id = ?2 AND number = ?3) WHERE STUDENT_id = ?1 AND CLASS_id = ?2")
    void updateStudentGroup(Integer studentId, Integer classId, Integer groupNumber);

    @Query(nativeQuery = true, value = "select count(*) from STUDENT_GROUP where GROUP_id = ?1")
    Integer getCurrentNumberOfMemberInGroup(Integer groupId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "insert into STUDENT_GROUP(STUDENT_id, GROUP_id, CLASS_id,is_leader) values (?1, ?2, ?3,?4)")
    void addStudentInGroup(Integer studentId, Integer groupId, Integer classId, Integer isLeader);

    @Query(nativeQuery = true, value = "SELECT is_leader FROM STUDENT_GROUP WHERE STUDENT_id = ?1 AND CLASS_id = ?2")
    Integer findStudentLeaderRoleInClass(Integer studentId, Integer classId);//boolean

    Boolean existsByStudentAndGroupAndIsLeader(Student student, Group group, Boolean isLeader);
    
    @Query(nativeQuery = true, value = "select STUDENT_id from STUDENT_GROUP where GROUP_id = ?1 and is_leader = 1")
    Integer findLeaderInGroup(Integer groupId);
    
    @Query(nativeQuery = true, value = "select STUDENT_id from STUDENT_GROUP where GROUP_id = ?1 and STUDENT_id = ?2")
    Integer isStudentExistInGroup(Integer groupId, Integer studenId);
    
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update STUDENT_GROUP set is_leader = ?3 where GROUP_id = ?1 and STUDENT_id = ?2")
    void updateGroupLeader(Integer groupId, Integer studentId, Integer isLeader);

    @Query(nativeQuery = true, value = "select STUDENT_id from STUDENT_GROUP where GROUP_id = ?1 and is_leader = 0 limit 1")
    Integer chooseRandomGroupMember(Integer groupId);
 
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update STUDENT_GROUP set is_leader = 1 where GROUP_id = ?1 and STUDENT_id = ?2")
    void addRandomGroupLeader(Integer groupId, Integer leaderId);
    
}
