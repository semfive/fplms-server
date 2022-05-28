package plms.ManagementService.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import plms.ManagementService.repository.entity.Project;
import plms.ManagementService.repository.entity.Class;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer> {
	Set<Project> findByClassEntity(Class classEntity);
	
    @Query(nativeQuery = true, value = "select id from `PROJECT` where id = ?1 and CLASS_id = ?2")
    Integer isProjectExistsInClass(Integer projectId, Integer classId);

}
