package com.buildwithus.service;

import org.springframework.stereotype.Service;

@Service
public class DevAIService {

    private final AIService aiService;

    public DevAIService(AIService aiService){
        this.aiService = aiService;
    }

    public String generateCode(String prompt){

        String message =
                "Generate clean production level code for: " + prompt;

        return aiService.askAI(message);
    }

    public String debugCode(String code){

        String message =
                "Find bugs and fix this code:\n" + code;

        return aiService.askAI(message);
    }

    public String generateIdea(String topic){

        String message =
                "Generate startup ideas about: " + topic;

        return aiService.askAI(message);
    }
}