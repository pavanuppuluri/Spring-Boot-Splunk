package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    @GetMapping("/greet")
    public String greetUser(@RequestParam String name) {
        logger.info("Received greeting request for user: {}", name);
        
        try {
            String message = createGreetingMessage(name);
            logger.debug("Greeting message created: {}", message);  // Log debug message
            return message;
        } catch (Exception e) {
            logger.error("Error while creating greeting message", e);  // Log error message
            return "An error occurred";
        }
    }

    private String createGreetingMessage(String name) throws Exception {
        logger.info("Creating greeting message for: {}", name);
        if (name == null || name.isEmpty()) {
            throw new Exception("Invalid name provided");
        }
        return "Hello, " + name + "!";
    }
}
