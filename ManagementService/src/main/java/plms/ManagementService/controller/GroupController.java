package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import plms.ManagementService.controller.response.Response;
import plms.ManagementService.dto.GroupDTO;
import plms.ManagementService.repository.entity.Group;
import plms.ManagementService.service.GroupService;
import plms.ManagementService.service.StudentService;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups")
public class GroupController {
	@Autowired
	GroupService groupService;
	@Autowired
	StudentService studentService;
	
	@GetMapping("/{groupId}")
	public Response<GroupDTO> getGroupByClassIdAndGroupId(@PathVariable("classId") Integer classId,
			@PathVariable("groupId") Integer groupId) {
		return groupService.getGroupByGroupIdAndClassId(groupId, classId);
	}
	
	@PostMapping("/{groupId}/join")
	public Response<String> addStudentToGroup(@RequestHeader("token") String token,
			@PathVariable("classId") Integer classId,
			@PathVariable("groupId") Integer groupId) {
		//get email and role from token
		String email = "";
		String role = "student";
		
		Integer studentId = 1;
		return groupService.addStudentToGroup(classId, groupId, studentId);
		
	}
	
	@DeleteMapping("/{groupId}/leave")
	public Response<String> removeStudentFromGroup(@RequestHeader("token") String token,
			@PathVariable("classId") Integer classId,
			@PathVariable("groupId") Integer groupId) {
		//get email and role from token
		String email = "";
		String role = "student";
		
		Integer studentId = 1;
		return groupService.removeStudentFromGroup(classId, groupId, studentId);
		
	}
	
	@DeleteMapping("/{groupId}/remove/{removeStudentId}")
	public Response<String> removeStudentFromGroupByLeader(@RequestHeader("token") String token,
			@PathVariable("classId") Integer classId,
			@PathVariable("groupId") Integer groupId,
			@PathVariable("removeStudentId") Integer removeStudentId) {
		//get email and role from token
		String email = "";
		String role = "student";
		
		Integer studentId = 1; //used to check group leader
		return groupService.removeStudentFromGroup(classId, groupId, removeStudentId);
		
	}
}