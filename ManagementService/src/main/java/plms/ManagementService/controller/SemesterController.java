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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import plms.ManagementService.model.dto.SemesterDTO;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.SemesterService;

@RestController
@RequestMapping("api/management/semesters")
public class SemesterController {
	@Autowired
	SemesterService semesterService;
	
	@GetMapping
	public Response<Set<SemesterDTO>> getSemester(@RequestParam(required = false) String code) {
		return semesterService.getSemester(code);
	}
	
	@PostMapping
	public Response<Void> addSemester(@RequestBody SemesterDTO semesterDto) {
		return semesterService.addSemester(semesterDto);
	}
	
	@PutMapping
	public Response<Void> updateSemester(@RequestBody SemesterDTO semesterDto) {
		return semesterService.updateSemester(semesterDto);
	}
	
	@DeleteMapping("/{code}")
	public Response<Void> deleteSemester(@PathVariable String code) {
		return semesterService.deleteSemester(code);
	}
	
	@PutMapping("/changeSemester/{oldSemesterCode}/{newSemesterCode}")
	public Response<Void> changeSemester(@PathVariable(name = "oldSemesterCode") String oldSemesterCode,
						@PathVariable(name = "newSemesterCode") String newSemesterCode) {
		return semesterService.changeSemester(oldSemesterCode, newSemesterCode);
	}
}
