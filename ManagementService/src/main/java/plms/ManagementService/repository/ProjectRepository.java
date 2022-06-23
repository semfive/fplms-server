package plms.ManagementService.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import plms.ManagementService.repository.entity.Project;
import plms.ManagementService.repository.entity.Subject;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Integer> {
	
	@Query(nativeQuery = true, value = "select * from PROJECT where id = ?2 and LECTURER_id = ?1 and is_disable = 0")
	Integer existsByLecturerId(Integer lecturerId, Integer projectId);
	
	@Query(nativeQuery = true, value = "select * from PROJECT where SUBJECT_id = ?1 and LECTURER_id = ?2 and is_disable = 0")
	Set<Project> findBySubjectIdAndLecturerId(Integer subjectId, Integer lecturerId); 
	
	@Query(nativeQuery = true, value = "select * from PROJECT where LECTURER_id = ?1 and is_disable = 0")
	Set<Project> findByLecturerId(Integer lecturerId); 

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update PROJECT set is_disable = 1 where id = ?1")
    void deleteProject(Integer projectId);
}
