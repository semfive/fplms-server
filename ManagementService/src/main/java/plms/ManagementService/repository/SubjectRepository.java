package plms.ManagementService.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import plms.ManagementService.repository.entity.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
	boolean existsByName(String name);
	
	@Query(nativeQuery = true, value = "select * from SUBJECT where name = ?1")
	Subject findByName(String subjectName);
}
