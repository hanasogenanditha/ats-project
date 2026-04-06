package com.ats.screening.kafka;

import com.ats.screening.dto.ApplicationEvent;
import com.ats.screening.service.ScreeningService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScreeningConsumer {

    private final ScreeningService screeningService;

    public ScreeningConsumer(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @KafkaListener(topics = "application.submitted", groupId = "screening-group")
    public void consume(ApplicationEvent event) {
        System.out.println("Received: " + event.getName());
        screeningService.processApplication(event);  
    }
}
//review