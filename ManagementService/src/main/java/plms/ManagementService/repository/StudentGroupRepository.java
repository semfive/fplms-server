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
public interface StudentGroupRepository extends JpaRepository<StudentGroup,Integer> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM STUDENT_GROUP WHERE STUDENT_id = ?1 AND CLASS_id = ?2")
    void deleteStudentInGroup(Integer studentId, Integer classId);
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE `STUDENT_GROUP` SET GROUP_id = (SELECT id FROM `GROUP` WHERE CLASS_id = ?2 AND number = ?3) WHERE STUDENT_id = ?1 AND CLASS_id = ?2")
    void updateStudentGroup(Integer studentId, Integer classId, Integer groupNumber);
}
