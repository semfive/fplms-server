package plms.ManagementService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plms.ManagementService.model.request.CreateUserRequest;
import plms.ManagementService.service.AuthenticationService;

@RestController
@RequestMapping(value = "/api/management/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;
    @PostMapping
    public void createUser(@RequestBody CreateUserRequest createUserRequest){
        authenticationService.createUser(createUserRequest);
    }
}

