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
    public static final String EMAIL_TEST = "stu2@gmail.com";
    public static final String ROLE_TEST = ROLE_STUDENT;
    
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
        
        //student role
        apiEntities.add(new ApiEntity("enrollClass", "/api/management/classes/{classId:[0-9]+}/enroll", POST_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("unenrollClass", "/api/management/classes/{classId:[0-9]+}/unenroll", DELETE_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("getClassList", "/api/management/classes/student", GET_METHOD, ROLE_STUDENT));
        
        apiEntities.add(new ApiEntity("getGroupDetail", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}", GET_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("joinGroup", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/join", POST_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("leaveGroup", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/leave", DELETE_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("removeFromGroupByLeader", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/remove/{removeStudentId:[0-9]+}", DELETE_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("changeGroupLeader", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/changeLeader/{newLeaderId:[0-9]+}", PUT_METHOD, ROLE_STUDENT));

        apiEntities.add(new ApiEntity("getProjects", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/projects", GET_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("chooseProject", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/projects/{projectId:[0-9]+}", PUT_METHOD, ROLE_STUDENT));

        apiEntities.add(new ApiEntity("getCycleReport", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/cycle-reports", GET_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("addCycleReport", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/cycle-reports", POST_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("deleteCycleReport", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/cycle-reports/{reportId:[0-9]+}", DELETE_METHOD, ROLE_STUDENT));        
        apiEntities.add(new ApiEntity("getProgressReport", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/progress-reports", GET_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("addProgressReport", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/progress-reports", POST_METHOD, ROLE_STUDENT));
        apiEntities.add(new ApiEntity("deleteProgressReport", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/progress-reports/{reportId:[0-9]+}", DELETE_METHOD, ROLE_STUDENT));
        
        apiEntities.add(new ApiEntity("getMeeting", "/api/management/classes/{classId:[0-9]+}/groups/{groupId:[0-9]+}/meetings", GET_METHOD, ROLE_STUDENT));

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
