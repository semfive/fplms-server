package plms.ManagementService.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import plms.ManagementService.model.dto.ProjectDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.LecturerRepository;
import plms.ManagementService.repository.ProjectRepository;
import plms.ManagementService.repository.SemesterRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.SubjectRepository;
import plms.ManagementService.repository.entity.Project;
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
	GroupRepository groupRepository;
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	LecturerRepository lecturerRepository;
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	SemesterRepository semesterRepository;
	
	private static final Logger logger = LogManager.getLogger(ProjectService.class);
	private static final String PROJECT_NOT_MANAGE = "Lecturer not manage this project.";
	private static final String SUBJECT_NOT_EXIST = "Subject not exist";
	private static final String SEMESTER_NOT_EXIST = "Semester not exist";
	private static final String GET_PROJECT = "Get project from class: ";
	private static final String ADD_PROJECT = "Add project to class: ";
	private static final String UPDATE_PROJECT = "Update project in class: ";
	private static final String DELETE_PROJECT = "Delete project in class: ";


	public Response<Set<ProjectDTO>> getProjectFromClassByStudent(Integer classId, String userEmail) {
    	logger.info("getProjectFromClassByStudent(classId: {}, userEmail: {})", classId, userEmail);
    	Integer studentId = studentRepository.findStudentIdByEmail(userEmail);
		if (studentId == null || classId == null) {
            logger.warn("{}{}", GET_PROJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (classRepository.existsInClass(studentId, classId) == null) {
			logger.warn("{}{}", GET_PROJECT, "Student is not in class");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Student is not in class");
		}
		return getProjectFromClass(classId);
	}
	
	public Response<Set<ProjectDTO>> getProjectByLecturer(String semesterCode, Integer classId, String userEmail) {
    	logger.info("getProjectByLecturer(semesterCode: {}, classId: {}, userEmail: {})", semesterCode, classId, userEmail);
		Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
    	if (lecturerId == null) {
            logger.warn("{}{}", GET_PROJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (classId != null) {
			if (!classRepository.existsById(classId)) {
				logger.warn("{}{}", GET_PROJECT, ServiceMessage.ID_NOT_EXIST_MESSAGE);
				return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
			}
			if (!lecturerId.equals(classRepository.findOneById(classId).getLecturer().getId())) {
				logger.warn("{}{}", GET_PROJECT, "Lecturer not manage class");
				return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Lecturer not manage class");
			}
			return getProjectFromClass(classId);
    	}
		
		if (semesterCode != null) {
			return getAllProjectInSemesterByLecturer(lecturerId, semesterCode);
		}
    	return getAllProjectByLecturer(lecturerId);
	}
	
	public Response<Set<ProjectDTO>> getProjectFromClass(Integer classId) {
    	logger.info("getProjectFromClass(classId: {})", classId);
    	
		if (classRepository.findOneById(classId) == null) {
            logger.warn("Get project from class: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
		Set<Project> projectSet = projectRepository
				.findBySubjectIdAndLecturerIdAndSemester(classRepository.findOneById(classId).getSubject().getId(),
												classRepository.findOneById(classId).getLecturer().getId(),
												classRepository.findOneById(classId).getSemester().getCode());
		Set<ProjectDTO> projectDtoSet = projectSet.stream()
				.map(projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class)).collect(Collectors.toSet());
		logger.info("Get project from class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, projectDtoSet);
	}
	
	public Response<Set<ProjectDTO>> getAllProjectInSemesterByLecturer(Integer lecturerId, String semesterCode) {
    	logger.info("getAllProjectInSemesterByLecturer(lecturerId: {}, semesterCode: {})", lecturerId, semesterCode);

		if (!semesterRepository.existsById(semesterCode)) {
			logger.warn("{}{}", GET_PROJECT, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
		}
		Set<Project> projectSet = projectRepository.findByLecturerIdAndSemester(lecturerId, semesterCode);
		Set<ProjectDTO> projectDtoSet = projectSet.stream()
				.map(projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class)).collect(Collectors.toSet());
		logger.info("Get project from class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, projectDtoSet);

	}
	
	public Response<Set<ProjectDTO>> getAllProjectByLecturer(Integer lecturerId) {
    	logger.info("getAllProjectByLecturer(lecturerId: {})", lecturerId);

		Set<Project> projectSet = projectRepository.findByLecturerId(lecturerId);
		Set<ProjectDTO> projectDtoSet = projectSet.stream()
				.map(projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class)).collect(Collectors.toSet());
		logger.info("Get project from class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, projectDtoSet);

	}
	
	@Transactional
	public Response<Integer> addProject(ProjectDTO projectDTO, String userEmail) {
    	logger.info("addProject(projectDTO: {}, userEmail: {})", projectDTO, userEmail);
		Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
    	if (lecturerId == null || projectDTO == null || projectDTO.getSubjectId() == null 
    			|| projectDTO.getSemesterCode() == null) {
            logger.warn("{}{}", ADD_PROJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
    	if (!subjectRepository.existsById(projectDTO.getSubjectId())) {
    		logger.warn("{}{}", ADD_PROJECT, SUBJECT_NOT_EXIST);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, SUBJECT_NOT_EXIST);
    	}
    	if (!semesterRepository.existsById(projectDTO.getSemesterCode())) {
    		logger.warn("{}{}", ADD_PROJECT, SEMESTER_NOT_EXIST);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, SEMESTER_NOT_EXIST);
    	}
    	Project project = modelMapper.map(projectDTO, Project.class);
    	project.setLecturer(lecturerRepository.findOneByEmail(userEmail));
    	Integer id = projectRepository.save(project).getId();
		logger.info("Add project success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE,id);
	}
	
	@Transactional
	public Response<Void> updateProject(ProjectDTO projectDTO, String userEmail) {
    	logger.info("updateProject(projectDTO: {}, userEmail: {})", projectDTO, userEmail);
		Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
    	if (lecturerId == null || projectDTO == null) {
            logger.warn("{}{}", UPDATE_PROJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
    	if (!subjectRepository.existsById(projectDTO.getSubjectId())) {
    		logger.warn("{}{}", UPDATE_PROJECT, SUBJECT_NOT_EXIST);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, SUBJECT_NOT_EXIST);
    	}
    	if (!semesterRepository.existsById(projectDTO.getSemesterCode())) {
    		logger.warn("{}{}", UPDATE_PROJECT, SEMESTER_NOT_EXIST);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, SEMESTER_NOT_EXIST);
    	}
    	if (projectRepository.existsByLecturerId(lecturerId, projectDTO.getId()) == null) {
    		logger.warn("{}{}", UPDATE_PROJECT, PROJECT_NOT_MANAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, PROJECT_NOT_MANAGE);
    	}
    	Project project = modelMapper.map(projectDTO, Project.class);
    	project.setLecturer(lecturerRepository.findOneByEmail(userEmail));
    	projectRepository.save(project);
		logger.info("Update project success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	@Transactional
	public Response<Void> deleteProject(Integer projectId, String userEmail) {
    	logger.info("deleteProject(projectId: {}, userEmail: {})", projectId, userEmail);
		Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
    	if (lecturerId == null) {
            logger.warn("{}{}", DELETE_PROJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
    	if (!projectRepository.existsById(projectId)) {
            logger.warn("{}{}", DELETE_PROJECT, "Project not exist");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Project not exist");
    	}
    	if (projectRepository.existsByLecturerId(lecturerId, projectId) == null) {
    		logger.warn("{}{}", DELETE_PROJECT, PROJECT_NOT_MANAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, PROJECT_NOT_MANAGE);
    	}
    	if (groupRepository.existByProject(projectId) != null) {
    		logger.warn("{}{}", DELETE_PROJECT, "At least one group used this project.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "At least one group used this project.");
    	}
    	projectRepository.deleteProject(projectId);
    	logger.info("Delete project success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	
	
}
