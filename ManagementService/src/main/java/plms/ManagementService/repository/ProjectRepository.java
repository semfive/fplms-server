package plms.ManagementService.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import plms.ManagementService.repository.entity.Project;
import plms.ManagementService.repository.entity.Subject;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer> {
	Set<Project> findBySubject(Subject subject);
	
    @Query(nativeQuery = true, value = "select id from PROJECT where id = ?1 and SUBJECT_id = (select SUBJECT_id from CLASS where id = ?2)")
    Integer isProjectExistsInClass(Integer projectId, Integer classId);


}
