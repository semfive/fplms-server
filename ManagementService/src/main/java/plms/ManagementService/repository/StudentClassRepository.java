package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import plms.ManagementService.repository.compositePrimaryKey.StudentClassId;
import plms.ManagementService.repository.entity.StudentClass;

public interface StudentClassRepository extends JpaRepository<StudentClass, StudentClassId> {
	@Modifying
    @Transactional
	@Query(nativeQuery = true, value = "insert into STUDENT_CLASS(STUDENT_id, CLASS_id) values (?2, ?1)")
	public void insertClassIdAndStudentId(Integer classId, Integer studentId);
}
