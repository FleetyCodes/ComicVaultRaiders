package com.comicvaultraiders.comicvaultraiders.integration.ai.service;

import com.anthropic.errors.BadRequestException;
import com.comicvaultraiders.comicvaultraiders.entity.AiRateLimit;
import com.comicvaultraiders.comicvaultraiders.entity.RateLimit;
import com.comicvaultraiders.comicvaultraiders.integration.ai.tools.ComicTools;
import com.comicvaultraiders.comicvaultraiders.integration.ai.tools.UserTools;
import com.comicvaultraiders.comicvaultraiders.service.AiRateLimitService;
import com.comicvaultraiders.comicvaultraiders.service.RateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.stereotype.Service;



@Service
public class AiAssistantService {
    private final ChatClient chatClient;
    private final AiRateLimitService aiRateLimitService;
    private final RateLimitService rateLimitService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public AiAssistantService(ChatClient.Builder builder, ComicTools comicTools, UserTools userTools, AiRateLimitService aiRateLimitService, RateLimitService rateLimitService) {
        this.aiRateLimitService = aiRateLimitService;
        this.rateLimitService = rateLimitService;
        this.chatClient = builder
                .defaultSystem("You're an Assistant of a Comic Catalogy website (Comic Vault Raiders). " +
                        "Only provide answers based on the website's database." +
                        "Only answer comic related questions." +
                        "Always give short Answers, maximum 2-3 short sentences" +
                        "Do NOT use emojis or * symbols" +
                        "If the user asks anything unrelated (jokes, weather, coding etc.)," +
                        "      politely refuse and redirect to comics" +
                        "NEVER make up comic data, ONLY use what the tools return." +
                        "    If a tool returns no result, say so honestly")
                .defaultTools(comicTools, userTools)
                .defaultOptions(ChatOptions.builder()
                        .temperature(0.4)   //0.7 is the default, 0.0 fact based, 1.0 creative but unpredictable
                        .maxTokens(500))     //around ~100 words answers
                .build();
    }

    public String chat(String userMessage, Long userId) {
        String claudeResponse = "We are sorry, our agent is currently not available. " +
                "If this this message occurs for a long period of time, please contact us in e-mail.";;

        AiRateLimit userAiRateLimit = aiRateLimitService.findByUserId(userId);
        RateLimit aiRateLimit = rateLimitService.findByApiName("ANTHROPIC_AI");
        if(userAiRateLimit.getTraffic() >= aiRateLimit.getDailyLimit()){
            return "Daily limit reached. Please try again tomorrow.";
        }
        aiRateLimitService.updateRateLimit(userId, userAiRateLimit.getTraffic()+1);

        userMessage = "UserId: " + userId + "message: " +  userMessage;
        try{
            claudeResponse = chatClient.prompt()
                    .user(userMessage)
                    .call()
                    .content();

        }catch(NonTransientAiException e){
            logger.error(e.getMessage());
        }catch(BadRequestException e){
            logger.error(e.getMessage());
            if(e.getMessage() != null && e.getMessage().contains("credit balance")){
                claudeResponse =  "You don't have enough tokens left. Please check your balance.";
            }
        }
        return claudeResponse;
    }
}