package com.vietcrm.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.vietcrm")
public class VietCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(VietCrmApplication.class, args);
    }
}
