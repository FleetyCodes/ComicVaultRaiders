package com.comicvaultraiders.comicvaultraiders.integration.ai.controller;

import com.comicvaultraiders.comicvaultraiders.integration.ai.model.ChatRequest;
import com.comicvaultraiders.comicvaultraiders.integration.ai.model.ChatResponse;
import com.comicvaultraiders.comicvaultraiders.integration.ai.service.AiAssistantService;
import com.comicvaultraiders.comicvaultraiders.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/ai-agent")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;
    private final JwtUtil jwtUtils;

    public AiAssistantController(AiAssistantService aiAssistantService, JwtUtil jwtUtils) {
        this.aiAssistantService = aiAssistantService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> getAiAssistantAgentAnswer(@RequestBody ChatRequest request,
                                                       @RequestHeader("Authorization") String authHeader) {
        String token = jwtUtils.getJwtFromHeader(authHeader);
        Long userId = jwtUtils.getUserIdFromToken(token);

        return ResponseEntity.ok(new ChatResponse(aiAssistantService.chat(request.getMessage(), userId)));
    }
}
