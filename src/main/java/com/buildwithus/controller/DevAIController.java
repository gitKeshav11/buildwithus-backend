package com.buildwithus.controller;

import com.buildwithus.dto.CodeRequest;
import com.buildwithus.dto.DebugRequest;
import com.buildwithus.dto.IdeaRequest;
import com.buildwithus.service.DevAIService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dev-ai")
public class DevAIController {

    private final DevAIService devAIService;

    public DevAIController(DevAIService devAIService){
        this.devAIService = devAIService;
    }

    @PostMapping("/generate-code")
    public String generateCode(@RequestBody CodeRequest request){

        return devAIService.generateCode(request.getPrompt());

    }

    @PostMapping("/debug-code")
    public String debugCode(@RequestBody DebugRequest request){

        return devAIService.debugCode(request.getCode());

    }

    @PostMapping("/startup-idea")
    public String idea(@RequestBody IdeaRequest request){

        return devAIService.generateIdea(request.getTopic());

    }

}