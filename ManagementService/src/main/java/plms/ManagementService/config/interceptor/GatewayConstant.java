package plms.ManagementService.config.interceptor;

import java.util.ArrayList;
import java.util.List;

public class GatewayConstant {

    protected static final List<ApiEntity> apiEntities = new ArrayList<>();
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_LECTURE = "LECTURE";
     static final String ROLE_SPLIT_STRING = "&";
    public static final String EMAIL_TEST = "t@gmail.com";
    public static final String ROLE_TEST = ROLE_LECTURE;
    private GatewayConstant() {
    }

    public static void addApiEntities() {
        apiEntities.add(new ApiEntity("createClass","/api/management/classes","POST",ROLE_LECTURE));
        
        //student role
        apiEntities.add(new ApiEntity("enrollClass", "/api/management/classes/{classId}/enroll", "POST", ROLE_STUDENT));
        apiEntities.add(new ApiEntity("unenrollClass", "/api/management/classes/{classId}/unenroll", "DELETE", ROLE_STUDENT));
        apiEntities.add(new ApiEntity("getClassList", "/api/management/classes/student", "GET", ROLE_STUDENT));
        
        apiEntities.add(new ApiEntity("getGroupDetail", "/api/management/classes/{classId}/groups/{groupId}", "GET", ROLE_STUDENT));
        apiEntities.add(new ApiEntity("joinGroup", "/api/management/classes/{classId}/groups/{groupId}/join", "POST", ROLE_STUDENT));
        apiEntities.add(new ApiEntity("leaveGroup", "/api/management/classes/{classId}/groups/{groupId}/leave", "DELETE", ROLE_STUDENT));
        apiEntities.add(new ApiEntity("removeFromGroupByLeader", "/api/management/classes/{classId}/groups/{groupId}/remove/{removeStudentId}", "DELETE", ROLE_STUDENT));
        apiEntities.add(new ApiEntity("changeGroupLeader", "/api/management/classes/{classId}/groups/{groupId}/changeLeader/{newLeaderId}", "PUT", ROLE_STUDENT));

        apiEntities.add(new ApiEntity("getProjects", "/api/management/classes/{classId}/groups/{groupId}/projects", "GET", ROLE_STUDENT));
        apiEntities.add(new ApiEntity("chooseProject", "/api/management/classes/{classId}/groups/{groupId}/projects/{projectId}", "PUT", ROLE_STUDENT));

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
