package com.virhon.fintech.glexp.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.virhon.fintech.glexp.model"})
public class Application {

    @Autowired
    private Config config;

    public static void main(String[] args) {
        SpringApplication.run(com.virhon.fintech.glexp.model.Application.class, args);
    }

}
