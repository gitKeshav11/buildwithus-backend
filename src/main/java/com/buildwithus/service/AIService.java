package com.buildwithus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class AIService {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askAI(String message){

        Map<String,Object> body = new HashMap<>();

        body.put("model", model);

        List<Map<String,String>> messages = List.of(
                Map.of("role","user","content",message)
        );

        body.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String,Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(apiUrl, entity, Map.class);

        Map result = response.getBody();

        List choices = (List) result.get("choices");
        Map choice = (Map) choices.get(0);
        Map messageObj = (Map) choice.get("message");

        return (String) messageObj.get("content");

    }
}