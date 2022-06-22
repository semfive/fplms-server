package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import plms.ManagementService.model.response.ClassByStudentResponse;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.response.StudentInClassResponse;
import plms.ManagementService.model.dto.ClassDTO;
import plms.ManagementService.service.ClassService;
import plms.ManagementService.service.StudentService;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/management/classes")
public class ClassController {
    @Autowired
    ClassService classService;
    @Autowired
    StudentService studentService;

    @PostMapping
    public Response<Integer> createClassByLecturer(@RequestBody ClassDTO classDTO, @RequestAttribute(required = false) String userEmail) {

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
        return classService.getClassOfLecture(userEmail);
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
    public Response<Void> enrollStudentToClass(@RequestAttribute(required = false) String userEmail,
                                               @PathVariable Integer classId,
                                               @RequestBody String enrollKey) {
        Integer studentId = studentService.getStudentIdByEmail(userEmail);
        return classService.enrollStudentToClass(classId, studentId, enrollKey);
    }

    @DeleteMapping("/{classId}/unenroll")
    public Response<Void> unenrollStudentFromClass(@RequestAttribute(required = false) String userEmail,
                                                   @PathVariable Integer classId) {
        Integer studentId = studentService.getStudentIdByEmail(userEmail);
        return classService.unenrollStudentInClass(studentId, classId);
    }

    @GetMapping("/student")
    public Response<Set<ClassByStudentResponse>> getClassesBySearchStrByStudent(@RequestAttribute(required = false) String userEmail,
                                                                  @RequestParam(required = false, name = "search") String search) {
        Integer studentId = studentService.getStudentIdByEmail(userEmail);
        return classService.getClassesBySearchStrByStudent(search, studentId);
    }

}
