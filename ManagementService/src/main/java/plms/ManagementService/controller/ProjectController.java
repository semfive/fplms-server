package plms.ManagementService.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import plms.ManagementService.model.dto.ProjectDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.GroupService;
import plms.ManagementService.service.ProjectService;
import plms.ManagementService.service.StudentService;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups/{groupId}/projects")
public class ProjectController {
	@Autowired
	ProjectService projectService;
	@Autowired
	GroupService groupService;
	@Autowired
	StudentService studentService;
	
	@GetMapping
	public Response<Set<ProjectDTO>> getAllProjects(@PathVariable Integer classId) {
		return projectService.getProjectFromClass(classId);
	}
	
	@PutMapping("/{projectId}")
	public Response<Void> chooseProject(@RequestAttribute(required = false) String userEmail,
			@PathVariable Integer classId, @PathVariable Integer groupId, @PathVariable Integer projectId) {
		Integer studentId = studentService.getStudentIdByEmail(userEmail);
		return groupService.chooseProjectInGroup(classId, groupId, projectId, studentId);
	}
}
