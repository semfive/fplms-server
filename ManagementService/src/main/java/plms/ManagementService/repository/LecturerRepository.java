package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import plms.ManagementService.repository.entity.Lecturer;

public interface LecturerRepository extends JpaRepository<Lecturer,Integer> {
    Boolean existsByEmail(String email);
    Lecturer findOneByEmail(String lecturerEmail);

    @Query(nativeQuery = true, value = "select id from LECTURER where email = ?1")
    Integer findLecturerIdByEmail(String email);
}
