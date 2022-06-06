package plms.ManagementService.service;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import plms.ManagementService.model.dto.MeetingDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.MeetingRepository;
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
    GroupRepository groupRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(MeetingService.class);
    private static final String SCHEDULING_MEETING_MESSAGE = "Schedule meeting: ";

    public Response<Set<MeetingDTO>> getMeetingInGroup(Integer classId, Integer groupId,
                                                       Timestamp startDate, Timestamp endDate) {
        logger.info("getMeetingInGroup(classId: {}, groupId: {}, startDate: {}, endDate: {})", classId, groupId, startDate, endDate);

        if (classId == null || groupId == null || !classRepository.existsById(classId) ||
                !groupRepository.existsById(groupId)) {
            logger.warn("Get meeting in group: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("Get meeting in group: {}", "Group is not exist in class.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        Set<Meeting> meetingSet;
        if (startDate == null || endDate == null) {
            meetingSet = meetingRepository.findByGroup(new Group(groupId));
        } else {
            meetingSet = meetingRepository.findbyGroupIdAndTimeFilter(groupId, startDate, endDate);
        }
        Set<MeetingDTO> meetingDtoSet = meetingSet.stream()
                .map(meetingEntity -> modelMapper.map(meetingEntity, MeetingDTO.class))
                .collect(Collectors.toSet());
        logger.info("Get meeting in group success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, meetingDtoSet);
    }

    public Response<Void> scheduleMeetingByLecturer(MeetingDTO meetingDTO) {
        logger.info("{}{}",SCHEDULING_MEETING_MESSAGE, meetingDTO);
        //check whether group is in class of lecturer
        if (meetingDTO.getGroupId() == null || !groupRepository.findLectureIdOfGroup(meetingDTO.getGroupId()).equals(meetingDTO.getLecturerId())) {
            logger.warn("{}{}", SCHEDULING_MEETING_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        if (meetingDTO.getTitle() == null || meetingDTO.getScheduleTime() == null || meetingDTO.getLink() == null) {
            logger.warn("{}{}", SCHEDULING_MEETING_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.UNAUTHENTICATED_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Meeting meeting = modelMapper.map(meetingDTO, Meeting.class);
        meeting.setLecturer(new Lecturer(meetingDTO.getLecturerId()));
        meeting.setGroup(new Group(meetingDTO.getGroupId()));
        meetingRepository.save(meeting);
        logger.info("{}{}", SCHEDULING_MEETING_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

}
