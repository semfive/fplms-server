package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import plms.ManagementService.repository.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query(nativeQuery = true, value = "SELECT number FROM `GROUP` WHERE id = (SELECT GROUP_id FROM STUDENT_GROUP WHERE STUDENT_id = ?1 AND GROUP_id = ?2)")
    Integer findGroupByStudentIdAndClassId(Integer studentId, Integer classId);
    
    @Query(nativeQuery = true, value = "select id from STUDENT where email = ?1")
    Integer getStudentIdByEmail(String email);
    
    @Query(nativeQuery = true, value = "select STUDENT_id from STUDENT_GROUP where GROUP_id = ?2 and CLASS_id = ?1 order by vote desc limit 1")
    Integer getGroupLeaderByClassIdAndGroupId(Integer classId, Integer groupId);

    Student findOneById(Integer studentId);
    
    Student findOneByEmail(String email);
}
