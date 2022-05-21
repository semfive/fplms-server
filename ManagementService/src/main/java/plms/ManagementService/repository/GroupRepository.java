package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plms.ManagementService.repository.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group,Integer> {
}
