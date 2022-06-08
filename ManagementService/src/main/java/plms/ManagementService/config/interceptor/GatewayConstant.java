package plms.ManagementService.config.interceptor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;

public class GatewayConstant {

    protected static final List<ApiEntity> apiEntities = new ArrayList<>();
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_LECTURE = "LECTURE";
     static final String ROLE_SPLIT_STRING = "&";
    public static final String EMAIL_TEST = "lec1@gmail.com";
    public static final String ROLE_TEST = ROLE_LECTURE;
    
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String PUT_METHOD = "PUT";
    private static final String DELETE_METHOD = "DELETE";

    private GatewayConstant() {
    }

    public static void addApiEntities() {
        apiEntities.add(new ApiEntity("getClassByLecturer", "/api/management/classes", GET_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("createClassByLecturer", "/api/management/classes", POST_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("updateClassByLecturer", "/api/management/classes", PUT_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("deleteClassByLecturer", "/api/management/classes/{classId:\\d+}", DELETE_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("getStudentInClassByLecturer", "/api/management/classes/{id:\\d+}/students", GET_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("changeStudentGroupByLecturer", "/api/management/classes//{classId:\\d+}/students/{studentId:\\d+}", PUT_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("getGroupOfClass", "/api/management/classes/{classId:\\d+}/groups", GET_METHOD, combineRoles(ROLE_STUDENT,ROLE_LECTURE)));
        apiEntities.add(new ApiEntity("createGroupByLecturer", "/api/management/classes/{classId:\\d+}/groups", POST_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("updateGroupByLecturer", "/api/management/classes/{classId:\\d+}/groups", PUT_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("deleteGroupByLecturer", "/api/management/classes/{classId:\\d+}/groups/{groupId:\\d+}", PUT_METHOD, ROLE_LECTURE));
        
        apiEntities.add(new ApiEntity("addProjectByLecturer", "/api/management/projects", POST_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("updateProjectByLecturer", "/api/management/projects", PUT_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("deleteProjectByLecturer", "/api/management/projects/{projectId:\\d+}", DELETE_METHOD, ROLE_LECTURE));

        apiEntities.add(new ApiEntity("feedbackReportByLecturer", "/api/management/cycle-reports/{reportId:\\d+}/feedback", PUT_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("scheduleMeeting", "/api/management/meetings", POST_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("updateMeeting", "/api/management/meetings", PUT_METHOD, ROLE_LECTURE));
        apiEntities.add(new ApiEntity("deleteMeeting", "/api/management/meetings", PUT_METHOD, ROLE_LECTURE));

        //student role
        apiEntities.add(new ApiEntity("enrollClass", "/api/management/classes/{classId:\\d+}/enroll", POST_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("unenrollClass", "/api/management/classes/{classId:\\d+}/unenroll", DELETE_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("getClassList", "/api/management/classes/student", GET_METHOD, ROLE_STUDENT));
        
        apiEntities.add(new ApiEntity("getGroupDetail", "/api/management/classes/{classId:\\d+}/groups/{groupId:\\d+}", GET_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("joinGroup", "/api/management/classes/{classId:\\d+}/groups/{groupId:\\d+}/join", POST_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("leaveGroup", "/api/management/classes/{classId:\\d+}/groups/{groupId:\\d+}/leave", DELETE_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("removeFromGroupByLeader", "/api/management/classes/{classId:\\d+}/groups/{groupId:\\d+}/remove/{removeStudentId:\\d+}", DELETE_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("changeGroupLeader", "/api/management/classes/{classId:\\d+}/groups/{groupId:\\d+}/changeLeader/{newLeaderId:\\d+}", PUT_METHOD, ROLE_STUDENT));

        apiEntities.add(new ApiEntity("getProjects", "/api/management/projects", GET_METHOD, combineRoles(ROLE_STUDENT,ROLE_LECTURE)));
        apiEntities.add(new ApiEntity("chooseProject", "/api/management/projects/{projectId:\\d+}", PUT_METHOD, ROLE_STUDENT));

        apiEntities.add(new ApiEntity("getCycleReport", "/api/management/cycle-reports", GET_METHOD, combineRoles(ROLE_STUDENT,ROLE_LECTURE)));
        apiEntities.add(new ApiEntity("addCycleReport", "/api/management/cycle-reports", POST_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("deleteCycleReport", "/api/management/cycle-reports/{reportId:\\d+}", DELETE_METHOD, ROLE_STUDENT));        
        apiEntities.add(new ApiEntity("getProgressReport", "/api/management/progress-reports", GET_METHOD, combineRoles(ROLE_STUDENT,ROLE_LECTURE)));
        apiEntities.add(new ApiEntity("addProgressReport", "/api/management/progress-reports", POST_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("deleteProgressReport", "/api/management/progress-reports/{reportId:\\d+}", DELETE_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("getMeeting", "/api/management/meetings", GET_METHOD, combineRoles(ROLE_STUDENT,ROLE_LECTURE)));

    }

    private static String combineRoles(String... roles){
        StringBuilder builder = new StringBuilder();
        for(String role: roles){
            builder.append(ROLE_SPLIT_STRING + role);
        }
        //remove the first role split string
        return builder.substring(1);
    }

}
