package plms.ManagementService.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import plms.ManagementService.model.response.ClassByStudentResponse;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.model.response.StudentInClassResponse;
import plms.ManagementService.model.dto.ClassDTO;
import plms.ManagementService.service.constant.ServiceStatusCode;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.LecturerRepository;
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.entity.Class;
import plms.ManagementService.repository.entity.Lecturer;
import plms.ManagementService.repository.entity.Student;
import plms.ManagementService.repository.entity.Subject;
import plms.ManagementService.service.constant.ServiceMessage;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClassService {
    @Autowired
    ClassRepository classRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    StudentGroupRepository studentGroupRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(ClassService.class);
    private static final String INVALID_ENROLL_KEY_MESSAGE = "Enroll key is not correct";
    private static final String GET_STUDENT_IN_CLASS_MESSAGE = "Get student in class: ";
    private static final String GET_CLASS_OF_LECTURER_MESSAGE = "Get class of lecturer: ";
    private static final String REMOVE_STUDENT_IN_CLASS_MESSAGE = "Remove student in class: ";
    private static final String CHANGE_STUDENT_GROUP_MESSAGE = "Change student group: ";
    private static final String ENROLL_STUDENT_TO_CLASS_MESSAGE = "Enroll student to class: ";
    private static final String GET_CLASS_BY_STUDENT_MESSAGE = "Get class by student: ";

    public Response<Void> createClass(ClassDTO classDTO) {
        classDTO.setId(null);//jpa create class without id only
        Class classEntity = modelMapper.map(classDTO, Class.class);
        classEntity.setSubject(new Subject(classDTO.getSubjectId()));
        classRepository.save(classEntity);
        logger.info("Create class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Void> updateClass(ClassDTO classDTO) {
        if (!classRepository.existsById(classDTO.getId())) {
            logger.warn("Update class: {}", ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        Class classEntity = modelMapper.map(classDTO, Class.class);
        classEntity.setSubject(new Subject(classDTO.getSubjectId()));
        classEntity.setIsDisable(false); // class is always not disable until being deleted
        classRepository.save(classEntity);
        logger.info("Update class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Void> deleteClass(Integer classId) {
        Class classEntity = classRepository.findOneById(classId);
        if (classEntity == null) {
            logger.warn("Delete class: {}", ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        classEntity.setIsDisable(true); // delete class
        classRepository.save(classEntity);
        logger.info("Delete class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Set<ClassDTO>> getClassOfLecture(String lectureEmail) {
        Lecturer lecturer = lecturerRepository.findOneByEmail(lectureEmail);
        if (lecturer == null) {
            logger.warn("{}{}", GET_CLASS_OF_LECTURER_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.UNAUTHENTICATED_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Set<ClassDTO> classDTOSet = lecturer.getClassSet().stream().map(classEntity -> {
            ClassDTO classDTO = modelMapper.map(classEntity, ClassDTO.class);
            classDTO.setSubjectId(classEntity.getSubject().getId());
            return classDTO;
        }).collect(Collectors.toSet());
        logger.info("{}{}", GET_CLASS_OF_LECTURER_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, classDTOSet);
    }

    public Response<Set<StudentInClassResponse>> getStudentInClass(Integer classId) {
        if (classId == null) {
            logger.warn("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        Set<Student> studentSet = classRepository.findOneById(classId).getStudentSet();
        if (studentSet == null) {
            logger.warn("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        } else {
            Set<StudentInClassResponse> studentInClassResponseSet = studentSet.stream()
                    .map(student -> modelMapper.map(student, StudentInClassResponse.class)).collect(Collectors.toSet());
            studentInClassResponseSet.stream()
                    .forEach(studentInClassResponse -> {
                        studentInClassResponse
                                .setGroupNumber(studentRepository.findGroupByStudentIdAndClassId(studentInClassResponse.getId(), classId));
                        studentInClassResponse
                                .setIsLeader(studentGroupRepository.findStudentLeaderRoleInClass(studentInClassResponse.getId(), classId) == 1); // change to boolean type
                    });
            logger.info("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, studentInClassResponseSet);
        }
    }

    @Transactional
    public Response<Void> removeStudentInClass(Integer studentId, Integer classId) {
        if (studentId == null) {
            logger.warn("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) != null) //exist
        {
            classRepository.deleteStudentInClass(studentId, classId);
            studentGroupRepository.deleteStudentInGroup(studentId, classId);
            logger.info("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        } else {
            logger.warn("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
    }

    public Response<Void> changeStudentGroup(Integer classId, Integer studentId, Integer groupNumber) {
        if (classRepository.existsInClass(studentId, classId) != null) //exist
        {
            studentGroupRepository.updateStudentGroup(studentId, classId, groupNumber);
            logger.info("{}{}", CHANGE_STUDENT_GROUP_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        } else {
            logger.warn("{}{}", CHANGE_STUDENT_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
    }

    public Response<Void> enrollStudentToClass(Integer classId, Integer studentId, String enrollKey) {
        if (classId == null || studentId == null || classRepository.findOneById(classId) == null) {
            logger.warn("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        } else if (!classRepository.getClassEnrollKey(classId).equals(enrollKey)) {
            logger.warn("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, INVALID_ENROLL_KEY_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, INVALID_ENROLL_KEY_MESSAGE);
        } else {
            classRepository.insertStudentInClass(studentId, classId);
            logger.info("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        }
    }

    public Response<Set<ClassByStudentResponse>> getClassesBySearchStr(String search, Integer studentId) {
        if (studentId == null) {
            logger.warn("{}{}", GET_CLASS_BY_STUDENT_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (search == null) search = "";
        Set<Class> classSet = classRepository.getClassBySearchStr("%" + search + "%");
        Set<ClassByStudentResponse> classByStudentResponseSet = classSet.stream().map(classEntity -> {
            ClassByStudentResponse classByStudentResponse = modelMapper.map(classEntity, ClassByStudentResponse.class);
            classByStudentResponse.setSubjectId(classEntity.getSubject().getId());
            classByStudentResponse.setJoin(classRepository.existsInClass(studentId, classEntity.getId()) != null);
            return classByStudentResponse;
        }).collect(Collectors.toSet());
        logger.info("{}{}", GET_CLASS_BY_STUDENT_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, classByStudentResponseSet);

    }

}
