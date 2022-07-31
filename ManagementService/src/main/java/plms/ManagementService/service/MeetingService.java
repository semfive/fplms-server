package plms.ManagementService.service;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import plms.ManagementService.model.dto.CycleReportDTO;
import plms.ManagementService.model.dto.MeetingDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.LecturerRepository;
import plms.ManagementService.repository.MeetingRepository;
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.entity.CycleReport;
import plms.ManagementService.repository.entity.Group;
import plms.ManagementService.repository.entity.Lecturer;
import plms.ManagementService.repository.entity.Meeting;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;

@Service
public class MeetingService {
    @Autowired
    MeetingRepository meetingRepository;
    @Autowired
    ClassRepository classRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    StudentGroupRepository studentGroupRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(MeetingService.class);
    private static final Timestamp TIMESTAMP_MAX_VALUE = Timestamp.valueOf("9999-12-31 23:59:59.9");
    private static final Timestamp TIMESTAMP_MIN_VALUE = Timestamp.valueOf("0000-01-01 00:00:00.0");
    
    private static final String NOT_IN_GROUP = "Student is not exist in group.";
    private static final String LECTURER_NOT_MANAGE = "Lecturer not manage this meeting.";
    private static final String GET_MEETING = "Get meeting: ";
    private static final String SCHEDULING_MEETING_MESSAGE = "Schedule meeting: ";
    private static final String UPDATE_MEETING_MESSAGE = "Update meeting: ";
    private static final String DELETE_MEETING_MESSAGE = "Update meeting: ";
    
