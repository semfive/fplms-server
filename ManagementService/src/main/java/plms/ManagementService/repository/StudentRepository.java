package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import plms.ManagementService.repository.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student,Integer> {

    @Query(nativeQuery = true, value = "SELECT number FROM `GROUP` WHERE id = (SELECT GROUP_id FROM STUDENT_GROUP WHERE STUDENT_id = ?1 AND GROUP_id = ?2)")
    Integer findGroupByStudentIdAndClassId(Integer studentId, Integer classId);
}
