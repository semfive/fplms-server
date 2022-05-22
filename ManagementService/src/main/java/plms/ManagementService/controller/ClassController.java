package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import plms.ManagementService.controller.response.Response;
import plms.ManagementService.controller.response.StudentInClassResponse;
import plms.ManagementService.dto.ClassDTO;
import plms.ManagementService.service.ClassService;

import java.util.Set;

@RestController
@RequestMapping(value = "/management/classes")
public class ClassController {
    @Autowired
    ClassService classService;

    @PostMapping
    public Response<String> createClass(@RequestBody ClassDTO classDTO) {
        return classService.createClass(classDTO);
    }

    @PutMapping
    public Response<String> updateClass(@RequestBody ClassDTO classDTO) {
        return classService.updateClass(classDTO);
    }

    @GetMapping(value = "/{id}/students")
    public Response<Set<StudentInClassResponse>> getStudentInClass(@PathVariable int id) {
        return classService.getStudentInClass(id);
    }

    @DeleteMapping(value = "/{classId}/students/{studentId}")
    public Response<String> removeStudentInClass(@PathVariable int classId, @PathVariable int studentId) {
        return classService.removeStudentInClass(studentId, classId);
    }

    @PutMapping(value = "/{classId}/students/{studentId}/groups/{groupNumber}")
    public Response<String> changeStudentGroup(@PathVariable int classId, @PathVariable int studentId, @PathVariable int groupNumber) {
        return classService.changeStudentGroup(studentId, classId, groupNumber);
    }

}
