package plms.ManagementService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import plms.ManagementService.repository.StudentRepository;

@Service
public class StudentService {
	@Autowired
	StudentRepository studentRepository;
	
	public Integer validateStudent(String role, String email) {
		if (role.equalsIgnoreCase("student")) {
			return studentRepository.getStudentIdByEmail(email);
		}
		return null;
	}
}
