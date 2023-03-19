package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import plms.ManagementService.repository.entity.Group;
import plms.ManagementService.repository.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM `GROUP` WHERE id = (SELECT GROUP_id FROM STUDENT_GROUP WHERE STUDENT_id = ?1 AND CLASS_id = ?2)")
    Group findGroupByStudentIdAndClassId(Integer studentId, Integer classId);

    @Query(nativeQuery = true, value = "select id from STUDENT where email = ?1")
    Integer findStudentIdByEmail(String email);


    Student findOneById(Integer studentId);

    Boolean existsByEmail(String email);

}
