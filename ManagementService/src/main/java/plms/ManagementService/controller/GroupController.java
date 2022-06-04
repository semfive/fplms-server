package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import plms.ManagementService.config.interceptor.GatewayConstant;
import plms.ManagementService.model.request.CreateGroupRequest;
import plms.ManagementService.model.response.GroupDetailResponse;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.dto.GroupDTO;
import plms.ManagementService.service.GroupService;

import java.util.Set;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups")
public class GroupController {
    @Autowired
    GroupService groupService;

    @GetMapping
    public Response<Set<GroupDTO>> getGroupOfClassByLecturer(@PathVariable int classId
            , @RequestAttribute(required = false) String userEmail,@RequestAttribute(required = false) String userRole) {
        if(userRole.equals(GatewayConstant.ROLE_LECTURE))
        return groupService.getGroupOfClassByLecturer(classId, userEmail);
        if (userRole.equals(GatewayConstant.ROLE_STUDENT))
            return groupService.getGroupOfClassByStudent(classId,userEmail);
        return new Response<>(403,"Not have role access");
    }


    @PostMapping
    public Response<Void> createGroupByLecturer(@RequestBody CreateGroupRequest createGroupRequest, @PathVariable int classId, @RequestAttribute(required = false) String userEmail) {
        createGroupRequest.setClassId(classId);
        return groupService.createGroupRequestByLecturer(createGroupRequest, userEmail);
    }

    @PutMapping
    public Response<Void> updateGroupByLecturer(@PathVariable Integer classId, @RequestBody GroupDTO groupDTO, @RequestAttribute(required = false) String userEmail) {
        return groupService.updateGroupByLecturer(classId, groupDTO, userEmail);
    }

    @PutMapping("/{groupId}")
    public Response<Void> deleteGroupByLecturer(@PathVariable int groupId, @PathVariable int classId, @RequestAttribute(required = false) String userEmail) {
        return groupService.deleteGroupByLecturer(groupId, classId, userEmail);
    }

    @GetMapping("/{groupId}")
    public Response<GroupDetailResponse> getGroupByClassIdAndGroupId(@PathVariable Integer classId,
                                                                     @PathVariable Integer groupId) {
        return groupService.getGroupByGroupIdAndClassId(groupId, classId);
    }

    @PostMapping("/{groupId}/join")
    public Response<Void> addStudentToGroup(
            @PathVariable Integer classId,
            @PathVariable Integer groupId) {
        //get email and role from token
        Integer studentId = 2;
        return groupService.addStudentToGroup(classId, groupId, studentId);

    }

    @DeleteMapping("/{groupId}/leave")
    public Response<Void> removeStudentFromGroup(
            @PathVariable Integer classId,
            @PathVariable Integer groupId) {
        //get email and role from token
        Integer studentId = 4;
        return groupService.removeStudentFromGroup(classId, groupId, studentId);

    }

    @DeleteMapping("/{groupId}/remove/{removeStudentId}")
    public Response<Void> removeStudentFromGroupByLeader(
            @PathVariable Integer classId,
            @PathVariable Integer groupId,
            @PathVariable Integer removeStudentId) {
        //get email and role from token
        //used to check group leader
        return groupService.removeStudentFromGroup(classId, groupId, removeStudentId);
    }

    @PutMapping("/{groupId}/changeLeader/{newLeaderId}")
    public Response<Void> changeGroupLeader(
            @PathVariable Integer classId,
            @PathVariable Integer groupId,
            @PathVariable Integer newLeaderId) {
        //get email and role from token
        //used to check group leader
        Integer leaderId = 5;
        return groupService.changeGroupLeader(groupId, leaderId, newLeaderId);
    }
}
