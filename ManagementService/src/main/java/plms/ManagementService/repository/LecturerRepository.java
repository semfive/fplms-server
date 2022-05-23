package plms.ManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plms.ManagementService.repository.entity.Lecturer;

public interface LecturerRepository extends JpaRepository<Lecturer,Integer> {
    Lecturer findOneByEmail(String lecturerEmail);
}