    public Response<MeetingDTO> getMeetingDetailByLecturer(String userEmail, Integer meetingId) {
    	logger.info("getMeetingDetailByLecturer(meetingId: {}, userEmail: {})", meetingId, userEmail);
    	Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
        if (lecturerId == null || meetingId == null) {
            logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        } 
        if (!meetingRepository.existsById(meetingId)) {
            logger.warn("{}{}", GET_MEETING, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } 
        Meeting meeting = meetingRepository.getById(meetingId); 
        if (!lecturerId.equals(meeting.getGroup().getClassEntity().getLecturer().getId())) {
            logger.warn("{}{}", GET_MEETING, LECTURER_NOT_MANAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, LECTURER_NOT_MANAGE);

        }
        MeetingDTO meetingDTO = modelMapper.map(meeting, MeetingDTO.class);
        logger.info("Get meeting detail success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, meetingDTO);
    }
    
    public Response<MeetingDTO> getMeetingDetailByStudent(String userEmail, Integer meetingId) {
    	logger.info("getMeetingDetailByStudent(meetingId: {}, userEmail: {})", meetingId, userEmail);
    	Integer studentId = studentRepository.findStudentIdByEmail(userEmail);
        if (studentId == null || meetingId == null) {
            logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        } 
        if (!meetingRepository.existsById(meetingId)) {
            logger.warn("{}{}", GET_MEETING, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } 
        Meeting meeting = meetingRepository.getById(meetingId); 
        
        if (studentGroupRepository.isStudentExistInGroup(meeting.getGroup().getId(), studentId) == null) {
            logger.warn("{}{}", GET_MEETING, NOT_IN_GROUP);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP);
        }
        MeetingDTO meetingDTO = modelMapper.map(meeting, MeetingDTO.class);
        logger.info("Get meeting detail success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, meetingDTO);
    }
    

    public Response<Set<MeetingDTO>> getMeetingInGroupByStudent(Integer classId, Integer groupId,
                                                                Timestamp startDate, Timestamp endDate, String userEmail) {
        logger.info("getMeetingInGroupByStudent(classId: {}, groupId: {}, startDate: {}, endDate: {}, userEmail: {})", classId, groupId, startDate, endDate, userEmail);
        if (startDate == null) startDate = TIMESTAMP_MIN_VALUE;
        if (endDate == null) endDate = TIMESTAMP_MAX_VALUE;

        Integer studentId = studentRepository.findStudentIdByEmail(userEmail);
        if (studentId == null) {
            logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }

        Set<Meeting> meetingSet;
        if (classId == null && groupId == null) {           //find by student
            meetingSet = meetingRepository.findByStudentId(studentId, startDate, endDate);
        } else if (classId != null) {
            if (groupId == null) {               //classId!=null && groupId==null -> invalid case
                logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
                return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            } else {                             //classId!=null && groupId!=null -> find by group
                if (!groupRepository.existsById(groupId) || !classRepository.existsById(classId)) {
                    logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
                    return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
                }
                if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
                    logger.warn("{}{}", GET_MEETING, "Group is not exist in class.");
                    return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
                }
                if (studentGroupRepository.isStudentExistInGroup(groupId, studentId) == null) {
                    logger.warn("{}{}", GET_MEETING, NOT_IN_GROUP);
                    return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP);
                }
                meetingSet = meetingRepository.findByGroupId(groupId, startDate, endDate);
            }
        } else {                                 //classId==null && groupId!=null -> invalid case
            logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }

        Set<MeetingDTO> meetingDtoSet = meetingSet.stream()
                .map(meetingEntity -> modelMapper.map(meetingEntity, MeetingDTO.class))
                .collect(Collectors.toSet());
        logger.info("Get meeting in group success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, meetingDtoSet);
    }

    public Response<Set<MeetingDTO>> getMeetingInGroupByLecturer(Integer classId, Integer groupId,
                                                                 Timestamp startDate, Timestamp endDate, String userEmail) {
        logger.info("getMeetingInGroupByLecturer(classId: {}, groupId: {}, startDate: {}, endDate: {}, userEmail: {})", classId, groupId, startDate, endDate, userEmail);
        if (startDate == null) startDate = TIMESTAMP_MIN_VALUE;
        if (endDate == null) endDate = TIMESTAMP_MAX_VALUE;

        Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
        if (lecturerId == null) {
            logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }

        Set<Meeting> meetingSet;
        if (classId == null && groupId == null) {           //find by lecturer
            meetingSet = meetingRepository.findByLecturerId(lecturerId, startDate, endDate);
        } else if (classId != null) {
            if (!classRepository.existsById(classId)) {
                logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
                return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            }
            if (!lecturerId.equals(classRepository.findOneById(classId).getLecturer().getId())) {
                logger.warn("{}{}", GET_MEETING, LECTURER_NOT_MANAGE);
                return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, LECTURER_NOT_MANAGE);
            }
            if (groupId == null) {               //find by class
                meetingSet = meetingRepository.findByClassId(classId, startDate, endDate);
            } else {                             //group!=null -> find by group
                if (!groupRepository.existsById(groupId)) {
                    logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
                    return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
                }
                if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
                    logger.warn("{}{}", GET_MEETING, "Group is not exist in class.");
                    return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
                }
                meetingSet = meetingRepository.findByGroupId(groupId, startDate, endDate);
            }
        } else {                                 //case classId==null && groupId!=null
            logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Set<MeetingDTO> meetingDtoSet = meetingSet.stream()
                .map(meetingEntity -> modelMapper.map(meetingEntity, MeetingDTO.class))
                .collect(Collectors.toSet());
        logger.info("Get meeting in group success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, meetingDtoSet);

    }

    public Response<MeetingDTO> scheduleMeetingByLecturer(MeetingDTO meetingDTO) {
        logger.info("{}{}", SCHEDULING_MEETING_MESSAGE, meetingDTO);
        //check whether group is in class of lecturer
        if (meetingDTO.getGroupId() == null || !groupRepository.findLectureIdOfGroup(meetingDTO.getGroupId()).equals(meetingDTO.getLecturerId())) {
            logger.warn("{}{}", SCHEDULING_MEETING_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        if (meetingDTO.getTitle() == null || meetingDTO.getScheduleTime() == null || meetingDTO.getLink() == null) {
            logger.warn("{}{}", SCHEDULING_MEETING_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.UNAUTHENTICATED_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        meetingDTO.setId(null);
        Meeting meeting = modelMapper.map(meetingDTO, Meeting.class);
        meeting.setLecturer(new Lecturer(meetingDTO.getLecturerId()));
        meeting.setGroup(new Group(meetingDTO.getGroupId()));
        meetingDTO.setId(meetingRepository.save(meeting).getId());
        logger.info("{}{}", SCHEDULING_MEETING_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE,meetingDTO);
    }

    public Response<Void> updateMeetingByLecturer(MeetingDTO meetingDTO) {
        logger.info("{}{}", UPDATE_MEETING_MESSAGE, meetingDTO);
        Meeting meeting = meetingRepository.findOneById(meetingDTO.getId());
        if (!meetingDTO.getLecturerId().equals(meeting.getLecturer().getId())) {
            logger.warn("{}{}", SCHEDULING_MEETING_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        if (meetingDTO.getTitle() == null || meetingDTO.getScheduleTime() == null || meetingDTO.getLink() == null || meetingDTO.getGroupId().equals(meeting.getGroup().getId())) {
            logger.warn("{}{}", SCHEDULING_MEETING_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.UNAUTHENTICATED_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        meeting.setFeedback(meetingDTO.getFeedback());
        meeting.setLink(meetingDTO.getLink());
        meeting.setScheduleTime(meetingDTO.getScheduleTime());
        meeting.setTitle(meetingDTO.getTitle());
        meetingRepository.save(meeting);
        logger.info("{}{}", UPDATE_MEETING_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Void> deleteMeetingByLecturer(Integer lectureId, Integer meetingId) {
        logger.info("{}{}", DELETE_MEETING_MESSAGE,meetingId);
        if(!meetingRepository.findOneById(meetingId).getLecturer().getId().equals(lectureId))
        {
            logger.warn("{}{}", SCHEDULING_MEETING_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        meetingRepository.deleteById(meetingId);
        logger.info("{}{}", DELETE_MEETING_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }


}
