package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import plms.ManagementService.repository.entity.Class;

@Repository
public interface ClassRepository extends JpaRepository<Class, Integer> {
    Class findOneById(Integer classId);

    @Query(nativeQuery = true, value = "SELECT STUDENT_id FROM STUDENT_CLASS WHERE STUDENT_id = ?1 AND CLASS_id = ?2")
    Integer existsInClass(Integer studentId, Integer classId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM STUDENT_CLASS WHERE STUDENT_id = ?1 AND CLASS_id = ?2")
    void deleteStudentInClass(Integer studentId, Integer classId);

}
