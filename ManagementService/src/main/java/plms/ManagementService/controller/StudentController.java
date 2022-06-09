package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.dto.StudentDTO;
import plms.ManagementService.service.StudentService;

@RestController
@RequestMapping(value = "/api/management/students")
public class StudentController {
    @Autowired
    StudentService studentService;
    @GetMapping(value = "/{studentId}")
    public Response<StudentDTO> getStudentById(@PathVariable  int studentId){
        return studentService.getStudentById(studentId);
    }
}
