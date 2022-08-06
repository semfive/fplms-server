package plms.ManagementService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plms.ManagementService.config.interceptor.GatewayConstant;
import plms.ManagementService.model.response.Response;

@RestController
@RequestMapping("/api/management/valid")
public class ValidateController {
    @GetMapping
    public Response<Boolean> checkAdminRole(@RequestAttribute(required = false) String userRole){
        if(userRole == null) return new Response<>(501,"User role is null");
        if(userRole.contains(GatewayConstant.ROLE_ADMIN)) return new Response<>(200,"Success");
        return new Response<>(403,"User is not admin");
    }
    @GetMapping(("/role"))
    public Response<String>checkUserRole(@RequestAttribute(required = false) String userRole){
        return new Response<>(200,"Success",userRole);
    }
}
