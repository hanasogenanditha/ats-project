package com.ats.screening.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;

import java.util.Map;
import java.util.HashMap;

@Service
public class HuggingFaceService {

    private final String API_URL = "https://api-inference.huggingface.co/models/sentence-transformers/all-MiniLM-L6-v2";

    private final String API_KEY = "hf_YOUR_TOKEN_HERE";

    public String getEmbedding(String input) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("inputs", input);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(API_URL, request, String.class);
    }
}
//review