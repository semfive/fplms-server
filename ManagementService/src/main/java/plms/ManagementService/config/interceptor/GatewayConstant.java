package plms.ManagementService.config.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


public class GatewayConstant {

    protected static final List<ApiEntity> apiEntities = new ArrayList<>();
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_LECTURER = "LECTURER";
     static final String ROLE_SPLIT_STRING = "&";
    public static final String EMAIL_TEST = "t@gmail.com";
    public static final String ROLE_TEST = ROLE_STUDENT;

    private GatewayConstant() {
    }

    public static void addApiEntities() {
        apiEntities.add(new ApiEntity("createClass","/api/management/classes","POST",ROLE_LECTURER));
        apiEntities.add(new ApiEntity("viewClass","/api/management/classes/student","GET",ROLE_STUDENT));
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
