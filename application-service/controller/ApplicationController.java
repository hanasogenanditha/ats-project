package com.ats.applicationservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apply")
public class ApplicationController {

    private final KafkaProducer kafkaProducer;

    public ApplicationController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping
    public String apply(@RequestBody ApplicationRequest request) {
        kafkaProducer.send(request);
        return "Application submitted successfully";
    }
}