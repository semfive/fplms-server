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
import plms.ManagementService.repository.SubjectRepository;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;

@Service
public class SubjectService {
	@Autowired
	SubjectRepository subjectRepository;
	@Autowired
	ModelMapper modelMapper;
	
	private static final Logger logger = LogManager.getLogger(SubjectService.class);

	
	public Response<Set<SubjectDTO>> getSubjects() {
		Set<SubjectDTO> subjectDtoSet = subjectRepository.findAll().stream()
				.map(subjectEntity -> modelMapper.map(subjectEntity, SubjectDTO.class)).collect(Collectors.toSet());
		
		logger.info("Get subject success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, subjectDtoSet);
	}
}
