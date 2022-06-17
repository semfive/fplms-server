package plms.ManagementService.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plms.ManagementService.repository.entity.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
	boolean existsByName(String name);
}
