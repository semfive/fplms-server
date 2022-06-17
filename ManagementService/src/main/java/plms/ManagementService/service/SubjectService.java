package plms.ManagementService.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import plms.ManagementService.model.dto.SubjectDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.SubjectRepository;
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
	ModelMapper modelMapper;
	
	private static final Logger logger = LogManager.getLogger(SubjectService.class);
	private static final String CREATE_SUBJECT = "Create subject";
	private static final String UPDATE_SUBJECT = "Update subject";
	private static final String DELETE_SUBJECT = "Delete subject";
	
	public Response<Set<SubjectDTO>> getSubjects() {
		logger.info("getSubjects()");
		
		Set<SubjectDTO> subjectDtoSet = subjectRepository.findAll().stream()
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
		if (subjectRepository.existsByName(subjectDto.getName())) {
			logger.warn("{}{}", CREATE_SUBJECT, "Subject already exist.");
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Subject already exist.");
		}
		Subject subject = modelMapper.map(subjectDto, Subject.class);
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
		if (!subjectRepository.existsById(subjectDto.getId())) {
			logger.warn("{}{}", UPDATE_SUBJECT, ServiceMessage.ID_NOT_EXIST_MESSAGE);
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
		}
		Subject subject = modelMapper.map(subjectDto, Subject.class);
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
		if (classRepository.findClassBySubject(id) != null) {
			logger.warn("{}{}", DELETE_SUBJECT, "Some classes use this subject");
	        return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Some classes use this subject");
		}
		subjectRepository.delete(new Subject(id));
		logger.info("Delete subject success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	
}
