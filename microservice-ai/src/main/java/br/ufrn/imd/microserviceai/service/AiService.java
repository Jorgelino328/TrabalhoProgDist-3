package br.ufrn.imd.microserviceai.service;

import br.ufrn.imd.microserviceai.dto.AiRequest;
import br.ufrn.imd.microserviceai.dto.AiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    
    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    
    @Autowired
    private ChatClient chatClient;
    
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackProcessPrompt")
    @Retry(name = "aiService")
    public AiResponse processPrompt(AiRequest request) {
        logger.info("Processing AI prompt with model: {}", request.getModel());
        
        try {
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .withModel(request.getModel() != null ? request.getModel() : "gpt-3.5-turbo")
                    .withMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 150)
                    .build();
            
            Prompt prompt = new Prompt(new UserMessage(request.getPrompt()), options);
            ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
            
            String aiResponse = response.getResult().getOutput().getContent();
            Long tokensUsedLong = response.getMetadata().getUsage().getTotalTokens();
            Integer tokensUsed = tokensUsedLong != null ? tokensUsedLong.intValue() : 0;
            
            logger.info("AI prompt processed successfully. Tokens used: {}", tokensUsed);
            return new AiResponse(aiResponse, options.getModel(), tokensUsed, "SUCCESS");
            
        } catch (Exception e) {
            logger.error("Error processing AI prompt: {}", e.getMessage());
            throw new RuntimeException("Failed to process AI prompt", e);
        }
    }
    
    public AiResponse fallbackProcessPrompt(AiRequest request, Exception ex) {
        logger.warn("AI service fallback triggered for prompt: {}", request.getPrompt().substring(0, Math.min(50, request.getPrompt().length())));
        return new AiResponse(
            "AI service is temporarily unavailable. Please try again later.",
            "fallback",
            0,
            "FALLBACK"
        );
    }
    
    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackGenerateText")
    public AiResponse generateText(String topic) {
        logger.info("Generating text for topic: {}", topic);
        
        try {
            String prompt = "Generate a comprehensive text about: " + topic;
            AiRequest request = new AiRequest(prompt, "gpt-3.5-turbo", 300);
            return processPrompt(request);
        } catch (Exception e) {
            logger.error("Error generating text: {}", e.getMessage());
            throw new RuntimeException("Failed to generate text", e);
        }
    }
    
    public AiResponse fallbackGenerateText(String topic, Exception ex) {
        logger.warn("AI text generation fallback triggered for topic: {}", topic);
        return new AiResponse(
            "Text generation service is temporarily unavailable for topic: " + topic,
            "fallback",
            0,
            "FALLBACK"
        );
    }
}
