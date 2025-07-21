package br.ufrn.imd.microservicemcp.service;

import br.ufrn.imd.microservicemcp.dto.McpRequest;
import br.ufrn.imd.microservicemcp.dto.McpResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class McpService {
    
    private static final Logger logger = LoggerFactory.getLogger(McpService.class);
    private final WebClient webClient;
    
    @Value("${mcp.server.url:http://localhost:3000}")
    private String mcpServerUrl;
    
    public McpService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    @CircuitBreaker(name = "mcpService", fallbackMethod = "fallbackExecuteCommand")
    @Retry(name = "mcpService")
    public McpResponse executeCommand(McpRequest request) {
        logger.info("Executing MCP command: {}", request.getCommand());
        
        try {
            String response = webClient.post()
                    .uri(mcpServerUrl + "/execute")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))
                    .block();
            
            logger.info("MCP command executed successfully");
            return new McpResponse(response, "SUCCESS");
        } catch (Exception e) {
            logger.error("Error executing MCP command: {}", e.getMessage());
            throw new RuntimeException("Failed to execute MCP command", e);
        }
    }
    
    public McpResponse fallbackExecuteCommand(McpRequest request, Exception ex) {
        logger.warn("MCP service fallback triggered for command: {}", request.getCommand());
        return new McpResponse(
            Map.of("message", "MCP service temporarily unavailable", "command", request.getCommand()),
            "FALLBACK"
        );
    }
    
    public McpResponse getStatus() {
        try {
            String status = webClient.get()
                    .uri(mcpServerUrl + "/status")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            return new McpResponse(status, "SUCCESS");
        } catch (Exception e) {
            logger.error("Error getting MCP status: {}", e.getMessage());
            return new McpResponse("MCP server unavailable", "ERROR");
        }
    }
}
