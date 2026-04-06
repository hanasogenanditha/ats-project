package com.ats.screening.kafka;

import com.ats.shared.dto.ScreeningResultEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ScreeningResultProducer {

    private static final Logger logger = LoggerFactory.getLogger(ScreeningResultProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ScreeningResultProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ScreeningResultEvent event) {
        kafkaTemplate.send("screening.completed", event);
        logger.info("Sent result for: {} with score: {}", event.getName(), event.getScore());
    }
}
//review