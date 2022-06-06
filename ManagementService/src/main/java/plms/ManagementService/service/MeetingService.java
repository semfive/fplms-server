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
import plms.ManagementService.repository.LecturerRepository;
import plms.ManagementService.repository.MeetingRepository;
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.entity.Group;
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
	private static final String GET_MEETING = "Get meeting in group: ";
	
	public Response<Set<MeetingDTO>> getMeetingInGroupByStudent(Integer classId, Integer groupId, 
			Timestamp startDate, Timestamp endDate, String userEmail) {
    	logger.info("getMeetingInGroupByStudent(classId: {}, groupId: {}, startDate: {}, endDate: {}, userEmail: {})", classId, groupId, startDate, endDate, userEmail);

		Integer studentId = studentRepository.findStudentIdByEmail(userEmail);
		if (studentId == null) {
            logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}
		if (studentGroupRepository.isStudentExistInGroup(groupId, studentId) == null) {
			logger.warn("{}{}", GET_MEETING, "Student not in group");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Student not in group");
		}
		return getMeetingInGroup(classId, groupId, startDate, endDate);
	}
	
	public Response<Set<MeetingDTO>> getMeetingInGroupByLecturer(Integer classId, Integer groupId, 
			Timestamp startDate, Timestamp endDate, String userEmail) {
    	logger.info("getMeetingInGroupByLecturer(classId: {}, groupId: {}, startDate: {}, endDate: {}, userEmail: {})", classId, groupId, startDate, endDate, userEmail);

		Integer lecturerId = lecturerRepository.findLecturerIdByEmail(userEmail);
    	if (lecturerId == null) {
            logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
		}

		if (!lecturerId.equals(classRepository.findOneById(classId).getLecturer().getId())) {
			logger.warn("{}{}", GET_MEETING, "Lecturer not manage this class.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Lecturer not manage this class.");
		}
		return getMeetingInGroup(classId, groupId, startDate, endDate);
	}
	
	public Response<Set<MeetingDTO>> getMeetingInGroup(Integer classId, Integer groupId, 
			Timestamp startDate, Timestamp endDate) {
    	logger.info("getMeetingInGroup(classId: {}, groupId: {}, startDate: {}, endDate: {})", classId, groupId, startDate, endDate);

		if (classId == null || groupId == null || !classRepository.existsById(classId) ||
				!groupRepository.existsById(groupId)) {
            logger.warn("{}{}", GET_MEETING, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
		if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
			logger.warn("{}{}", GET_MEETING, "Group is not exist in class.");
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

}
