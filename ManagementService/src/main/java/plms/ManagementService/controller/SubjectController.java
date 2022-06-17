package plms.ManagementService.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@PostMapping
	public Response<Void> createSubject(@RequestBody SubjectDTO subjectDto) {
		return subjectService.createSubject(subjectDto);
	}
	
	@PutMapping
	public Response<Void> updateSubject(@RequestBody SubjectDTO subjectDto) {
		return subjectService.updateSubject(subjectDto);
	}
	
	@DeleteMapping("/{subjectId}")
	public Response<Void> deleteSubject(@PathVariable Integer subjectId) {
		return subjectService.deleteSubject(subjectId);
	}
}
