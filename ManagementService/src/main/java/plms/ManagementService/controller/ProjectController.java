package plms.ManagementService.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import plms.ManagementService.model.dto.ProjectDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.GroupService;
import plms.ManagementService.service.ProjectService;

@RestController
@RequestMapping("/api/management/classes/{classId}/groups/{groupId}/projects")
public class ProjectController {
	@Autowired
	ProjectService projectService;
	@Autowired
	GroupService groupService;
	
	@GetMapping
	public Response<Set<ProjectDTO>> getAllProjects(@PathVariable Integer classId) {
		return projectService.getProjectFromClass(classId);
	}
	
	@PutMapping("/{projectId}")
	public Response<Void> chooseProject(@PathVariable Integer classId,
										@PathVariable Integer groupId,
										@PathVariable Integer projectId) {
		return groupService.chooseProjectInGroup(classId, groupId, projectId);
	}
}
