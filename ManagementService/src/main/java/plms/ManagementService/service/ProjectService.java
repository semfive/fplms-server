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
import plms.ManagementService.repository.ProjectRepository;
import plms.ManagementService.repository.entity.Class;
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
	
	private static final Logger logger = LogManager.getLogger(ClassService.class);

	
	public Response<Set<ProjectDTO>> getProjectFromClass(Integer classId) {
		if (classId == null || classRepository.findOneById(classId) == null) {
            logger.warn("Get project from class: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
		Set<Project> projectSet = projectRepository.findByClassEntity(new Class(classId));
		Set<ProjectDTO> projectDtoSet = projectSet.stream()
				.map(projectEntity -> modelMapper.map(projectEntity, ProjectDTO.class)).collect(Collectors.toSet());
		logger.info("Get project from class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, projectDtoSet);
	}
	
	
	
}
