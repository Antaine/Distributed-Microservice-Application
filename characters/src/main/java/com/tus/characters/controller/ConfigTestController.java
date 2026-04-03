package com.tus.characters.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigTestController {

    @Value("${test.message:DefaultMessage}")
    private String message;

    @GetMapping("/test-config")
    public String testConfig() {
        return message;
    }
}