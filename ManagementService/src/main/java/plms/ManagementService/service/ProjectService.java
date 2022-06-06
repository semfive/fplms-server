package plms.ManagementService.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import plms.ManagementService.model.dto.ProjectDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.LecturerRepository;
import plms.ManagementService.repository.ProjectRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.entity.Project;
import plms.ManagementService.repository.entity.Subject;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;

@Service
public class ProjectService {
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	ModelMapper modelMapper;
	@Autowired
	ClassRepository classRepository;
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	LecturerRepository lecturerRepository;
	
	private static final Logger logger = LogManager.getLogger(ProjectService.class);
	private static final String GET_PROJECT = "Get project from class: ";

	public Response<Set<ProjectDTO>> getProjectFromClassByStudent(Integer classId, String userEmail) {
    	logger.info("getProjectFromClassByStudent(classId: {}, userEmail: {})", classId, userEmail);
    	Integer studentId = studentRepository.findStudentIdByEmail(userEmail);
		if (studentId == null) {
            logger.warn("{}{}", GET_PROJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (classRepository.existsInClass(studentId, classId) == null) {
			logger.warn("{}{}", GET_PROJECT, "Student is not in class");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Student is not in class");
		}
		return getProjectFromClass(classId);
	}
	
	public Response<Set<ProjectDTO>> getProjectFromClassByLecturer(Integer classId, String userEmail) {
    	logger.info("getProjectFromClassByLecturer(classId: {}, userEmail: {})", classId, userEmail);
		Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
    	if (lecturerId == null) {
            logger.warn("{}{}", GET_PROJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (!lecturerId.equals(classRepository.findOneById(classId).getLecturer().getId())) {
			logger.warn("{}{}", GET_PROJECT, "Student not manage class");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Lecturer not manage class");
		}
		return getProjectFromClass(classId);
	}
		
	
	public Response<Set<ProjectDTO>> getProjectFromClass(Integer classId) {
    	logger.info("getProjectFromClass(classId: {})", classId);
    	
		if (classId == null || classRepository.findOneById(classId) == null) {
            logger.warn("Get project from class: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
		Set<Project> projectSet = projectRepository.findBySubject(new Subject(classRepository.findSubjectId(classId)));
		Set<ProjectDTO> projectDtoSet = projectSet.stream()
				.map(projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class)).collect(Collectors.toSet());
		logger.info("Get project from class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, projectDtoSet);
	}
	
	
	
}
