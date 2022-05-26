package plms.ManagementService.repository;

import java.util.Set;

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

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "insert into STUDENT_CLASS(STUDENT_id, CLASS_id) values (?1, ?2)")
    void insertStudentInClass(Integer studentId, Integer classId);

    @Query(nativeQuery = true, value = "select enroll_key from CLASS where id = ?1")
    public String getClassEnrollKey(Integer classId);

    @Query(nativeQuery = true, value = "select * from CLASS where name like ?1")
    public Set<Class> getClassBySearchStr(String search);

}
