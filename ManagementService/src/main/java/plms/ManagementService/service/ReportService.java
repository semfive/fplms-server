package plms.ManagementService.service;

import java.sql.Date;
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
import plms.ManagementService.model.request.*;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.*;
import plms.ManagementService.repository.entity.*;
import plms.ManagementService.repository.entity.Class;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;
import plms.ManagementService.service.exception.ServiceException;

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
    LecturerRepository lecturerRepository;
    @Autowired
    ProgressReportRepository progressReportRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(ReportService.class);
    private static final String REPORT_NOT_IN_GROUP = "Report is not belong to this group.";
    private static final String NOT_IN_GROUP = "Student not in group.";
    private static final String NOT_A_LEADER = "Not a leader.";
    private static final String NOT_IN_CYCLE = "Not in cycle";
    private static final String CYCLE_REPORT_EXISTS = "This cycle has report already";
    private static final String LECTURER_NOT_MANAGE = "Lecturer not manage this class.";
    private static final String GROUP_DISABLE = "Group is disable.";
    private static final String CREATE_CYCLE_REPORT = "Create cycle report: ";
    private static final String UPDATE_CYCLE_REPORT = "Update cycle report: ";
    private static final String DELETE_CYCLE_REPORT = "Delete cycle report: ";
    private static final String CREATE_PROGRESS_REPORT = "Create progress report: ";
    private static final String UPDATE_PROGRESS_REPORT = "Update progress report: ";
    private static final String DELETE_PROGRESS_REPORT = "Delete progress report: ";
    private static final String GET_CYCLE_REPORT = "Get cycle report in group: ";
    private static final String GET_PROGRESS_REPORT = "Get progress report in group: ";
    private static final String FEEDBACK_CYCLE_REPORT = "Feedback cycle report: ";

    public Response<Set<CycleReportDTO>> getCycleReportInGroupByStudent(Integer classId, Integer groupId,
                                                                        Timestamp startDate, Timestamp endDate, String userEmail) {
        logger.info("getCycleReportInGroupByStudent(classId: {}, groupId: {}, startDate: {}, endDate: {}, userEmail: {})", classId, groupId, startDate, endDate, userEmail);
        Integer studentId = studentRepository.findStudentIdByEmail(userEmail);
        if (studentId == null) {
            logger.warn("{}{}", GET_CYCLE_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (studentGroupRepository.isStudentExistInGroup(groupId, studentId) == null) {
            logger.warn("{}{}", GET_CYCLE_REPORT, NOT_IN_GROUP);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP);
        }
        return getCycleReportInGroup(classId, groupId, startDate, endDate);
    }

    public Response<Set<CycleReportDTO>> getCycleReportInGroupByLecturer(Integer classId, Integer groupId,
                                                                         Timestamp startDate, Timestamp endDate, String userEmail) {
        logger.info("getCycleReportInGroupByLecturer(classId: {}, groupId: {}, startDate: {}, endDate: {}, userEmail: {})", classId, groupId, startDate, endDate, userEmail);
        Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
        if (lecturerId == null) {
            logger.warn("{}{}", GET_CYCLE_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }

        if (!lecturerId.equals(classRepository.findOneById(classId).getLecturer().getId())) {
            logger.warn("{}{}", GET_CYCLE_REPORT, LECTURER_NOT_MANAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, LECTURER_NOT_MANAGE);
        }
        return getCycleReportInGroup(classId, groupId, startDate, endDate);
    }


    public Response<Set<CycleReportDTO>> getCycleReportInGroup(Integer classId, Integer groupId,
                                                               Timestamp startDate, Timestamp endDate) {
        logger.info("getCycleReportInGroup(classId: {}, groupId: {}, startDate: {}, endDate: {})", classId, groupId, startDate, endDate);

        if (classId == null || groupId == null || !classRepository.existsById(classId) ||
                !groupRepository.existsById(groupId)) {
            logger.warn("{}{}", GET_CYCLE_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", GET_CYCLE_REPORT, "Group is not exist in class.");
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


    public Response<Void> addCycleReport(CreateCycleReportRequest reportRequest, Integer leaderId) {
        Integer groupId = reportRequest.getGroupId();
        logger.info("addCycleReport(reportRequest: {}, leaderId: {})", reportRequest, leaderId);

        if (reportRequest == null || groupId == null || leaderId == null ||
                !groupRepository.existsById(groupId) || !studentRepository.existsById(leaderId)) {
            logger.warn("{}{}", CREATE_CYCLE_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (groupRepository.isGroupDisable(groupId) == 1) {
            logger.warn("{}{}", CREATE_CYCLE_REPORT, GROUP_DISABLE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_DISABLE);
        }
        if (!leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("{}{}", CREATE_CYCLE_REPORT, NOT_A_LEADER);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_A_LEADER);
        }
        Integer currentCycle = getCurrentCycle(groupId);
        if (currentCycle == null) {
            logger.warn("{}{}", CREATE_CYCLE_REPORT, NOT_IN_CYCLE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_CYCLE);
        }
        if (cycleReportRepository.existsByGroupAndCycleNumber(new Group(groupId), currentCycle)) {
            logger.warn("{}{}", CREATE_CYCLE_REPORT, CYCLE_REPORT_EXISTS);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, CYCLE_REPORT_EXISTS);
        }
        CycleReport cycleReport = modelMapper.map(reportRequest, CycleReport.class);
        cycleReport.setGroup(new Group(groupId));
        cycleReport.setCycleNumber(currentCycle);
        cycleReportRepository.save(cycleReport);
        logger.info("Add cycle report success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Void> updateCycleReport(UpdateCycleReportRequest reportRequest, Integer leaderId) {
        CycleReport cycleReport = cycleReportRepository.getById(reportRequest.getId());
        Integer groupId = reportRequest.getGroupId();
        logger.info("UpdateCycleReport(reportRequest: {}, groupId: {}, leaderId: {})", reportRequest, groupId, leaderId);
        if (reportRequest == null || groupId == null || leaderId == null ||
                !groupRepository.existsById(groupId) || !studentRepository.existsById(leaderId) || !cycleReportRepository.existsById(reportRequest.getId())) {
            logger.warn("{}{}", UPDATE_CYCLE_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (cycleReport.getFeedback() != null) {
            logger.warn("{}{}", UPDATE_CYCLE_REPORT, "Feedback is not null");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Can not update report having feedback");
        }
        if (groupRepository.isGroupDisable(groupId) == 1) {
            logger.warn("{}{}", UPDATE_CYCLE_REPORT, GROUP_DISABLE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_DISABLE);
        }
        if (!leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("{}{}", UPDATE_CYCLE_REPORT, NOT_A_LEADER);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_A_LEADER);
        }
        Integer currentCycle = getCurrentCycle(groupId);
        if (currentCycle == null || !cycleReport.getCycleNumber().equals(currentCycle)) {
            logger.warn("{}{}", UPDATE_CYCLE_REPORT, NOT_IN_CYCLE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_CYCLE);
        }
        cycleReport.setContent(reportRequest.getContent());
        cycleReport.setTitle(reportRequest.getTitle());
        cycleReport.setResourceLink(reportRequest.getResourceLink());
        cycleReportRepository.save(cycleReport);
        logger.info("Update cycle report success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }


    public Response<Void> deleteCycleReport(Integer groupId, Integer reportId, Integer leaderId) {
        logger.info("deleteCycleReport(reportId: {}, groupId: {}, leaderId: {})", reportId, groupId, leaderId);

        if (reportId == null || groupId == null || leaderId == null || !groupRepository.existsById(groupId)
                || !studentRepository.existsById(leaderId) || !cycleReportRepository.existsById(reportId)) {
            logger.warn("{}{}", DELETE_CYCLE_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (groupRepository.isGroupDisable(groupId) == 1) {
            logger.warn("{}{}", DELETE_CYCLE_REPORT, GROUP_DISABLE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_DISABLE);
        }
        if (!leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("{}{}", DELETE_CYCLE_REPORT, NOT_A_LEADER);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_A_LEADER);
        }
        if (cycleReportRepository.existsByIdAndGroupId(groupId, reportId) == null) {
            logger.warn("{}{}", DELETE_CYCLE_REPORT, REPORT_NOT_IN_GROUP);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, REPORT_NOT_IN_GROUP);
        }
        if (cycleReportRepository.getById(reportId).getFeedback() != null) {
            logger.warn("{}{}", DELETE_CYCLE_REPORT, "Feedback is not null");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Can not delete report having feedback");
        }
        cycleReportRepository.delete(new CycleReport(reportId));
        logger.info("Delete cycle report success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }


    public Response<Void> feedbackCycleReport(FeedbackCycleReportRequest feedbackCycleReportRequest, String userEmail) {
        logger.info("{}{}", FEEDBACK_CYCLE_REPORT, feedbackCycleReportRequest);
        Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
        if (lecturerId == null || feedbackCycleReportRequest.getGroupId() == null || feedbackCycleReportRequest.getReportId() == null ||
                !groupRepository.existsById(feedbackCycleReportRequest.getGroupId()) || !cycleReportRepository.existsById(feedbackCycleReportRequest.getReportId())) {
            logger.warn("{}{}", FEEDBACK_CYCLE_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (groupRepository.isGroupDisable(feedbackCycleReportRequest.getGroupId()) == 1) {
            logger.warn("{}{}", FEEDBACK_CYCLE_REPORT, GROUP_DISABLE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_DISABLE);
        }
        if (!lecturerId.equals(groupRepository.findOneById(feedbackCycleReportRequest.getGroupId()).getClassEntity().getLecturer().getId())) {
            logger.warn("{}{}", FEEDBACK_CYCLE_REPORT, LECTURER_NOT_MANAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, LECTURER_NOT_MANAGE);
        }
        if (cycleReportRepository.existsByIdAndGroupId(feedbackCycleReportRequest.getGroupId(), feedbackCycleReportRequest.getReportId()) == null) {
            logger.warn("{}{}", FEEDBACK_CYCLE_REPORT, REPORT_NOT_IN_GROUP);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, REPORT_NOT_IN_GROUP);
        }
        cycleReportRepository.addFeedback(feedbackCycleReportRequest.getReportId(), feedbackCycleReportRequest.getFeedback(), feedbackCycleReportRequest.getMark());
        logger.info("Feedback cycle report successful.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Set<ProgressReportDTO>> getProgressReportInGroupByStudent(Integer classId, Integer groupId,
                                                                              Timestamp startDate, Timestamp endDate, String userEmail) {
        logger.info("getProgressReportInGroupByStudent(classId: {}, groupId: {}, startDate: {}, endDate: {}, userEmail: {})", classId, groupId, startDate, endDate, userEmail);
        Integer studentId = studentRepository.findStudentIdByEmail(userEmail);
        if (studentId == null) {
            logger.warn("{}{}", GET_PROGRESS_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (studentGroupRepository.isStudentExistInGroup(groupId, studentId) == null) {
            logger.warn("{}{}", GET_PROGRESS_REPORT, "Student not in group");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Student not in group");
        }
        return getProgressReportInGroup(classId, groupId, startDate, endDate);
    }

    public Response<Set<ProgressReportDTO>> getProgressReportInGroupByLecturer(Integer classId, Integer groupId,
                                                                               Timestamp startDate, Timestamp endDate, String userEmail) {
        logger.info("getCycleReportInGroupByLecturer(classId: {}, groupId: {}, startDate: {}, endDate: {}, userEmail: {})", classId, groupId, startDate, endDate, userEmail);
        Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
        if (lecturerId == null) {
            logger.warn("{}{}", GET_PROGRESS_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }

        if (!lecturerId.equals(classRepository.findOneById(classId).getLecturer().getId())) {
            logger.warn("{}{}", GET_PROGRESS_REPORT, "Lecturer not manage this group.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Lecturer not manage this group.");
        }
        return getProgressReportInGroup(classId, groupId, startDate, endDate);
    }


    public Response<Set<ProgressReportDTO>> getProgressReportInGroup(Integer classId, Integer groupId,
                                                                     Timestamp startDate, Timestamp endDate) {
        logger.info("getProgressReportInGroup(classId: {}, groupId: {}, startDate: {}, endDate: {})", classId, groupId, startDate, endDate);

        if (classId == null || groupId == null || !classRepository.existsById(classId) ||
                !groupRepository.existsById(groupId)) {
            logger.warn("{}{}", GET_PROGRESS_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", GET_PROGRESS_REPORT, "Group is not exist in class.");
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


    public Response<Void> addProgressReport(CreateProgressReportRequest reportRequest, Integer studentId) {
        Integer groupId = reportRequest.getGroupId();
        logger.info("addProgressReport(reportRequest: {}, groupId: {}, studentId: {})", reportRequest, groupId, studentId);
        if (reportRequest == null || groupId == null || studentId == null ||
                !groupRepository.existsById(groupId) || !studentRepository.existsById(studentId)) {
            logger.warn("{}{}", CREATE_PROGRESS_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (groupRepository.isGroupDisable(groupId) == 1) {
            logger.warn("{}{}", CREATE_PROGRESS_REPORT, GROUP_DISABLE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_DISABLE);
        }
        if (studentGroupRepository.isStudentExistInGroup(groupId, studentId) == null) {
            logger.warn("{}{}", CREATE_PROGRESS_REPORT, NOT_IN_GROUP);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP);
        }
        ProgressReport progressReport = modelMapper.map(reportRequest, ProgressReport.class);
        progressReport.setGroup(groupRepository.findOneById(groupId));
        progressReport.setStudent(studentRepository.findOneById(studentId));
        progressReportRepository.save(progressReport);
        logger.info("Add progress report success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Void> updateProgressReport(UpdateProgressReportRequest reportRequest, Integer studentId) {
        Integer groupId = reportRequest.getGroupId();
        logger.info("updateProgressReport(reportRequest: {}, groupId: {}, studentId: {})", reportRequest, groupId, studentId);
        if (reportRequest == null || groupId == null || studentId == null ||
                !groupRepository.existsById(groupId) || !studentRepository.existsById(studentId) || !cycleReportRepository.existsById(reportRequest.getId())) {
            logger.warn("{}{}", UPDATE_PROGRESS_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (groupRepository.isGroupDisable(groupId) == 1) {
            logger.warn("{}{}", UPDATE_PROGRESS_REPORT, GROUP_DISABLE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_DISABLE);
        }
        if (studentGroupRepository.isStudentExistInGroup(groupId, studentId) == null) {
            logger.warn("{}{}", UPDATE_PROGRESS_REPORT, NOT_IN_GROUP);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP);
        }
        ProgressReport progressReport = modelMapper.map(reportRequest, ProgressReport.class);
        progressReport.setGroup(new Group(groupId));
        progressReport.setStudent(new Student(studentId));
        progressReportRepository.save(progressReport);
        logger.info("Update progress report success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }


    public Response<Void> deleteProgressReport(Integer groupId, Integer reportId, Integer studentId) {
        logger.info("deleteProgressReport(reportId: {}, groupId: {}, studentId: {})", reportId, groupId, studentId);

        if (reportId == null || groupId == null || studentId == null || !groupRepository.existsById(groupId)
                || !studentRepository.existsById(studentId) || !progressReportRepository.existsById(reportId)) {
            logger.warn("{}{}", DELETE_PROGRESS_REPORT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }

        if (studentId.equals(studentGroupRepository.isStudentExistInGroup(groupId, studentId))) {
            logger.warn("{}{}", DELETE_PROGRESS_REPORT, NOT_IN_GROUP);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP);
        }
        if (groupRepository.isGroupDisable(groupId) == 1) {
            logger.warn("{}{}", DELETE_PROGRESS_REPORT, GROUP_DISABLE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_DISABLE);
        }
        if (progressReportRepository.existsByIdAndGroupIdAndStudentId(groupId, reportId, studentId) == null) {
            logger.warn("{}{}", DELETE_PROGRESS_REPORT, "Report is not belong to this student.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Report is not belong to this student.");
        }
        progressReportRepository.delete(new ProgressReport(reportId));
        logger.info("Delete progress report success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    @Transactional
    public Integer getCurrentCycle(int groupId) {
        Class classEntity = groupRepository.findOneById(groupId).getClassEntity();
        Integer cycleDuration = classEntity.getCycleDuration();
        Semester semester = classEntity.getSemester();
        Date currentDate = new Date(System.currentTimeMillis());
        if (currentDate.before(semester.getStartDate()) || currentDate.after(semester.getEndDate())) {
            // not in semester
            return null;
        }
        Integer currentCycle = Integer.valueOf((int) ((currentDate.getTime() - semester.getStartDate().getTime()) / ((long) 1000 * 60 * 60 * 24 * cycleDuration) + 1));
        return currentCycle;
    }
}
