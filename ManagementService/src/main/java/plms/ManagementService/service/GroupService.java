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
    private static final String ADD_STUDENT_TO_GROUP_MESSAGE = "Add student to group: ";
    private static final String REMOVE_STUDENT_FROM_GROUP_MESSAGE = "Remove student from group: ";
    private static final String GET_GROUP_IN_CLASS_MESSAGE = "Get group in class: ";
    private static final String CHANGE_GROUP_LEADER_MESSAGE = "Change group leader: ";

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
        for (int index = startGroupNumber; index < startGroupNumber + createGroupRequest.getMemberQuantity(); index++) {
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
        if (groupDTO.getId() == null || groupRepository.isGroupExistsInClass(groupDTO.getId(), classId) == null) {
            logger.warn("Update group: {}", ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.NOT_FOUND_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        Group group = modelMapper.map(groupDTO, Group.class);
        group.setClassEntity(new Class(classId));
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
        logger.info("Delete group success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Set<GroupDTO>> getGroupOfClassByLecturer(Integer classId, String lecturerEmail) {
        logger.info("{}{}", GET_GROUP_OF_CLASS_MESSAGE, classId);
        //check if the class not of the lecturer
        if (!classRepository.findLecturerEmailOfClass(classId).equals(lecturerEmail)) {
            logger.warn("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        Class classEntity = classRepository.findOneById(classId);
        if (classEntity == null) {
            logger.warn("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        Set<GroupDTO> groupDTOSet = classEntity.getGroupSet().stream().map(group -> {
                    GroupDTO groupDTO = modelMapper.map(group, GroupDTO.class);
                    if (group.getProject() != null)
                        groupDTO.setProjectDTO(modelMapper.map(group.getProject(), ProjectDTO.class));
                    return groupDTO;
                })
                .collect(Collectors.toSet());
        logger.info("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, groupDTOSet);
    }

    public Response<Set<GroupDTO>> getGroupOfClassByStudent(Integer classId, String studentEmail) {
        logger.info("{}{}", GET_GROUP_OF_CLASS_MESSAGE, classId);
        //check if the class not of the lecturer
        if (classRepository.existsInClass(studentRepository.findStudentIdByEmail(studentEmail), classId) == null) {
            logger.warn("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        Class classEntity = classRepository.findOneById(classId);
        if (classEntity == null) {
            logger.warn("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        Set<GroupDTO> groupDTOSet = classEntity.getGroupSet().stream().map(group -> {
                    GroupDTO groupDTO = modelMapper.map(group, GroupDTO.class);
                    if (group.getProject() != null)
                        groupDTO.setProjectDTO(modelMapper.map(group.getProject(), ProjectDTO.class));
                    return groupDTO;
                })
                .collect(Collectors.toSet());
        logger.info("{}{}", GET_GROUP_OF_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, groupDTOSet);
    }

    @Transactional
    public Response<Void> addStudentToGroup(Integer classId, Integer groupId, Integer studentId) {
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

        if (studentGroupRepository.findLeaderInGroup(groupId) == null)
            studentGroupRepository.addStudentInGroup(studentId, groupId, classId, 1); // 1 is Boolean.TRUE
        else
            studentGroupRepository.addStudentInGroup(studentId, groupId, classId, 0); // 0 is Boolean.FALSE
        logger.info("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    @Transactional
    public Response<Void> removeStudentFromGroup(Integer classId, Integer groupId, Integer studentId) {
        if (classId == null || groupId == null || studentId == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) == null ||
                groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        if (!groupId.equals(groupRepository.findGroupByStudentIdAndClassId(studentId, classId))) {
            logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, NOT_IN_GROUP_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, NOT_IN_GROUP_MESSAGE);
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

    public Response<GroupDetailResponse> getGroupByGroupIdAndClassId(Integer groupId, Integer classId) {
        if (classId == null || groupId == null || !groupRepository.existsById(groupId) ||
                !classRepository.existsById(classId)) {
            logger.warn("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        } else if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
            logger.warn("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } else {
            Group group = groupRepository.findOneById(groupId);
            GroupDetailResponse groupDetailResponse = modelMapper.map(group, GroupDetailResponse.class);
            groupDetailResponse.setStudentDtoSet(group.getStudentGroupSet().stream()
                    .map(studentGroupEntity -> modelMapper.map(studentGroupEntity.getStudent(), StudentDTO.class))
                    .collect(Collectors.toSet()));
            groupDetailResponse.setLeaderId(studentGroupRepository.findLeaderInGroup(groupId));
            if (group.getProject() != null)
                groupDetailResponse.setProjectDTO(modelMapper.map(group.getProject(), ProjectDTO.class));
            logger.info("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, groupDetailResponse);
        }
    }

    public Response<Void> changeGroupLeader(Integer groupId, Integer leaderId, Integer newLeaderId) {
        if (groupId == null || leaderId == null || newLeaderId == null) {
            logger.warn("{}{}", CHANGE_GROUP_LEADER_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (studentGroupRepository.isStudentExistInGroup(groupId, newLeaderId) == null ||
                leaderId.equals(studentGroupRepository.findLeaderInGroup(groupId))) {
            logger.warn("{}{}", CHANGE_GROUP_LEADER_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        studentGroupRepository.updateGroupLeader(groupId, leaderId, 0);     //remove old leader
        studentGroupRepository.updateGroupLeader(groupId, newLeaderId, 1);   //add new leader
        logger.info("{}{}", CHANGE_GROUP_LEADER_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Void> chooseProjectInGroup(Integer classId, Integer groupId, Integer projectId) {
        if (classId == null || groupId == null || projectId == null) {
            logger.warn("Choose project in group: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (groupRepository.isGroupExistsInClass(groupId, classId) == null ||
                projectRepository.isProjectExistsInClass(projectId, classId) == null) {
            logger.warn("Choose project in group: {}", ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        groupRepository.updateProjectinGroup(groupId, projectId);
        logger.warn("Choose project in group success.");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }


}
