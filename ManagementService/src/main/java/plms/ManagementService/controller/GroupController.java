package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Response<Set<GroupDTO>> getGroupOfClass(@PathVariable int classId) {
        return groupService.getGroupOfClass(classId);
    }

    @PostMapping
    public Response<Void> createGroup(@RequestBody CreateGroupRequest createGroupRequest, @PathVariable int classId) {
        createGroupRequest.setClassId(classId);
        return groupService.createGroupRequest(createGroupRequest);
    }
    @PutMapping
    public Response<Void> updateGroup(@PathVariable Integer classId,@RequestBody GroupDTO groupDTO){
        return groupService.updateGroup(classId,groupDTO);
    }
    @PutMapping("/{groupId}")
    public Response<Void> deleteGroup(@PathVariable int groupId, @PathVariable int classId) {
        return groupService.deleteGroup(groupId, classId);
    }

    @GetMapping("/{groupId}")
    public Response<GroupDetailResponse> getGroupByClassIdAndGroupId(@PathVariable Integer classId,
                                                          @PathVariable Integer groupId) {
        return groupService.getGroupByGroupIdAndClassId(groupId, classId);
    }

    @PostMapping("/{groupId}/join")
    public Response<Void> addStudentToGroup(@RequestHeader String token,
                                              @PathVariable Integer classId,
                                              @PathVariable Integer groupId) {
        //get email and role from token
        Integer studentId = 2;
        return groupService.addStudentToGroup(classId, groupId, studentId);

    }

    @DeleteMapping("/{groupId}/leave")
    public Response<Void> removeStudentFromGroup(@RequestHeader String token,
                                                   @PathVariable Integer classId,
                                                   @PathVariable Integer groupId) {
        //get email and role from token
        Integer studentId = 4;
        return groupService.removeStudentFromGroup(classId, groupId, studentId);

    }

    @DeleteMapping("/{groupId}/remove/{removeStudentId}")
    public Response<Void> removeStudentFromGroupByLeader(@RequestHeader String token,
                                                           @PathVariable Integer classId,
                                                           @PathVariable Integer groupId,
                                                           @PathVariable Integer removeStudentId) {
        //get email and role from token
        //used to check group leader
        return groupService.removeStudentFromGroup(classId, groupId, removeStudentId);
    }
    
    @PutMapping("/{groupId}/changeLeader/{newLeaderId}")
    public Response<Void> changeGroupLeader(@RequestHeader String token,
                                                           @PathVariable Integer classId,
                                                           @PathVariable Integer groupId,
                                                           @PathVariable Integer newLeaderId) {
        //get email and role from token
        //used to check group leader
    	Integer leaderId = 5;
        return groupService.changeGroupLeader(groupId, leaderId, newLeaderId);
    }
}
