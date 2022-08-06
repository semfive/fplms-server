package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import plms.ManagementService.config.interceptor.GatewayConstant;
import plms.ManagementService.model.request.CreateUserRequest;
import plms.ManagementService.model.response.Response;
import plms.ManagementService.service.AuthenticationService;

@RestController
@RequestMapping(value = "/api/auth/management")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping
    public void createUser(@RequestBody CreateUserRequest createUserRequest){
        authenticationService.createUser(createUserRequest);
    }

}

