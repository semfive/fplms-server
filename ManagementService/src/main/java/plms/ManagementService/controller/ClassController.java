package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import plms.ManagementService.model.response.ClassByStudentResponse;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.response.StudentInClassResponse;
import plms.ManagementService.model.dto.ClassDTO;
import plms.ManagementService.service.AuthenticationService;
import plms.ManagementService.service.ClassService;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/management/classes")
public class ClassController {
    @Autowired
    ClassService classService;
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping
    public Response<Void> createClassByLecturer(@RequestBody ClassDTO classDTO, @RequestAttribute(required = false) String userEmail) {

        return classService.createClassByLecturer(classDTO, userEmail);
    }

    @PutMapping
    public Response<Void> updateClassByLecturer(@RequestBody ClassDTO classDTO, @RequestAttribute(required = false) String userEmail) {

        return classService.updateClassByLecturer(classDTO, userEmail);
    }

    @DeleteMapping(value = "/{classId}")
    public Response<Void> deleteClassByLecturer(@PathVariable int classId, @RequestAttribute(required = false) String userEmail) {
        return classService.deleteClassByLecturer(classId, userEmail);
    }

    @GetMapping
    public Response<Set<ClassDTO>> getClassOfLecturer(@RequestAttribute(required = false) String userEmail) {
        return classService.getClassOfLecture(userEmail);// mock data
    }

    @GetMapping(value = "/{id}/students")
    public Response<Set<StudentInClassResponse>> getStudentInClassByLecturer(@PathVariable int id, @RequestAttribute(required = false) String userEmail) {
        return classService.getStudentInClassByLecturer(id, userEmail);
    }

    @DeleteMapping(value = "/{classId}/students/{studentId}")
    public Response<Void> removeStudentInClassByLecturer(@PathVariable int classId, @PathVariable int studentId, @RequestAttribute(required = false) String userEmail) {
        return classService.removeStudentInClassByLecturer(studentId, classId, userEmail);
    }

    @PutMapping(value = "/{classId}/students/{studentId}/groups/{groupNumber}")
    public Response<Void> changeStudentGroupByLecturer(@PathVariable int classId, @PathVariable int studentId, @PathVariable int groupNumber, @RequestAttribute(required = false) String userEmail) {
        return classService.changeStudentGroupByLecturer(studentId, classId, groupNumber, userEmail);
    }

    @PostMapping("/{classId}/enroll")
    public Response<Void> enrollStudentToClass(
            @PathVariable Integer classId,
            @RequestBody String enrollKey) {
        return classService.enrollStudentToClass(classId, 1, enrollKey);
    }

    @DeleteMapping("/{classId}/unenroll")
    public Response<Void> unenrollStudentFromClass(
            @PathVariable Integer classId) {
        //return classService.removeStudentInClass(1, classId);
        return null;
    }

    @GetMapping("/student")
    public Response<Set<ClassByStudentResponse>> getClassBySearch(
            @RequestParam(required = false, name = "search") String search) {
        return classService.getClassesBySearchStr(search, 1);
    }

}
