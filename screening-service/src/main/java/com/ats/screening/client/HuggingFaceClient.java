package com.ats.screening.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component
public class HuggingFaceClient {

    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceClient.class);

    @Value("${huggingface.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_URL =
        "https://router.huggingface.co/hf-inference/models/sentence-transformers/all-MiniLM-L6-v2";

    public HuggingFaceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double scoreResume(String resumeText, String jobDescription) {

        logger.info("Calling HuggingFace API...");
        logger.info("API Key starts with: {}", apiKey != null ? apiKey.substring(0, 5) : "NULL");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON)); 

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", Map.of(
            "source_sentence", jobDescription,
            "sentences", List.of(resumeText)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                API_URL, HttpMethod.POST, request, String.class
            );

            logger.info("HuggingFace raw response: {}", response.getBody());

            JsonNode json = objectMapper.readTree(response.getBody());
            double similarity = json.get(0).asDouble();

            logger.info("Parsed similarity score: {}", similarity);

            return similarity * 100;

        } catch (Exception e) {
            logger.error("HuggingFace API call failed: {}", e.getMessage());
            return 50.0;
        }
    }
}