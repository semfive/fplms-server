package plms.ManagementService.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import plms.ManagementService.repository.entity.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, String>{
	@Query(nativeQuery = true, value = "select * from SEMESTER where code like ?1")
	public Set<Semester> getSemester(String code);
}
