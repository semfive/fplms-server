package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import plms.ManagementService.controller.response.Response;
import plms.ManagementService.service.GroupService;
import plms.ManagementService.service.StudentService;

@RestController
@RequestMapping("/classes/{classId}/groups")
public class GroupController {
	@Autowired
	GroupService groupService;
	@Autowired
	StudentService studentService;
	
	@PutMapping("/{groupId}/join")
	public Response<String> addStudentToGroup(@RequestHeader("token") String token,
			@PathVariable("classId") Integer classId,
			@PathVariable("groupId") Integer groupId) {
		//get email and role from token
		String email = "";
		String role = "student";
		
		Integer studentId = studentService.validateStudent(role, email);
		return groupService.addStudentToGroup(classId, groupId, 1);
		
	}
	
	@PutMapping("/{groupId}/leave")
	public Response<String> removeStudentFromGroup(@RequestHeader("token") String token,
			@PathVariable("classId") Integer classId,
			@PathVariable("groupId") Integer groupId) {
		//get email and role from token
		String email = "";
		String role = "student";
		
		Integer studentId = studentService.validateStudent(role, email);
		return groupService.removeStudentFromGroup(classId, groupId, 1);
		
	}
}
