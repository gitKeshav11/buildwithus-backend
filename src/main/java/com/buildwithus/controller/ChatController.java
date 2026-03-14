package com.buildwithus.controller;

import com.buildwithus.dto.ChatRequest;
import com.buildwithus.dto.ChatResponse;
import com.buildwithus.entity.ChatMessage;
import com.buildwithus.service.ChatService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService){
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ChatResponse send(@RequestBody ChatRequest request){

        String reply = chatService.processMessage(
                request.getSessionId(),
                request.getMessage()
        );

        return new ChatResponse(reply);

    }

    @GetMapping("/history/{sessionId}")
    public List<ChatMessage> history(@PathVariable Long sessionId){

        return chatService.getHistory(sessionId);

    }
}