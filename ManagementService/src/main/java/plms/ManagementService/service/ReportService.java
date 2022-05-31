package plms.ManagementService.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import plms.ManagementService.model.dto.CycleReportDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.CycleReportRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.entity.CycleReport;
import plms.ManagementService.repository.entity.Group;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;

@Service
public class ReportService {
	@Autowired
	CycleReportRepository cycleReportRepository;
	@Autowired
	ClassRepository classRepository;
	@Autowired
	GroupRepository groupRepository;
	@Autowired
	ModelMapper modelMapper;
	
	private static final Logger logger = LogManager.getLogger(ReportService.class);
	private static final String GET_CYCLE_REPORT_IN_GROUP = "Get cycle report in group: ";
	
	public Response<Set<CycleReportDTO>> getCycleReportInGroup(Integer classId, Integer groupId) {
		if (classId == null || groupId == null || !classRepository.existsById(classId) ||
				!groupRepository.existsById(groupId)) {
            logger.warn("{}{}", GET_CYCLE_REPORT_IN_GROUP, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
		if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
			logger.warn("{}{}", GET_CYCLE_REPORT_IN_GROUP, "Group is not exist in class.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
		}
		Set<CycleReport> cycleReportSet = cycleReportRepository.findByGroup(new Group(groupId));
		Set<CycleReportDTO> cycleReportDtoSet = cycleReportSet.stream().map(cycleReportEntity -> {
			CycleReportDTO dto = modelMapper.map(cycleReportEntity, CycleReportDTO.class);
			dto.setGroupId(cycleReportEntity.getGroup().getId());
			return dto;
		}).collect(Collectors.toSet());
		
		logger.info("Get cycle report from group success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, cycleReportDtoSet);
	}
	
	
}
