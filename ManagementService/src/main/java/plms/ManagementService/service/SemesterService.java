package plms.ManagementService.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import plms.ManagementService.model.dto.SemesterDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.ProjectRepository;
import plms.ManagementService.repository.SemesterRepository;
import plms.ManagementService.repository.entity.Semester;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;

@Service
public class SemesterService {
	@Autowired
	ModelMapper modelMapper;
	@Autowired
	SemesterRepository semesterRepository;
	@Autowired
	ClassRepository classRepository;
	@Autowired
	ProjectRepository projectRepository;
	
    private static final Logger logger = LogManager.getLogger(SemesterService.class);
    private static final String CREATE_SEMESTER = "Create semester: ";
    private static final String DELETE_SEMESTER = "Delete semester: ";
    private static final String UPDATE_SEMESTER = "Update semester: ";
    private static final String CHANGE_SEMESTER = "Change semester: ";
    
    public Response<Void> addSemester(SemesterDTO semesterDto) {
    	logger.info("addSemester(semesterDto: {})", semesterDto);
    	if (semesterDto.getCode() == null || semesterDto.getStartDate() == null 
    			|| semesterDto.getEndDate() == null) {
            logger.warn("{}{}", CREATE_SEMESTER, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
    	}
    	if (semesterDto.getEndDate().before(semesterDto.getStartDate())) {
            logger.warn("{}{}", CREATE_SEMESTER, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
    	}
    	Semester semester = modelMapper.map(semesterDto, Semester.class);
    	semesterRepository.save(semester);
        logger.info("Create semester success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }
    
    public Response<Set<SemesterDTO>> getSemester(String code) {
    	logger.info("getSemester(code: {})", code);
    	if (code == null)
    		code = "";
    	Set<Semester> semesterSet = semesterRepository.getSemester("%" + code + "%");
    	Set<SemesterDTO> semesterDtoSet = semesterSet.stream()
    			.map(semesterEntity -> modelMapper.map(semesterEntity, SemesterDTO.class)).collect(Collectors.toSet());
        logger.info("Get semester success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, semesterDtoSet);
    }
    
    public Response<Void> updateSemester(SemesterDTO semesterDto) {
    	logger.info("updateSemester(semesterDto: {})", semesterDto);
    	if (semesterDto.getCode() == null || semesterDto.getStartDate() == null 
    			|| semesterDto.getEndDate() == null) {
            logger.warn("{}{}", UPDATE_SEMESTER, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
    	}
    	if (!semesterRepository.existsById(semesterDto.getCode())) {
            logger.warn("{}{}", UPDATE_SEMESTER, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
    	}
    	if (semesterDto.getEndDate().before(semesterDto.getStartDate())) {
            logger.warn("{}{}", UPDATE_SEMESTER, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
    	}
    	Semester semester = modelMapper.map(semesterDto, Semester.class);
    	semesterRepository.save(semester);
        logger.info("Update semester success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }
    
    public Response<Void> deleteSemester(String code) {
    	logger.info("deleteSemester(code: {})", code);
    	if (code == null) {
            logger.warn("{}{}", DELETE_SEMESTER, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
    	}
    	if (!semesterRepository.existsById(code)) {
            logger.warn("{}{}", DELETE_SEMESTER, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
    	}
    	if (classRepository.findClassBySemester(code) != null) {
            logger.warn("{}{}", DELETE_SEMESTER, "Some classes created in this semester.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Some classes created in this semester.");
    	}
    	if (projectRepository.findProjectBySemester(code) != null) {
            logger.warn("{}{}", DELETE_SEMESTER, "Some projects created in this semester.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Some projects created in this semester.");
    	}
    	semesterRepository.delete(new Semester(code));
        logger.info("Delete semester success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }
    
    public Response<Void> changeSemester(String oldCode, String newCode) {
    	logger.info("changeSemester(code: {}, code: {})", oldCode, newCode);
    	if (oldCode == null && newCode == null) {
            logger.warn("{}{}", CHANGE_SEMESTER, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
    	}
    	if (!semesterRepository.existsById(oldCode) || !semesterRepository.existsById(newCode)) {
            logger.warn("{}{}", CHANGE_SEMESTER, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
    	}
    	classRepository.changeClassSemester(oldCode, newCode);
    	projectRepository.changeProjectSemester(oldCode, newCode);
        logger.info("Change semester success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }
}
