package com.ats.screening.service.impl;

import com.ats.screening.dto.ApplicationEvent;
import com.ats.shared.dto.ScreeningResultEvent;
import com.ats.screening.dto.Job;
import com.ats.screening.kafka.ScreeningResultProducer;
import com.ats.screening.client.HuggingFaceClient;
import com.ats.screening.service.ScreeningService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ScreeningServiceImpl implements ScreeningService {

    private static final Logger logger = LoggerFactory.getLogger(ScreeningServiceImpl.class);

    private final HuggingFaceClient huggingFaceClient;
    private final ScreeningResultProducer producer;

    @Autowired
    private RestTemplate restTemplate;

    public ScreeningServiceImpl(HuggingFaceClient huggingFaceClient,
                                ScreeningResultProducer producer) {
        this.huggingFaceClient = huggingFaceClient;
        this.producer = producer;
    }

    @Override
    public void processApplication(ApplicationEvent event) {

        logger.info("Processing application from: {}", event.getName());

        Job job = restTemplate.getForObject(
                "http://job-service:8084/jobs/" + event.getJobId(),
                Job.class
        );

        String jobDescription = (job != null) ? job.getDescription() : "";

        logger.info("Resume text: {}", event.getResumeText());
        logger.info("Job description: {}", jobDescription);

        double score = huggingFaceClient.scoreResume(event.getResumeText(), jobDescription);

        logger.info("AI Score for {}: {}", event.getName(), score);

        // Bonus if resume mentions the job title
        if (job != null && event.getResumeText().toLowerCase()
                .contains(job.getTitle().toLowerCase())) {
            score += 10;
            logger.info("Bonus applied! New score: {}", score);
        }

        score = Math.min(score, 100);

        ScreeningResultEvent result = new ScreeningResultEvent();
        result.setName(event.getName());
        result.setScore(score);
        result.setCandidateEmail(event.getCandidateEmail());
        result.setApplicationId(event.getJobId());

        producer.send(result);
    }
}