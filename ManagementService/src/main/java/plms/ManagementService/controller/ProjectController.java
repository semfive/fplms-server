package plms.ManagementService.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import plms.ManagementService.config.interceptor.GatewayConstant;
import plms.ManagementService.model.dto.ProjectDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.GroupService;
import plms.ManagementService.service.ProjectService;
import plms.ManagementService.service.StudentService;

@RestController
@RequestMapping("/api/management/projects")
public class ProjectController {
	@Autowired
	ProjectService projectService;
	@Autowired
	GroupService groupService;
	@Autowired
	StudentService studentService;
	
	@GetMapping
	public Response<Set<ProjectDTO>> getAllProjects(@RequestParam(required = false) Integer classId,
			@RequestAttribute(required = false) String userRole,
			@RequestAttribute(required = false) String userEmail) {
		if (userRole.equals(GatewayConstant.ROLE_LECTURER)) {
			return projectService.getProjectFromClassByLecturer(classId, userEmail);
		} 
		if (userRole.equals(GatewayConstant.ROLE_STUDENT)) {
			return projectService.getProjectFromClassByStudent(classId, userEmail);
		}
		return new Response<>(403, "Not have role access");
	}
	
	@PutMapping("/{projectId}")
	public Response<Void> chooseProject(@RequestAttribute(required = false) String userEmail,
			@RequestParam Integer classId, @PathVariable Integer projectId) {
		Integer studentId = studentService.getStudentIdByEmail(userEmail);
		return groupService.chooseProjectInGroup(classId, projectId, studentId);
	}
	
	@PostMapping
	public Response<Void> addProject(@RequestAttribute(required = false) String userEmail, 
			@RequestBody ProjectDTO projectDTO) {
		return projectService.addProject(projectDTO, userEmail);
	}
	
	@PutMapping
	public Response<Void> updateProject(@RequestAttribute(required = false) String userEmail, 
			@RequestBody ProjectDTO projectDTO) {
		return projectService.updateProject(projectDTO, userEmail);
	}
	
	@DeleteMapping("/{projectId}")
	public Response<Void> deleteProject(@RequestAttribute(required = false) String userEmail, 
			@PathVariable Integer projectId) {
		return projectService.deleteProject(projectId, userEmail);
	}

	
}
