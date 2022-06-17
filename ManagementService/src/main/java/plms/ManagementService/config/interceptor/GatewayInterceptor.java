package plms.ManagementService.config.interceptor;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import plms.ManagementService.config.interceptor.exception.NoAccessRoleException;
import plms.ManagementService.config.interceptor.exception.NoTokenException;
import plms.ManagementService.config.interceptor.exception.NotFoundApiException;
import plms.ManagementService.config.interceptor.exception.WrongTokenException;
import plms.ManagementService.model.dto.EmailVerifyDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class GatewayInterceptor implements HandlerInterceptor {
    private static Logger logger = LogManager.getLogger(GatewayInterceptor.class);
    @Value("${application.admin.email}")
    private String adminEmail;

    @Bean
    public WebClient.Builder getWebClientBuilder() {
        return WebClient.builder();
    }

    private static final String VERIFY_URL = "http://fplms-authservice-clusterip:7209/api/auth/accounts/verify";


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            EmailVerifyDTO emailVerifyDTO = verifyRequest(request);
            request.setAttribute("userEmail", emailVerifyDTO.getEmail());//get user email if request check success
            request.setAttribute("userRole", emailVerifyDTO.getRole());
            return true;
        } catch (NoAccessRoleException e) {
            response.sendError(403, "User do not have role to access this api");
            logger.error("403 User do not have role to access this api");
            return false;
        } catch (NoTokenException e) {
            response.sendError(401, "User must have token to access");
            logger.error("401 User must have token to access");
            return false;
        } catch (NotFoundApiException e) {
            response.sendError(404, "Not found api request");
            logger.error("404 Not found api");
            return false;
        } catch (WrongTokenException e) {
            response.sendError(401, "Token is not valid");
            logger.error("401 Token is not valid");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private EmailVerifyDTO verifyRequest(HttpServletRequest request) throws RuntimeException {
        String httpMethod = request.getMethod();
        String servletPath = request.getServletPath();
        String accessToken = request.getHeader(GatewayConstant.AUTHORIZATION_HEADER);
        EmailVerifyDTO emailVerifyDTO;
        if (accessToken == null) {
            logger.info("No access token");
            logger.warn("Use test user");
            throw new NoTokenException();
//            emailVerifyDTO = new EmailVerifyDTO(GatewayConstant.EMAIL_TEST,GatewayConstant.ROLE_TEST);
//            logger.info("Path:{} Role:{} Email:{}", servletPath, emailVerifyDTO.getEmail(), emailVerifyDTO.getRole());
//            ApiEntity apiEntity = getMatchingAPI(httpMethod, servletPath);
//            if (apiEntity == null) throw new NotFoundApiException();
//            verifyRole(apiEntity.getRole(), emailVerifyDTO.getRole());
//            logger.info("Request validated. Start forward request to controller");
//            return emailVerifyDTO;
        } else {
            emailVerifyDTO = getEmailVerifiedEntity(accessToken);
            if (emailVerifyDTO.getEmail() == null)
                throw new WrongTokenException();
        }
        if (adminEmail.equals(emailVerifyDTO.getEmail())) {
        	emailVerifyDTO.setRole("ADMIN");
        }  
        emailVerifyDTO.setRole(emailVerifyDTO.getRole().trim().toUpperCase());
        logger.info("Path:{} VerifyDTO:{}", servletPath, emailVerifyDTO);
        ApiEntity apiEntity = getMatchingAPI(httpMethod, servletPath);
        if (apiEntity == null) throw new NotFoundApiException();
        verifyRole(apiEntity.getRole(), emailVerifyDTO.getRole());
        logger.info("Request validated. Start forward request to controller");
        return emailVerifyDTO;
    }


    private ApiEntity getMatchingAPI(String httpMethod, String path) {
        AntPathMatcher matcher = new AntPathMatcher();
        for (ApiEntity apiEntity : GatewayConstant.apiEntities) {
            if (matcher.match(apiEntity.getPattern(), path) && httpMethod.equals(apiEntity.getHttpMethod())) {
                logger.info("Found api matched");
                return apiEntity;
            }
        }
        logger.warn("Not found api");
        return null;
    }

    private void verifyRole(String pathRole, String userRole) {
        String[] roles = pathRole.split(GatewayConstant.ROLE_SPLIT_STRING);
        for (String role : roles)
            if (role.equals(userRole)) {
                return;
            }
        throw new NoAccessRoleException();
    }

    public EmailVerifyDTO getEmailVerifiedEntity(String token) {
        return getWebClientBuilder().build().get().uri(VERIFY_URL).header(GatewayConstant.AUTHORIZATION_HEADER, token)
                .retrieve().bodyToMono(EmailVerifyDTO.class).block();
    }
}
