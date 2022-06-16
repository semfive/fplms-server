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
	@Query(nativeQuery = true, value = "SELECT * FROM CLASS WHERE id = ?1 AND is_disable = 0")
    Class findOneById(Integer classId);

    @Query(nativeQuery = true, value = "SELECT STUDENT_id FROM STUDENT_CLASS WHERE STUDENT_id = ?1 AND CLASS_id = ?2")
    Integer existsInClass(Integer studentId, Integer classId);

    @Query(nativeQuery = true, value = "select SUBJECT_id from `CLASS` where id = ?1 and is_disable = 0")
    Integer findSubjectId(Integer classId);
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM STUDENT_CLASS WHERE STUDENT_id = ?1 AND CLASS_id = ?2")
    void deleteStudentInClass(Integer studentId, Integer classId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "insert into STUDENT_CLASS(STUDENT_id, CLASS_id) values (?1, ?2)")
    void insertStudentInClass(Integer studentId, Integer classId);

    @Query(nativeQuery = true, value = "select enroll_key from CLASS where id = ?1 and is_disable = 0")
    public String getClassEnrollKey(Integer classId);

    @Query(nativeQuery = true, value = "select * from CLASS where name like ?1 and is_disable = 0")
    public Set<Class> getClassBySearchStr(String search);

    @Query(nativeQuery = true, value = "SELECT email FROM LECTURER WHERE id = (SELECT LECTURER_id FROM CLASS WHERE id = ?1 and is_disable = 0)")
    public String findLecturerEmailOfClass(Integer classId);
    
    @Query(nativeQuery = true, value = "select semester from CLASS where id = ?1 and is_disable = 0")
    public String getClassSemester(Integer classId);

    @Query(nativeQuery = true, value = "select id from CLASS where SEMESTER_code = ?1 limit 1")
    public Integer findClassBySemester(String code);
}
