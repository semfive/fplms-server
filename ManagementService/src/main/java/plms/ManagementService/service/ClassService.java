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
import plms.ManagementService.model.dto.LecturerDTO;
import plms.ManagementService.repository.entity.*;
import plms.ManagementService.repository.entity.Class;
import plms.ManagementService.service.constant.ServiceStatusCode;
import plms.ManagementService.repository.ClassRepository;
import plms.ManagementService.repository.GroupRepository;
import plms.ManagementService.repository.LecturerRepository;
import plms.ManagementService.repository.SemesterRepository;
import plms.ManagementService.repository.StudentGroupRepository;
import plms.ManagementService.repository.StudentRepository;
import plms.ManagementService.repository.SubjectRepository;
import plms.ManagementService.service.constant.ServiceMessage;

import java.sql.Date;
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
    SubjectRepository subjectRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    GroupService groupService;
    @Autowired
    SemesterRepository semesterRepository;
    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(ClassService.class);
    private static final String CREATE_CLASS_MESSAGE = "Create class: ";
    private static final String UPDATE_CLASS_MESSAGE = "Update class: ";
    private static final String DELETE_CLASS_MESSAGE = "Delete class: ";

    private static final String INVALID_ENROLL_KEY_MESSAGE = "Enroll key is not correct";
    private static final String GET_STUDENT_IN_CLASS_MESSAGE = "Get student in class: ";
    private static final String GET_CLASS_OF_LECTURER_MESSAGE = "Get class of lecturer: ";
    private static final String REMOVE_STUDENT_IN_CLASS_MESSAGE = "Remove student in class: ";
    private static final String CHANGE_STUDENT_GROUP_MESSAGE = "Change student group: ";
    private static final String ENROLL_STUDENT_TO_CLASS_MESSAGE = "Enroll student to class: ";
    private static final String GET_CLASS_BY_STUDENT_MESSAGE = "Get class by student: ";

    public Response<Integer> createClassByLecturer(ClassDTO classDTO, String lecturerEmail) {
        logger.info("{}{}", CREATE_CLASS_MESSAGE, classDTO);
        if (!subjectRepository.existsById(classDTO.getSubjectId())) {
        	logger.warn("{}{}", CREATE_CLASS_MESSAGE, "Subject not exist.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        if (!semesterRepository.existsById(classDTO.getSemesterCode())) {
        	logger.warn("{}{}", CREATE_CLASS_MESSAGE, "Semester not exist.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Semester not exist.");
        }
        if (semesterRepository.getSemesterEndDate(classDTO.getSemesterCode()).before(new Date(System.currentTimeMillis()))) {
        	logger.warn("{}{}", CREATE_CLASS_MESSAGE, "Semester already end.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Semester already end.");
        }
        classDTO.setId(null);//jpa create class without id only
        Class classEntity = modelMapper.map(classDTO, Class.class);
        classEntity.setSemester(new Semester(classDTO.getSemesterCode()));;
        classEntity.setSubject(new Subject(classDTO.getSubjectId()));
        classEntity.setLecturer(new Lecturer(lecturerRepository.findLecturerIdByEmail(lecturerEmail)));
        classRepository.save(classEntity);
        logger.info("Create class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, classEntity.getId());
    }

    public Response<Void> updateClassByLecturer(ClassDTO classDTO, String lecturerEmail) {
        logger.info("{}{}", UPDATE_CLASS_MESSAGE, classDTO);
        //check if the class not of the lecturer
        if (!classRepository.findLecturerEmailOfClass(classDTO.getId()).equals(lecturerEmail)) {
            logger.warn("{}{}", UPDATE_CLASS_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        if (!classRepository.existsById(classDTO.getId())) {
            logger.warn("{}{}", UPDATE_CLASS_MESSAGE,ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        if (!semesterRepository.existsById(classDTO.getSemesterCode())) {
        	logger.warn("{}{}", UPDATE_CLASS_MESSAGE, "Semester not exist.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Semester not exist.");
        }
        if (!subjectRepository.existsById(classDTO.getSubjectId())) {
        	logger.warn("{}{}", UPDATE_CLASS_MESSAGE, "Subject not exist.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Subject not exist.");
        }
        Class classEntity = modelMapper.map(classDTO, Class.class);
        classEntity.setSubject(new Subject(classDTO.getSubjectId()));
        classEntity.setIsDisable(false); // class is always not disable until being deleted
        classEntity.setLecturer(new Lecturer(lecturerRepository.findLecturerIdByEmail(lecturerEmail)));
        classRepository.save(classEntity);
        logger.info("Update class success");
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
    }

    public Response<Void> deleteClassByLecturer(Integer classId, String lecturerEmail) {
        logger.info("{}{}", DELETE_CLASS_MESSAGE, classId);
        //check if the class not of the lecturer
        if (!classRepository.existsById(classId)) {
            logger.warn("{}{}", DELETE_CLASS_MESSAGE,ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        if (!classRepository.findLecturerEmailOfClass(classId).equals(lecturerEmail)) {
            logger.warn("{}{}", DELETE_CLASS_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        Class classEntity = classRepository.findOneById(classId);
        if (classEntity == null) {
            logger.warn("{}{}",DELETE_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
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

    public Response<Set<StudentInClassResponse>> getStudentInClassByLecturer(Integer classId,String lecturerEmail) {
        if (classId == null) {
            logger.warn("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        logger.info("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, classId);
        //check if the class not of the lecturer
        if (!classRepository.findLecturerEmailOfClass(classId).equals(lecturerEmail)) {
            logger.warn("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
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
                        Group group= studentRepository.findGroupByStudentIdAndClassId(studentInClassResponse.getId(), classId);
                        studentInClassResponse.setGroupId(group.getId());
                        studentInClassResponse
                                .setGroupNumber(group.getNumber());
                        studentInClassResponse
                                .setIsLeader(studentGroupRepository.findStudentLeaderRoleInClass(studentInClassResponse.getId(), classId) == 1); // change to boolean type
                    });
            logger.info("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, studentInClassResponseSet);
        }
    }

    @Transactional
    public Response<Void> removeStudentInClassByLecturer(Integer studentId, Integer classId,String lecturerEmail) {
        if (studentId == null) {
            logger.warn("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        logger.info("{}{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, classId,studentId);
        //check if the class not of the lecturer
        if (!classRepository.findLecturerEmailOfClass(classId).equals(lecturerEmail)) {
            logger.warn("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) != null) //exist
        {
            Integer groupId = groupRepository.findGroupByStudentIdAndClassId(studentId, classId);
            if (groupId != null)
            	groupService.removeStudentFromGroup(classId, studentId);
            classRepository.deleteStudentInClass(studentId, classId);      
            logger.info("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        } else {
            logger.warn("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
    }

    public Response<Void> changeStudentGroupByLecturer(Integer classId, Integer studentId, Integer groupNumber,String lecturerEmail) {
        logger.info("{}{}{}", CHANGE_STUDENT_GROUP_MESSAGE, classId,studentId);
        //check if the class not of the lecturer
        if (!classRepository.findLecturerEmailOfClass(classId).equals(lecturerEmail)) {
            logger.warn("{}{}", CHANGE_STUDENT_GROUP_MESSAGE, ServiceMessage.FORBIDDEN_MESSAGE);
            return new Response<>(ServiceStatusCode.FORBIDDEN_STATUS, ServiceMessage.FORBIDDEN_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) == null) //exist
        {
            logger.warn("{}{}", CHANGE_STUDENT_GROUP_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
        studentGroupRepository.updateStudentGroup(studentId, classId, groupNumber);
        logger.info("{}{}", CHANGE_STUDENT_GROUP_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);

    }

    public Response<Void> enrollStudentToClass(Integer classId, Integer studentId, String enrollKey) {
        logger.info("enrollStudentToClass(classId: {}, studentId: {}, enrollKey: {})", classId, studentId,  enrollKey);

        if (classId == null || studentId == null || !classRepository.existsById(classId) 
        		|| !studentRepository.existsById(studentId)) {
            logger.warn("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        } 
        if (Boolean.TRUE.equals(classRepository.getById(classId).getIsDisable())) {
        	logger.warn("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, "Class is disable");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Class is disable");
        }
        Date startDate = semesterRepository.getSemesterStartDate(classRepository.getClassSemester(classId));
        Date endDate = semesterRepository.getSemesterEndDate(classRepository.getClassSemester(classId));
        if (new Date(System.currentTimeMillis()).before(startDate)) {
        	logger.warn("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, "Class not open yet");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Class not open yet");
        } 
        if (new Date(System.currentTimeMillis()).after(endDate)) {
        	logger.warn("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, "Enroll time is over");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Enroll time is over");
        }        
        if (!classRepository.getClassEnrollKey(classId).equals(enrollKey)) {
            logger.warn("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, INVALID_ENROLL_KEY_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, INVALID_ENROLL_KEY_MESSAGE);
        } 
        if (classRepository.existsInClass(studentId, classId) != null) {
        	logger.warn("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, "Student already joined this class.");
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, "Student already joined this class.");
        } 
            classRepository.insertStudentInClass(studentId, classId);
            logger.info("{}{}", ENROLL_STUDENT_TO_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        
    }

    public Response<Set<ClassByStudentResponse>> getClassesBySearchStrByStudent(String search, Integer studentId) {
        logger.info("getClassesBySearchStrByStudent(studentId: {}, search: {})", studentId, search);

        if (studentId == null) {
            logger.warn("{}{}", GET_CLASS_BY_STUDENT_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (search == null) search = "";
        Set<Class> classSet = classRepository.getClassBySearchStr("%" + search + "%");
        Set<ClassByStudentResponse> classByStudentResponseSet = classSet.stream().map(classEntity -> {
            ClassByStudentResponse classByStudentResponse = modelMapper.map(classEntity, ClassByStudentResponse.class);
            if(classEntity.getEnrollKey() == null){
                classByStudentResponse.setHasEnrollKey(false);
            }
            else{
                classByStudentResponse.setHasEnrollKey(true);
            }
            classByStudentResponse.setSubjectId(classEntity.getSubject().getId());
            classByStudentResponse.setLecturerDto(modelMapper.map(classEntity.getLecturer(), LecturerDTO.class));
            classByStudentResponse.setJoin(classRepository.existsInClass(studentId, classEntity.getId()) != null);
            return classByStudentResponse;
        }).collect(Collectors.toSet());
        logger.info("{}{}", GET_CLASS_BY_STUDENT_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE, classByStudentResponseSet);
    }

    @Transactional
    public Response<Void> unenrollStudentInClass(Integer studentId, Integer classId) {
        logger.info("removeStudentInClass(studentId: {}, classId: {})", studentId, classId);
        if (studentId == null) {
            logger.warn("{}{}", GET_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        }
        if (classRepository.existsInClass(studentId, classId) != null) //exist
        {
            Integer groupId = groupRepository.findGroupByStudentIdAndClassId(studentId, classId);
            if (groupId != null)
            	groupService.removeStudentFromGroup(classId, studentId);
            classRepository.deleteStudentInClass(studentId, classId);
            logger.info("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.SUCCESS_MESSAGE);
            return new Response<>(ServiceStatusCode.OK_STATUS, ServiceMessage.SUCCESS_MESSAGE);
        } else {
            logger.warn("{}{}", REMOVE_STUDENT_IN_CLASS_MESSAGE, ServiceMessage.ID_NOT_EXIST_MESSAGE);
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS, ServiceMessage.ID_NOT_EXIST_MESSAGE);
        }
    }
}
