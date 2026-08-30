package com.ats.application_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ats.application_service.model.Application;
import com.ats.application_service.service.ApplicationService;
import com.ats.application_service.producer.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/apply")
public class ApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping
    public ResponseEntity<?> apply(@RequestBody Application application) {
        try {
            // Save application to database
            Application savedApp = applicationService.saveApplication(application);
            logger.info("Application saved with ID: {}", savedApp.getId());

            // Send to Kafka for screening
            ObjectMapper mapper = new ObjectMapper();
            String appJson = mapper.writeValueAsString(savedApp);
            kafkaProducerService.sendMessage(appJson);
            logger.info("Application sent to Kafka for screening");

            return ResponseEntity.ok("Application submitted successfully. ID: " + savedApp.getId());
        } catch (Exception e) {
            logger.error("Failed to process application", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplication(@PathVariable Long id) {
        Application app = applicationService.getApplication(id);
        if (app != null) {
            return ResponseEntity.ok(app);
        }
        return ResponseEntity.notFound().build();
    }
}
//review