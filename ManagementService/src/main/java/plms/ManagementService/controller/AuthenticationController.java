package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import plms.ManagementService.config.interceptor.GatewayConstant;
import plms.ManagementService.model.request.CreateUserRequest;
import plms.ManagementService.service.AuthenticationService;

@RestController
@RequestMapping(value = "/api")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/auth/management")
    public void createUser(@RequestBody CreateUserRequest createUserRequest){
        authenticationService.createUser(createUserRequest);
    }
    @GetMapping("/management/auth")
    public boolean checkAdminRole(@RequestAttribute(required = false) String userRole){
        if(userRole == null) return false;
        if(userRole.contains(GatewayConstant.ROLE_ADMIN)) return true;
        else return false;
    }
}

