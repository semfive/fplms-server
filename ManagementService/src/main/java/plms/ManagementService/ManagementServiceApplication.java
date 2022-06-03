package plms.ManagementService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import plms.ManagementService.config.interceptor.GatewayConstant;

import plms.ManagementService.config.interceptor.GatewayConstant;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ManagementServiceApplication {

	public static void main(String[] args) {
		GatewayConstant.addApiEntities();
		SpringApplication.run(ManagementServiceApplication.class, args);
	}

}
