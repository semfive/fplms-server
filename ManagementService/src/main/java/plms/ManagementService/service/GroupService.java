package plms.ManagementService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plms.ManagementService.controller.response.Response;
import plms.ManagementService.controller.response.StudentInClassResponse;
import plms.ManagementService.dto.GroupDTO;
import plms.ManagementService.dto.StudentDTO;
import plms.ManagementService.interceptor.GatewayConstant;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.entity.Group;
import plms.ManagementService.repository.entity.Student;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {
	@Autowired
    ClassRepository classRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    StudentGroupRepository studentGroupRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    ModelMapper modelMapper;
    
    private static final Logger logger = LogManager.getLogger(ClassService.class);
    private static final String SUCCESS_MESSAGE = "Success";
    private static final String ID_NOT_EXIST_MESSAGE = "Id is not exist";
    private static final String INVALID_ARGUMENT_MESSAGE = "Invalid argument";
    private static final String JOINED_OTHER_GROUP_MESSAGE = "Student already joined other group";
    private static final String NOT_LEADER_MESSAGE = "Only leader is allowed to remove";
    private static final String NOT_IN_GROUP_MESSGAE = "Student not in group";
    private static final String GROUP_FULL_MESSGAE = "Group is full";
    private static final String ADD_STUDENT_TO_GROUP_MESSAGE = "Add student to group: ";
    private static final String REMOVE_STUDENT_FROM_GROUP_MESSAGE = "Remove student from group: ";
    private static final String GET_GROUP_IN_CLASS_MESSAGE = "Get group in class: ";

    @Transactional
    public Response<String> addStudentToGroup(Integer classId, Integer groupId, Integer studentId) {
    	if (classId == null || groupId == null || studentId == null) {
    		logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, INVALID_ARGUMENT_MESSAGE);
            return new Response<>(GatewayConstant.BAD_REQUEST_STATUS, INVALID_ARGUMENT_MESSAGE);
    	}
    	if (classRepository.existsInClass(studentId, classId) == null || 
    			groupRepository.isGroupExistsInClass(groupId, classId) == null) {
    		logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, ID_NOT_EXIST_MESSAGE);
    		return new Response<>(GatewayConstant.NOT_FOUND_STATUS, ID_NOT_EXIST_MESSAGE);
    	} else if (groupRepository.findGroupByStudentIdAndClassId(studentId, classId) != null) {
    		logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, JOINED_OTHER_GROUP_MESSAGE);
    		return new Response<String>(GatewayConstant.BAD_REQUEST_STATUS, JOINED_OTHER_GROUP_MESSAGE);
    	} else if (groupRepository.getGroupLimitNumber(groupId) <= studentGroupRepository.getCurrentNumberOfMemberInGroup(groupId)) {
    		logger.warn("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, GROUP_FULL_MESSGAE);
    		return new Response<String>(GatewayConstant.BAD_REQUEST_STATUS, GROUP_FULL_MESSGAE);
    	} else {
    		studentGroupRepository.addStudentInGroup(studentId, groupId, classId);
    		logger.info("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, SUCCESS_MESSAGE);
    		return new Response<String>(GatewayConstant.OK_STATUS, SUCCESS_MESSAGE);
    	}
    	
    }
    
    @Transactional
    public Response<String> removeStudentFromGroup(Integer classId, Integer groupId, Integer studentId) {
    	if (classId == null || groupId == null || studentId == null) {
    		logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, INVALID_ARGUMENT_MESSAGE);
            return new Response<>(GatewayConstant.BAD_REQUEST_STATUS, INVALID_ARGUMENT_MESSAGE);
    	}
    	if (classRepository.existsInClass(studentId, classId) == null || 
    			groupRepository.isGroupExistsInClass(groupId, classId) == null) {
    		logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, ID_NOT_EXIST_MESSAGE);
    		return new Response<>(GatewayConstant.NOT_FOUND_STATUS, ID_NOT_EXIST_MESSAGE);
    	} else if (!groupRepository.findGroupByStudentIdAndClassId(studentId, classId).equals(groupId)) {
    		logger.warn("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, NOT_IN_GROUP_MESSGAE);
    		return new Response<String>(GatewayConstant.BAD_REQUEST_STATUS, NOT_IN_GROUP_MESSGAE);
    	} else {
    		studentGroupRepository.deleteStudentInGroup(studentId, classId);
    		logger.info("{}{}", ADD_STUDENT_TO_GROUP_MESSAGE, SUCCESS_MESSAGE);
    		return new Response<String>(GatewayConstant.OK_STATUS, SUCCESS_MESSAGE);
    	}
    	
    }
    
    @Transactional
    public Response<String> removeStudentFromGroupByLeader(Integer classId, Integer groupId, Integer leaderStudentId, Integer removeStudentId) {
    	if (studentRepository.getGroupLeaderByClassIdAndGroupId(classId, groupId) == leaderStudentId)
    		return removeStudentFromGroup(classId, groupId, removeStudentId);
    	else {
    		logger.info("{}{}", REMOVE_STUDENT_FROM_GROUP_MESSAGE, NOT_LEADER_MESSAGE);
    		return new Response<String>(GatewayConstant.BAD_REQUEST_STATUS, NOT_LEADER_MESSAGE);
    	}
    }
    
    public Response<Group> getGroupByGroupIdAndClassId(Integer groupId, Integer classId) {
    	if (classId == null || groupId == null) {
    		logger.warn("{}{}",  GET_GROUP_IN_CLASS_MESSAGE, INVALID_ARGUMENT_MESSAGE);
    		return new Response<>(GatewayConstant.BAD_REQUEST_STATUS, INVALID_ARGUMENT_MESSAGE);
    	} else if (groupRepository.isGroupExistsInClass(groupId, classId) == null) {
    		logger.warn("{}{}", GET_GROUP_IN_CLASS_MESSAGE, ID_NOT_EXIST_MESSAGE);
    		return new Response<>(GatewayConstant.NOT_FOUND_STATUS, ID_NOT_EXIST_MESSAGE);
    	} else {
    		Group group = groupRepository.getGroupById(groupId);
    		logger.info("{}{}", GET_GROUP_IN_CLASS_MESSAGE, SUCCESS_MESSAGE);
    		return new Response<Group>(GatewayConstant.OK_STATUS, SUCCESS_MESSAGE, group);
    	}
    	
    }
    
    
    
    
    
    
    
    
    
}
