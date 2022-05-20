package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plms.ManagementService.repository.entity.Class;

@Repository
public interface ClassRepository extends JpaRepository<Class,Integer> {
}
