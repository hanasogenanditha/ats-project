package com.ats.notification_service.consumer;

import com.ats.shared.dto.ScreeningResultEvent;
import com.ats.notification_service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "screening.completed", groupId = "notification-group")
    public void consume(ScreeningResultEvent event) {

        logger.info("STEP 1: Event received → Name: {}, Score: {}, Email: {}", 
                event.getName(), event.getScore(), event.getCandidateEmail());

        if (event.getCandidateEmail() == null || event.getCandidateEmail().isEmpty()) {
            logger.error("Email is NULL or EMPTY. Cannot send email.");
            return;
        }

        try {
            logger.info("STEP 2: Calling email service...");

            emailService.sendScreeningResultEmail(
                    event.getCandidateEmail(),
                    event.getName(),
                    event.getScore()
            );

            logger.info("STEP 3: Email sent successfully to {}", event.getCandidateEmail());

        } catch (Exception e) {
            logger.error("ERROR while sending email: {}", e.getMessage(), e);
        }
    }
}