package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import plms.ManagementService.model.response.ClassByStudentResponse;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.response.StudentInClassResponse;
import plms.ManagementService.model.dto.ClassDTO;
import plms.ManagementService.service.ClassService;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/management/classes")
public class ClassController {
    @Autowired
    ClassService classService;

    @PostMapping
    public Response<Void> createClass(@RequestBody ClassDTO classDTO) {
        return classService.createClass(classDTO,1);
    }

    @PutMapping
    public Response<Void> updateClass(@RequestBody ClassDTO classDTO) {
        return classService.updateClass(classDTO);
    }

    @DeleteMapping(value = "/{classId}")
    public Response<Void> deleteClass(@PathVariable int classId) {
        return classService.deleteClass(classId);
    }

    @GetMapping
    public Response<Set<ClassDTO>> getClassOfLecturer(@RequestHeader String token) {
        return classService.getClassOfLecture("t@gmail.com");// mock data
    }

    @GetMapping(value = "/{id}/students")
    public Response<Set<StudentInClassResponse>> getStudentInClass(@PathVariable int id) {
        return classService.getStudentInClass(id);
    }

    @DeleteMapping(value = "/{classId}/students/{studentId}")
    public Response<Void> removeStudentInClass(@PathVariable int classId, @PathVariable int studentId) {
        return classService.removeStudentInClass(studentId, classId);
    }

    @PutMapping(value = "/{classId}/students/{studentId}/groups/{groupNumber}")
    public Response<Void> changeStudentGroup(@PathVariable int classId, @PathVariable int studentId, @PathVariable int groupNumber) {
        return classService.changeStudentGroup(studentId, classId, groupNumber);
    }
    
    @PostMapping("/{classId}/enroll")
    public Response<Void> enrollStudentToClass(@RequestHeader String token,
    		@PathVariable Integer classId,
    		@RequestBody String enrollKey) {
    	return classService.enrollStudentToClass(classId, 1, enrollKey);
    }
    
    @DeleteMapping("/{classId}/unenroll")
    public Response<Void> unenrollStudentFromClass(@RequestHeader String token,
    		@PathVariable Integer classId) {
    	return classService.removeStudentInClass(1, classId);
    }
    
    @GetMapping("/student")
    public Response<Set<ClassByStudentResponse>> getClassBySearch(@RequestHeader String token,
    		@RequestParam(required = false, name = "search") String search) {
    	return classService.getClassesBySearchStr(search, 1);
    }

}
