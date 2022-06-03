package plms.ManagementService.service;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import plms.ManagementService.model.dto.CycleReportDTO;
import plms.ManagementService.model.dto.ProgressReportDTO;
import plms.ManagementService.model.request.CreateCycleReportRequest;
import plms.ManagementService.model.request.CreateProgressReportRequest;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.CycleReportRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.ProgressReportRepository;
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.entity.CycleReport;
import plms.ManagementService.repository.entity.Group;
import plms.ManagementService.repository.entity.ProgressReport;
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
	ProgressReportRepository progressReportRepository;
	@Autowired
	ModelMapper modelMapper;
	
	private static final Logger logger = LogManager.getLogger(ReportService.class);
	private static final String NOT_IN_GROUP = "Student not in group.";
	private static final String NOT_A_LEADER = "Not a leader.";
	private static final String DELETE_CYCLE_REPORT = "Delete cycle report: ";
	
	public Response<Set<CycleReportDTO>> getCycleReportInGroup(Integer classId, Integer groupId, 
			Timestamp startDate, Timestamp endDate) {
    	logger.info("getCycleReportInGroup(classId: {}, groupId: {}, startDate: {}, endDate: {})", classId, groupId, startDate, endDate);

		if (classId == null || groupId == null || !classRepository.existsById(classId) ||
				!groupRepository.existsById(groupId)) {
            logger.warn("Get cycle report in group: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
		if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
			logger.warn("Get cycle report in group: {}", "Group is not exist in class.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
		}
		Set<CycleReport> cycleReportSet;
		if (startDate == null || endDate == null) {
			cycleReportSet = cycleReportRepository.findByGroup(new Group(groupId));
		} else {
			cycleReportSet = cycleReportRepository.findByGroupIdAndTimeFilter(groupId, startDate, endDate);
		}
		
		Set<CycleReportDTO> cycleReportDtoSet = cycleReportSet.stream().map(cycleReportEntity -> {
			CycleReportDTO dto = modelMapper.map(cycleReportEntity, CycleReportDTO.class);
			dto.setGroupId(cycleReportEntity.getGroup().getId());
			return dto;
		}).collect(Collectors.toSet());
		
		logger.info("Get cycle report from group success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, cycleReportDtoSet);
	}
	
	@Transactional
	public Response<Void> addCycleReport(CreateCycleReportRequest reportRequest, Integer groupId, Integer leaderId) {
    	logger.info("addCycleReport(reportRequest: {}, groupId: {}, leaderId: {})", reportRequest, groupId, leaderId);
    	
		if (reportRequest == null || groupId == null || leaderId == null ||
				!groupRepository.existsById(groupId) || !studentRepository.existsById(leaderId)) {
            logger.warn("Add cycle report: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (!leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("Add cycle report: {}", NOT_A_LEADER);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_A_LEADER);
		}
		CycleReport cycleReport = modelMapper.map(reportRequest, CycleReport.class);
		cycleReport.setGroup(groupRepository.findOneById(groupId));
		cycleReportRepository.save(cycleReport);
		logger.info("Add cycle report success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	@Transactional
	public Response<Void> deleteCycleReport(Integer groupId, Integer reportId, Integer leaderId) {
    	logger.info("deleteCycleReport(reportId: {}, groupId: {}, leaderId: {})", reportId, groupId, leaderId);

		if (reportId == null || groupId == null || leaderId == null || !groupRepository.existsById(groupId)
				 || !studentRepository.existsById(leaderId) || !cycleReportRepository.existsById(reportId)) {
            logger.warn("{}{}", DELETE_CYCLE_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (!leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("{}{}", DELETE_CYCLE_REPORT, NOT_A_LEADER);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_A_LEADER);
		}
		if (cycleReportRepository.existsByIdAndGroupId(groupId, reportId) == null) {
			logger.warn("{}{}", DELETE_CYCLE_REPORT, "Report is not belong to this group.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Report is not belong to this group.");
		}
		cycleReportRepository.delete(new CycleReport(reportId));
		logger.info("Delete cycle report success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	public Response<Set<ProgressReportDTO>> getProgressReportInGroup(Integer classId, Integer groupId,
			Timestamp startDate, Timestamp endDate) {
    	logger.info("getProgressReportInGroup(classId: {}, groupId: {}, startDate: {}, endDate: {})", classId, groupId, startDate, endDate);

		if (classId == null || groupId == null || !classRepository.existsById(classId) ||
				!groupRepository.existsById(groupId)) {
            logger.warn("Get progress report in group: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
		if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
			logger.warn("Get progress report in group: {}", "Group is not exist in class.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
		}
		Set<ProgressReport> progressReportSet;
		if (startDate == null || endDate == null) {
			progressReportSet = progressReportRepository.findByGroup(new Group(groupId));
		} else {
			progressReportSet = progressReportRepository.findByGroupIdAndTimeFilter(groupId, startDate, endDate);
		}
		Set<ProgressReportDTO> progressReportDtoSet = progressReportSet.stream().map(progressRepotEntity -> {
			ProgressReportDTO dto = modelMapper.map(progressRepotEntity, ProgressReportDTO.class);
			dto.setGroupId(progressRepotEntity.getGroup().getId());
			dto.setStudentId(progressRepotEntity.getStudent().getId());
			return dto;
		}).collect(Collectors.toSet());
		
		logger.info("Get progress report from group success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, progressReportDtoSet);
	}

	@Transactional
	public Response<Void> addProgressReport(CreateProgressReportRequest reportRequest, Integer groupId, Integer studentId) {
    	logger.info("addProgressReport(reportRequest: {}, groupId: {}, studentId: {})", reportRequest, groupId, studentId);

		if (reportRequest == null || groupId == null || studentId == null ||
				!groupRepository.existsById(groupId) || !studentRepository.existsById(studentId)) {
            logger.warn("Add progress report: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (studentGroupRepository.isStudentExistInGroup(groupId, studentId) == null) {
			logger.warn("Add progress report: {}", NOT_IN_GROUP);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP);
		}
		ProgressReport progressReport = modelMapper.map(reportRequest, ProgressReport.class);
		progressReport.setGroup(groupRepository.findOneById(groupId));
		progressReport.setStudent(studentRepository.findOneById(studentId));
		progressReportRepository.save(progressReport);
		logger.info("Add progress report success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
	
	@Transactional
	public Response<Void> deleteProgressReport(Integer groupId, Integer reportId, Integer studentId) {
    	logger.info("deleteProgressReport(reportId: {}, groupId: {}, studentId: {})", reportId, groupId, studentId);

		if (reportId == null || groupId == null || studentId == null || !groupRepository.existsById(groupId)
				 || !studentRepository.existsById(studentId) || !progressReportRepository.existsById(reportId)) {
           logger.warn("Delete progress report: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
           return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		
		if (studentId.equals(studentGroupRepository.isStudentExistInGroup(groupId, studentId))) {
           logger.warn("Delete progress report: {}", NOT_IN_GROUP);
           return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP);
		}
		if (progressReportRepository.existsByIdAndGroupIdAndStudentId(groupId, reportId, studentId) == null) {
			logger.warn("Delete cycle report: {}", "Report is not belong to this student.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Report is not belong to this student.");
		}
		progressReportRepository.delete(new ProgressReport(reportId));
		logger.info("Delete progress report success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
	}
}
