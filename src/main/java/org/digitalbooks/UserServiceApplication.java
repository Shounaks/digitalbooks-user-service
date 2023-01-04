package org.digitalbooks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class UserServiceApplication {
    public static void main(String[] args) {
        System.out.println("Use this URL for accessing swaggerUI: http://localhost:8080/swagger-ui/index.html#/");
        SpringApplication.run(UserServiceApplication.class, args);
    }
}