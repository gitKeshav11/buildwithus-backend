package com.buildwithus.ai.controller;

import com.buildwithus.ai.dto.ChatConversationDTO;
import com.buildwithus.ai.dto.ChatMessageDTO;
import com.buildwithus.ai.dto.CreateConversationRequest;
import com.buildwithus.ai.dto.SendMessageRequest;
import com.buildwithus.ai.service.ChatService;
import com.buildwithus.common.dto.ApiResponse;
import com.buildwithus.common.dto.PagedResponse;
import com.buildwithus.security.CurrentUser;
import com.buildwithus.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai/chat")
@RequiredArgsConstructor
@Tag(name = "AI Chat", description = "AI developer assistant chat APIs")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {
    
    private final ChatService chatService;
    
    @PostMapping("/conversations")
    @Operation(summary = "Create a new chat conversation")
    public ResponseEntity<ApiResponse<ChatConversationDTO>> createConversation(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CreateConversationRequest request) {
        ChatConversationDTO result = chatService.createConversation(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Conversation created", result));
    }
    
    @PostMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Send a message in a conversation")
    public ResponseEntity<ApiResponse<ChatMessageDTO>> sendMessage(
            @PathVariable Long conversationId,
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody SendMessageRequest request) {
        ChatMessageDTO result = chatService.sendMessage(conversationId, currentUser.getId(), request.getMessage());
        return ResponseEntity.ok(ApiResponse.success("Message sent", result));
    }
    
    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "Get conversation by ID")
    public ResponseEntity<ApiResponse<ChatConversationDTO>> getConversationById(
            @PathVariable Long conversationId,
            @CurrentUser UserPrincipal currentUser) {
        ChatConversationDTO result = chatService.getConversationById(conversationId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    @GetMapping("/conversations")
    @Operation(summary = "Get my conversations")
    public ResponseEntity<ApiResponse<PagedResponse<ChatConversationDTO>>> getMyConversations(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var conversations = chatService.getMyConversations(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(conversations)));
    }
    
    @DeleteMapping("/conversations/{conversationId}")
    @Operation(summary = "Delete a conversation")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @PathVariable Long conversationId,
            @CurrentUser UserPrincipal currentUser) {
        chatService.deleteConversation(conversationId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Conversation deleted"));
    }
}