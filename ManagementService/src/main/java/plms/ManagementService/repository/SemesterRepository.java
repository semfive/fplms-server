package plms.ManagementService.repository;

import java.sql.Date;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import plms.ManagementService.repository.entity.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, String>{
	@Query(nativeQuery = true, value = "select * from SEMESTER where code like ?1")
	public Set<Semester> getSemester(String code);
	
	@Query(nativeQuery = true, value = "select end_date from SEMESTER where code = ?1")
	public Date getSemesterEndDate(String code);
	
	@Query(nativeQuery = true, value = "select start_date from SEMESTER where code = ?1")
	public Date getSemesterStartDate(String code);

}
