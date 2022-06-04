package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import plms.ManagementService.config.interceptor.GatewayConstant;
import plms.ManagementService.model.request.CreateGroupRequest;
import plms.ManagementService.model.response.GroupDetailResponse;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.dto.GroupDTO;
import plms.ManagementService.service.GroupService;
import plms.ManagementService.service.StudentService;

import java.util.Set;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups")
public class GroupController {
    @Autowired
    GroupService groupService;
    @Autowired
    StudentService studentService;

    @GetMapping
    public Response<Set<GroupDTO>> getGroupOfClass(@PathVariable int classId
            , @RequestAttribute(required = false) String userEmail, @RequestAttribute(required = false) String userRole) {
        if (userRole.equals(GatewayConstant.ROLE_LECTURE))
            return groupService.getGroupOfClassByLecturer(classId, userEmail);
        if (userRole.equals(GatewayConstant.ROLE_STUDENT))
            return groupService.getGroupOfClassByStudent(classId, userEmail);
        return new Response<>(403, "Not have role access");
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
    public Response<GroupDetailResponse> getGroupByClassIdAndGroupId(@RequestAttribute(required = false) String userEmail,
                                                                     @PathVariable Integer classId, @PathVariable Integer groupId) {
        Integer studentId = studentService.getStudentIdByEmail(userEmail);
        return groupService.getGroupByGroupIdAndClassId(groupId, classId, studentId);
    }

    @PostMapping("/{groupId}/join")
    public Response<Void> addStudentToGroup(@RequestAttribute(required = false) String userEmail,
                                            @PathVariable Integer classId, @PathVariable Integer groupId) {
        Integer studentId = studentService.getStudentIdByEmail(userEmail);
        return groupService.addStudentToGroup(classId, groupId, studentId);

    }

    @DeleteMapping("/{groupId}/leave")
    public Response<Void> removeStudentFromGroup(@RequestAttribute(required = false) String userEmail,
                                                 @PathVariable Integer classId, @PathVariable Integer groupId) {
        Integer studentId = studentService.getStudentIdByEmail(userEmail);
        return groupService.removeStudentFromGroup(classId, groupId, studentId);

    }

    @DeleteMapping("/{groupId}/remove/{removeStudentId}")
    public Response<Void> removeStudentFromGroupByLeader(@RequestAttribute(required = false) String userEmail,
                                                         @PathVariable Integer classId, @PathVariable Integer groupId, @PathVariable Integer removeStudentId) {
        Integer leaderId = studentService.getLeaderIdByEmail(userEmail, groupId);
        return groupService.removeStudentFromGroupByLeader(classId, groupId, removeStudentId, leaderId);
    }

    @PutMapping("/{groupId}/changeLeader/{newLeaderId}")
    public Response<Void> changeGroupLeader(@RequestAttribute(required = false) String userEmail,
                                            @PathVariable Integer groupId, @PathVariable Integer newLeaderId) {
        Integer leaderId = studentService.getLeaderIdByEmail(userEmail, groupId);
        return groupService.changeGroupLeader(groupId, leaderId, newLeaderId);
    }
}
