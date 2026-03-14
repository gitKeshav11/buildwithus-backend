package com.buildwithus.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/chat")
    public Map<String,String> chat(@RequestBody Map<String,String> request){

        String userMessage = request.get("message");

        Map<String,Object> body = new HashMap<>();
        body.put("model", model);

        List<Map<String,String>> messages = List.of(
                Map.of("role","user","content",userMessage)
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
        Map message = (Map) choice.get("message");

        String reply = (String) message.get("content");

        return Map.of("reply", reply);


    }
    @PostMapping("/project-idea")
    public Map<String,String> suggestProject(@RequestBody Map<String,String> request){

        String skills = request.get("skills");

        Map<String,String> prompt = new HashMap<>();
        prompt.put("message","Suggest a project idea for a developer with skills: "+skills);

        return chat(prompt);
    }
}

