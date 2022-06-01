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
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.StudentRepository;
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
	StudentGroupRepository studentGroupRepository;
	@Autowired
	StudentRepository studentRepository;
	@Autowired
	ModelMapper modelMapper;
	
	private static final Logger logger = LogManager.getLogger(ReportService.class);
	
	public Response<Set<CycleReportDTO>> getCycleReportInGroup(Integer classId, Integer groupId) {
		if (classId == null || groupId == null || !classRepository.existsById(classId) ||
				!groupRepository.existsById(groupId)) {
            logger.warn("Get cycle report in group: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
		if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
			logger.warn("Get cycle report in group: {}", "Group is not exist in class.");
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
	
	public Response<Void> addCycleReport(CycleReportDTO cycleReportDto, Integer groupId, Integer leaderId) {
		if (cycleReportDto == null || groupId == null || leaderId == null ||
				!groupRepository.existsById(groupId) || !studentRepository.existsById(leaderId)) {
            logger.warn("Add cycle report: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (!leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("Add cycle report: {}", "Not a leader");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Not a leader");
		}
		CycleReport cycleReport = modelMapper.map(cycleReportDto, CycleReport.class);
		cycleReport.setGroup(groupRepository.findOneById(groupId));
		cycleReportRepository.save(cycleReport);
		logger.info("Add cycle report success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	public Response<Void> deleteCycleReport(Integer groupId, Integer reportId, Integer leaderId) {
		if (reportId == null || groupId == null || leaderId == null || !groupRepository.existsById(groupId)
				 || !studentRepository.existsById(leaderId) || !cycleReportRepository.existsById(reportId)) {
            logger.warn("Delete cycle report: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (!leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("Delete cycle report: {}", "Not a leader");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Not a leader");
		}
		if (cycleReportRepository.existsByIdAndGroupId(groupId, reportId) == null) {
			logger.warn("Delete cycle report: {}", "Report is not belong to this group.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Report is not belong to this group.");
		}
		cycleReportRepository.delete(new CycleReport(reportId));
		logger.info("Delete cycle report success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	
}
