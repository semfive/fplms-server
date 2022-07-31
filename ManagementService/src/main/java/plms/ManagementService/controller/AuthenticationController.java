package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import plms.ManagementService.model.request.CreateUserRequest;
import plms.ManagementService.service.AuthenticationService;

@RestController
@RequestMapping(value = "/api/auth/management")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;
    @Value("${application.admin.email}")
    private String adminEmail;
    @PostMapping
    public void createUser(@RequestBody CreateUserRequest createUserRequest){
        authenticationService.createUser(createUserRequest);
    }
    @GetMapping
    public boolean checkAdminRole(@RequestAttribute(required = false) String userEmail){
        if(userEmail.equals(adminEmail)) return true;
        else return false;
    }
}

