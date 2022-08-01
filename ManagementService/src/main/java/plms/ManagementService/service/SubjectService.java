package plms.ManagementService.service;

import java.sql.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import plms.ManagementService.model.dto.SubjectDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.SemesterRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.SubjectRepository;
import plms.ManagementService.repository.entity.Class;
import plms.ManagementService.repository.entity.Subject;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;

@Service
public class SubjectService {
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	ClassRepository classRepository;
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	SemesterRepository semesterRepository;
	@Autowired
	ModelMapper modelMapper;
	
	private static final Logger logger = LogManager.getLogger(SubjectService.class);
	private static final String CREATE_SUBJECT = "Create subject: ";
	private static final String UPDATE_SUBJECT = "Update subject: ";
	private static final String DELETE_SUBJECT = "Delete subject: ";
	private static final String IS_STUDIED = "Is studentstudy subject: ";
	
	public Response<Set<SubjectDTO>> getSubjects() {
		logger.info("getSubjects()");
		
		Set<SubjectDTO> subjectDtoSet = subjectRepository.getAllSubject().stream()
				.map(subjectEntity -> modelMapper.map(subjectEntity, SubjectDTO.class)).collect(Collectors.toSet());
		
		logger.info("Get subject success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, subjectDtoSet);
	}
	
	public Response<Void> createSubject(SubjectDTO subjectDto) {
		logger.info("createSubject(subjectDto: {})", subjectDto);

		if (subjectDto == null) {
			logger.warn("{}{}", CREATE_SUBJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (subjectRepository.findByName(subjectDto.getName()) != null) {
			logger.warn("{}{}", CREATE_SUBJECT, "Subject already exist.");
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Subject already exist.");
		}
		Subject subject = modelMapper.map(subjectDto, Subject.class);
		subject.setIsDisable(false);
		subjectRepository.save(subject);
		logger.info("Create subject success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	public Response<Void> updateSubject(SubjectDTO subjectDto) {
		logger.info("updateSubject(subjectDto: {})", subjectDto);

		if (subjectDto == null) {
			logger.warn("{}{}", UPDATE_SUBJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (!subjectRepository.existsById(subjectDto.getId()) || subjectRepository.isSubjectDisable(subjectDto.getId()) == 1) {
			logger.warn("{}{}", UPDATE_SUBJECT, ServiceMessage.ID_NOT_EXIST_MESSAGE);
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
		}
		if (subjectRepository.findByName(subjectDto.getName()) != null) {
			logger.warn("{}{}", UPDATE_SUBJECT, "Subject already exist.");
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Subject already exist.");
		}
		Subject subject = modelMapper.map(subjectDto, Subject.class);
		subject.setIsDisable(false);
		subjectRepository.save(subject);
		logger.info("Update subject success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	public Response<Void> deleteSubject(Integer id) {
		logger.info("deleteSubject(id: {})", id);
		
		if (!subjectRepository.existsById(id)) {
			logger.warn("{}{}", DELETE_SUBJECT, ServiceMessage.ID_NOT_EXIST_MESSAGE);
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
		}
		String currentSemester = semesterRepository.getCurrentSemester(new Date(System.currentTimeMillis()));
		if (classRepository.findClassBySubjectAndSemester(id, currentSemester) != null) {
			logger.warn("{}{}", DELETE_SUBJECT, "Some classes use this subject in this semester");
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Some classes use this subject in this semester");
		}
		Subject subject = subjectRepository.findOneById(id);
		subject.setIsDisable(true);
		subjectRepository.save(subject);
		logger.info("Delete subject success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	public ResponseEntity<Void> isStudentStudiedSubject(String userEmail, String subjectName) {
    	logger.info("isStudentStudiedSubject(subjectName: {}, userEmail: {})", subjectName, userEmail);
    	Integer studentId = studentRepository.findStudentIdByEmail(userEmail);
        if (studentId == null || subjectName == null) {
        	logger.warn("{}{}", IS_STUDIED, ServiceStatusCode.BAD_REQUEST_STATUS);
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (subjectRepository.findByName(subjectName) == null) {
        	logger.warn("{}{}", IS_STUDIED, "Subject name not exist.");
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<Class> classList = subjectRepository.findByName(subjectName).getClassList();
        for (Class classEntity : classList) {
        	if (classRepository.existsInClass(studentId, classEntity.getId()) != null) {
        		logger.info("{}{}", IS_STUDIED, "Student study this subject.");
        		return new ResponseEntity<>(HttpStatus.OK);
        	}
        }
		logger.info("{}{}", IS_STUDIED, "Student not study this subject.");
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	
}
