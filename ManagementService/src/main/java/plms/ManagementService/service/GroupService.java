package plms.ManagementService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plms.ManagementService.model.dto.ProjectDTO;
import plms.ManagementService.model.request.CreateGroupRequest;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.dto.GroupDTO;
import plms.ManagementService.repository.entity.Class;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.entity.Group;

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
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(GroupService.class);
    private static final String GET_GROUP_OF_CLASS = "Get group of class: ";
    private static final String JOINED_OTHER_GROUP_MESSAGE = "Student already joined other group";
    private static final String NOT_IN_GROUP_MESSAGE = "Student not in group";
    private static final String GROUP_FULL_MESSAGE = "Group is full";
    private static final String ADD_STUDENT_TO_GROUP_MESSAGE = "Add student to group: ";
    private static final String REMOVE_STUDENT_FROM_GROUP_MESSAGE = "Remove student from group: ";
    private static final String GET_GROUP_IN_CLASS_MESSAGE = "Get group in class: ";

    @Transactional
    public Response<String> createGroupRequest(CreateGroupRequest createGroupRequest) {
        if (createGroupRequest.getGroupQuantity() == null || createGroupRequest.getMemberQuantity() == null) {
            logger.warn("{}{}", "Create group : ", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Group group = modelMapper.map(createGroupRequest, Group.class);
        group.setId(null);
        logger.error("Group id:{}",group.getId());
        group.setClassEntity(new Class(createGroupRequest.getClassId()));
        //create group with amount quantity
        Integer startGroupNumber = groupRepository.getMaxGroupNumber(createGroupRequest.getClassId());
        if (startGroupNumber == null) // there is no group in class
            startGroupNumber = 0;
        startGroupNumber++;
        for (int index = startGroupNumber; index < startGroupNumber + createGroupRequest.getMemberQuantity(); index++) {
            group = modelMapper.map(createGroupRequest, Group.class);
            group.setId(null);
            group.setClassEntity(new Class(createGroupRequest.getClassId()));
            group.setNumber(index);
            groupRepository.save(group);
        }
        logger.info("{}{}", GET_GROUP_OF_CLASS, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Set<GroupDTO>> getGroupOfClass(Integer classId) {
        Class classEntity = classRepository.findOneById(classId);
        if (classEntity == null) {
            logger.warn("{}{}", GET_GROUP_OF_CLASS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        Set<GroupDTO> groupDTOSet = classEntity.getGroupSet().stream().map(group -> {
                    GroupDTO groupDTO = modelMapper.map(group, GroupDTO.class);
                    if (group.getProject() != null)
                        groupDTO.setProjectDTO(modelMapper.map(group.getProject(), ProjectDTO.class));
                    return groupDTO;
                })
                .collect(Collectors.toSet());
        logger.info("{}{}", GET_GROUP_OF_CLASS, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, groupDTOSet);
    }

    @Transactional
    public Response<String> addStudentToGroup(Integer classId, Integer groupId, Integer studentId) {
        if (classId == null || groupId == null || studentId == null) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) == null ||
                groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } else if (groupRepository.findGroupByStudentIdAndClassId(studentId, classId) != null) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, JOINED_OTHER_GROUP_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, JOINED_OTHER_GROUP_MESSAGE);
        } else if (groupRepository.getGroupLimitNumber(groupId) <= studentGroupRepository.getCurrentNumberOfMemberInGroup(groupId)) {
            logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, GROUP_FULL_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, GROUP_FULL_MESSAGE);
        } else {
            studentGroupRepository.addStudentInGroup(studentId, groupId, classId);
            logger.info("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        }

    }

    @Transactional
    public Response<String> removeStudentFromGroup(Integer classId, Integer groupId, Integer studentId) {
        if (classId == null || groupId == null || studentId == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) == null ||
                groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } else if (!groupRepository.findGroupByStudentIdAndClassId(studentId, classId).equals(groupId)) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, NOT_IN_GROUP_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP_MESSAGE);
        } else {
            studentGroupRepository.deleteStudentInGroup(studentId, classId);
            logger.info("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        }

    }

    public Response<GroupDTO> getGroupByGroupIdAndClassId(Integer groupId, Integer classId) {
        if (classId == null || groupId == null) {
            logger.warn("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        } else if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } else {
            Group group = groupRepository.getGroupById(groupId);
            GroupDTO groupDTO = modelMapper.map(group, GroupDTO.class);
            logger.info("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, groupDTO);
        }
    }


}
