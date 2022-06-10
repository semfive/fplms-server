package plms.ManagementService.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import plms.ManagementService.model.dto.SubjectDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.SubjectService;

@RestController
@RequestMapping("/api/management/subjects")

public class SubjectController {
	@Autowired
	SubjectService subjectService;
	
	@GetMapping
	public Response<Set<SubjectDTO>> getSubjects() {
		return subjectService.getSubjects();
	}
}
