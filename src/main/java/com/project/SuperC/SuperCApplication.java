package com.project.SuperC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.project.SuperC.models", "com.project.SuperC.token"})
@EnableJpaRepositories(basePackages = {"com.project.SuperC.repository", "com.project.SuperC.token"})
public class SuperCApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperCApplication.class, args);
    }

}
