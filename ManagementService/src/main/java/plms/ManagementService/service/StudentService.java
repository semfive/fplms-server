package plms.ManagementService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import plms.ManagementService.controller.response.Response;
import plms.ManagementService.dto.StudentDTO;
import plms.ManagementService.interceptor.GatewayConstant;
import plms.ManagementService.repository.StudentRepository;

@Service
public class StudentService {
    private static final String SUCCESS_MESSAGE = "Success";
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ModelMapper modelMapper;
    private static final Logger logger = LogManager.getLogger(ClassService.class);
    private static final String GET_STUDENT_BY_ID_MESSAGE = "Get student by id: ";
    public Response<StudentDTO> getStudentById(Integer studentId){
        StudentDTO studentDTO = modelMapper.map(studentRepository.findOneById(studentId),StudentDTO.class);
        if(studentDTO == null){
            logger.warn("{}{}", GET_STUDENT_BY_ID_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(GatewayConstant.UNAUTHENTICATED_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        logger.info("{}}{}",GET_STUDENT_BY_ID_MESSAGE,SUCCESS_MESSAGE);
        return new Response<>(GatewayConstant.OK_STATUS,SUCCESS_MESSAGE,studentDTO);
    }
}
