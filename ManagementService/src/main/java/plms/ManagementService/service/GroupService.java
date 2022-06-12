package plms.ManagementService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plms.ManagementService.model.dto.ProjectDTO;
import plms.ManagementService.model.dto.StudentDTO;
import plms.ManagementService.model.request.CreateGroupRequest;
import plms.ManagementService.model.response.GroupDetailResponse;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.dto.GroupDTO;
import plms.ManagementService.repository.*;
import plms.ManagementService.repository.entity.Class;
import plms.ManagementService.repository.entity.Project;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;
import plms.ManagementService.repository.entity.Group;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    ClassRepository classRepository;
    @Autowired
    StudentGroupRepository studentGroupRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(GroupService.class);
    private static final String CREATE_GROUP_MESSAGE = "Create group : ";
    private static final String UPDATE_GROUP_MESSAGE = "Update group : ";
    private static final String DELETE_GROUP_MESSAGE = "Delete group : ";

    private static final String GET_GROUP_OF_CLASS_MESSAGE = "Get group of class: ";
    private static final String JOINED_OTHER_GROUP_MESSAGE = "Student already joined other group";
    private static final String NOT_IN_GROUP_MESSAGE = "Student not in group";
    private static final String GROUP_FULL_MESSAGE = "Group is full";
    private static final String ENROLL_TIME_OVER = "Enroll time is over";
    private static final String ADD_STUDENT_TO_GROUP_MESSAGE = "Add student to group: ";
    private static final String REMOVE_STUDENT_FROM_GROUP_MESSAGE = "Remove student from group: ";
    private static final String GET_GROUP_IN_CLASS_MESSAGE = "Get group in class: ";
    private static final String CHANGE_GROUP_LEADER_MESSAGE = "Change group leader: ";
    private static final String CHOOSE_PROJECT = "Choose project in group: ";

    @Transactional
    public Response<Void> createGroupRequestByLecturer(CreateGroupRequest createGroupRequest, String lecturerEmail) {
        logger.info("{}{}", CREATE_GROUP_MESSAGE, createGroupRequest);
        //check if the class not of the lecturer
        if (!classRepository.findLecturerEmailOfClass(createGroupRequest.getClassId()).equals(lecturerEmail)) {
            logger.warn("{}{}", CREATE_GROUP_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        if (createGroupRequest.getGroupQuantity() == null || createGroupRequest.getMemberQuantity() == null) {
            logger.warn("{}{}", CREATE_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Group group = modelMapper.map(createGroupRequest, Group.class);
        group.setId(null);
        logger.error("Group id:{}", group.getId());
        group.setClassEntity(new Class(createGroupRequest.getClassId()));
        //create group with amount quantity
        Integer startGroupNumber = groupRepository.getMaxGroupNumber(createGroupRequest.getClassId());
        if (startGroupNumber == null) // there is no group in class
            startGroupNumber = 0;
        startGroupNumber++;
        for (int index = startGroupNumber; index < startGroupNumber + createGroupRequest.getGroupQuantity(); index++) {
            group = modelMapper.map(createGroupRequest, Group.class);
            group.setId(null);
            group.setClassEntity(new Class(createGroupRequest.getClassId()));
            group.setNumber(index);
            groupRepository.save(group);
        }
        logger.info("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Void> updateGroupByLecturer(Integer classId, GroupDTO groupDTO, String lecturerEmail) {
        logger.info("{}{}", UPDATE_GROUP_MESSAGE, groupDTO);
        //check if the class not of the lecturer
        if (!classRepository.findLecturerEmailOfClass(classId).equals(lecturerEmail)) {
            logger.warn("{}{}", UPDATE_GROUP_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        Integer groupNumber = groupRepository.findGroupNumber(groupDTO.getId(), classId);
        if (groupDTO.getId() == null || groupNumber == null) {
            logger.warn("Update group: {}", ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        // not accept chane in the same group
        if(groupNumber.equals(groupDTO.getNumber())){
            logger.warn("Update group: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Group number already exist");
        }
        Group group = modelMapper.map(groupDTO, Group.class);
        group.setClassEntity(new Class(classId));
        if (groupDTO.getProjectDTO() != null)
        	group.setProject(new Project(groupDTO.getProjectDTO().getId()));
        groupRepository.save(group);
        logger.info("Update group success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Void> deleteGroupByLecturer(Integer groupId, Integer classId, String lecturerEmail) {
        logger.info("{}{}{}", DELETE_GROUP_MESSAGE, groupId, classId);
        //check if the class not of the lecturer
        if (!classRepository.findLecturerEmailOfClass(classId).equals(lecturerEmail)) {
            logger.warn("{}{}", DELETE_GROUP_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", DELETE_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        studentGroupRepository.deleteAllStudentInGroup(groupId);
        groupRepository.delete(new Group(groupId));
        logger.info("Delete group success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Set<GroupDetailResponse>> getGroupOfClassByLecturer(Integer classId, String lecturerEmail) {
        logger.info("{}{}", GET_GROUP_OF_CLASS_MESSAGE, classId);
        //check if the class not of the lecturer  
        if (!lecturerEmail.equals(classRepository.findLecturerEmailOfClass(classId))) {
            logger.warn("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        Class classEntity = classRepository.findOneById(classId);
        if (classEntity == null) {
            logger.warn("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        Set<GroupDetailResponse> groupDetailResponses = classEntity.getGroupSet().stream().map(group -> {
        	GroupDetailResponse groupDetailResponse = modelMapper.map(group, GroupDetailResponse.class);
        		groupDetailResponse.setMemberQuantity(group.getMemberQuantity());            
        		groupDetailResponse.setCurrentNumber(studentGroupRepository.getCurrentNumberOfMemberInGroup(group.getId()));
                    if (group.getProject() != null)
                    	groupDetailResponse.setProjectDTO(modelMapper.map(group.getProject(), ProjectDTO.class));
                    return groupDetailResponse;
                })
                .collect(Collectors.toSet());
        logger.info("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, groupDetailResponses);
    }

    public Response<Set<GroupDetailResponse>> getGroupOfClassByStudent(Integer classId, String studentEmail) {
        logger.info("{}{}", GET_GROUP_OF_CLASS_MESSAGE, classId);
        //check if the class not of the student
        Integer studentId = studentRepository.findStudentIdByEmail(studentEmail);
        if (classRepository.existsInClass(studentId, classId) == null) {
            logger.warn("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        Class classEntity = classRepository.findOneById(classId);
        if (classEntity == null) {
            logger.warn("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        Set<GroupDetailResponse> groupDetailResponses = classEntity.getGroupSet().stream().map(group -> {
        	GroupDetailResponse groupDetailResponse = modelMapper.map(group, GroupDetailResponse.class);
        		groupDetailResponse.setMemberQuantity(group.getMemberQuantity());            
        		groupDetailResponse.setCurrentNumber(studentGroupRepository.getCurrentNumberOfMemberInGroup(group.getId()));
                    if (group.getProject() != null)
                    	groupDetailResponse.setProjectDTO(modelMapper.map(group.getProject(), ProjectDTO.class));
                    return groupDetailResponse;
                })
                .collect(Collectors.toSet());
        logger.info("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, groupDetailResponses);
    }

    @Transactional
    public Response<Void> addStudentToGroup(Integer classId, Integer groupId, Integer studentId) {
        logger.info("addStudentToGroup(classId: {}, groupId: {}, studentId: {})", classId, groupId, studentId);

        if (classId == null || groupId == null || studentId == null) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) == null ||
                groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        if (groupRepository.findGroupByStudentIdAndClassId(studentId, classId) != null) {
            logger.warn("{}{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, groupRepository.findGroupByStudentIdAndClassId(studentId, classId), JOINED_OTHER_GROUP_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, JOINED_OTHER_GROUP_MESSAGE);
        }
        if (groupRepository.getGroupLimitNumber(groupId) <= studentGroupRepository.getCurrentNumberOfMemberInGroup(groupId)) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, GROUP_FULL_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_FULL_MESSAGE);
        }
        if (groupRepository.isEnrollTimeOver(groupId, new Timestamp(System.currentTimeMillis())) == null) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ENROLL_TIME_OVER);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ENROLL_TIME_OVER);
        }

        if (studentGroupRepository.findLeaderInGroup(groupId) == null)
            studentGroupRepository.addStudentInGroup(studentId, groupId, classId, 1); // 1 is Boolean.TRUE
        else
            studentGroupRepository.addStudentInGroup(studentId, groupId, classId, 0); // 0 is Boolean.FALSE
        logger.info("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    @Transactional
    public Response<Void> removeStudentFromGroupByLeader(Integer classId, Integer studentId, Integer leaderId) {
        logger.info("removeStudentFromGroupByLeader(classId: {}, studentId: {}, leaderId: {})", classId, studentId, leaderId);

        if (classId == null || studentId == null || leaderId == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Integer groupId = groupRepository.findGroupByStudentIdAndClassId(studentId, classId);
        if (!leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        return removeStudentFromGroup(classId, studentId);
    }

    @Transactional
    public Response<Void> removeStudentFromGroup(Integer classId, Integer studentId) {
        logger.info("removeStudentFromGroup(classId: {}, studentId: {})", classId, studentId);

        if (classId == null || studentId == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        Integer groupId = groupRepository.findGroupByStudentIdAndClassId(studentId, classId);
        if (groupId == null) {
        	logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, NOT_IN_GROUP_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP_MESSAGE);
        }
        if (groupRepository.isEnrollTimeOver(groupId, new Timestamp(System.currentTimeMillis())) == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ENROLL_TIME_OVER);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ENROLL_TIME_OVER);
        }
        if (studentId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            Integer newLeaderId = studentGroupRepository.chooseRandomGroupMember(groupId);
            if (newLeaderId != null) {
                studentGroupRepository.addRandomGroupLeader(groupId, newLeaderId);
            }
        }
        studentGroupRepository.deleteStudentInGroup(studentId, classId);

        logger.info("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);

    }

    public Response<GroupDetailResponse> getGroupByClassId(Integer classId, Integer studentId) {
        logger.info("getGroupByGroupIdAndClassId(classId: {}, studentId: {})", classId, studentId);

        if (classId == null || studentId == null || !classRepository.existsById(classId)) {
            logger.warn("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Integer groupId = groupRepository.findGroupByStudentIdAndClassId(studentId, classId);
        if (groupId == null) {
            logger.warn("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Group group = groupRepository.findOneById(groupId);
        GroupDetailResponse groupDetailResponse = modelMapper.map(group, GroupDetailResponse.class);
        groupDetailResponse.setStudentDtoSet(group.getStudentGroupSet().stream()
               .map(studentGroupEntity -> modelMapper.map(studentGroupEntity.getStudent(), StudentDTO.class))
               .collect(Collectors.toSet()));
		groupDetailResponse.setCurrentNumber(studentGroupRepository.getCurrentNumberOfMemberInGroup(group.getId()));
        groupDetailResponse.setLeaderId(studentGroupRepository.findLeaderInGroup(groupId));
        if (group.getProject() != null)
            groupDetailResponse.setProjectDTO(modelMapper.map(group.getProject(), ProjectDTO.class));
       logger.info("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, groupDetailResponse);
        
    }

    @Transactional
    public Response<Void> changeGroupLeader(Integer classId, Integer leaderId, Integer newLeaderId) {
        logger.info("changeGroupLeader(classId: {}, leaderId: {}, newLeaderId: {})", classId, leaderId, newLeaderId);

        if (classId == null || leaderId == null || newLeaderId == null) {
            logger.warn("{}{}", CHANGE_GROUP_LEADER_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Integer groupId = groupRepository.findGroupByStudentIdAndClassId(leaderId, classId);
        if (studentGroupRepository.isStudentExistInGroup(groupId, newLeaderId) == null ||
                !leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("{}{}", CHANGE_GROUP_LEADER_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        if (groupRepository.isEnrollTimeOver(groupId, new Timestamp(System.currentTimeMillis())) == null) {
            logger.warn("{}{}", CHANGE_GROUP_LEADER_MESSAGE, ENROLL_TIME_OVER);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ENROLL_TIME_OVER);
        }
        studentGroupRepository.updateGroupLeader(groupId, leaderId, 0);     //remove old leader
        studentGroupRepository.updateGroupLeader(groupId, newLeaderId, 1);   //add new leader
        logger.info("{}{}", CHANGE_GROUP_LEADER_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    @Transactional
    public Response<Void> chooseProjectInGroup(Integer classId, Integer projectId, Integer studentId) {
        logger.info("chooseProjectInGroup(classId: {}, projectId: {}, studentId: {})", classId, projectId, studentId);

        if (classId == null || projectId == null || studentId == null) {
            logger.warn("{}{}", CHOOSE_PROJECT, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Integer groupId = groupRepository.findGroupByStudentIdAndClassId(studentId, classId);
        if (!studentId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("{}{}", CHOOSE_PROJECT, "Not a leader");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Not a leader");
        }
        if (!projectRepository.existsById(projectId)) {
            logger.warn("{}{}", CHOOSE_PROJECT, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        if (groupRepository.isEnrollTimeOver(groupId, new Timestamp(System.currentTimeMillis())) == null) {
            logger.warn("{}{}", CHOOSE_PROJECT, ENROLL_TIME_OVER);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ENROLL_TIME_OVER);
        }
        groupRepository.updateProjectInGroup(groupId, projectId);
        logger.info("Choose project in group success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

}
