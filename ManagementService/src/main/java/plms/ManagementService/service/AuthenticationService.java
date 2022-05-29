package plms.ManagementService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import plms.ManagementService.model.request.CreateUserRequest;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.repository.LecturerRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.entity.Lecturer;
import plms.ManagementService.repository.entity.Student;
import plms.ManagementService.service.constant.ServiceMessage;
import plms.ManagementService.service.constant.ServiceStatusCode;

@Service
public class AuthenticationService {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    private static final String CREATE_USER_MESSAGE = "Create user: ";
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    public Response<Void> createUser(CreateUserRequest createUserRequest) {
        if (createUserRequest.getIsLecturer()) {
            if (lecturerRepository.existsByEmail(createUserRequest.getEmail())) {
                logger.warn("{}{}", CREATE_USER_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
                return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            }
            lecturerRepository.save(new Lecturer(createUserRequest.getName(), createUserRequest.getEmail(), createUserRequest.getImageUrl()));

            logger.info("{}{}", CREATE_USER_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);

        }
        if (studentRepository.existsByEmail(createUserRequest.getEmail())) {
            logger.warn("{}{}", CREATE_USER_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        studentRepository.save(new Student(createUserRequest.getName(), createUserRequest.getEmail(), createUserRequest.getCode(), createUserRequest.getImageUrl()));
        logger.info("{}{}", CREATE_USER_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }
}
