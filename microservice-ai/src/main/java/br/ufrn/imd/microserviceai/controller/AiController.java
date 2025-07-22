package br.ufrn.imd.microserviceai.controller;

import br.ufrn.imd.microserviceai.dto.AiRequest;
import br.ufrn.imd.microserviceai.dto.AiResponse;
import br.ufrn.imd.microserviceai.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*")
public class AiController {
    
    private final AiService aiService;
    
    @Autowired
    public AiController(AiService aiService) {
        this.aiService = aiService;
    }
    
    @PostMapping("/prompt")
    public ResponseEntity<AiResponse> processPrompt(@RequestBody AiRequest request) {
        try {
            AiResponse response = aiService.processPrompt(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AiResponse("Error processing prompt: " + e.getMessage(), "error", 0, "ERROR"));
        }
    }
    
    @GetMapping("/generate/{topic}")
    public ResponseEntity<AiResponse> generateText(@PathVariable String topic) {
        try {
            AiResponse response = aiService.generateText(topic);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AiResponse("Error generating text: " + e.getMessage(), "error", 0, "ERROR"));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<AiResponse> health() {
        return ResponseEntity.ok(new AiResponse("AI Service is healthy", "health-check", 0, "SUCCESS"));
    }
    
    @GetMapping("/status")
    public ResponseEntity<AiResponse> status() {
        return ResponseEntity.ok(new AiResponse("AI Service is running", "gpt-3.5-turbo", 0, "SUCCESS"));
    }
    
    @GetMapping("/debug")
    public ResponseEntity<Map<String, String>> debugConfig() {
        Map<String, String> debug = new HashMap<>();
        
        // Check if API key is loaded (without exposing the actual key)
        String apiKey = System.getenv("OPENAI_API_KEY");
        debug.put("api_key_configured", apiKey != null && !apiKey.isEmpty() ? "true" : "false");
        debug.put("api_key_length", apiKey != null ? String.valueOf(apiKey.length()) : "0");
        debug.put("api_key_starts_with", apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : "null");
        debug.put("api_key_ends_with", apiKey != null && apiKey.length() > 10 ? "..." + apiKey.substring(apiKey.length() - 4) : "null");
        
        // Validate API key format
        if (apiKey != null) {
            debug.put("api_key_format_valid", apiKey.startsWith("sk-") ? "true" : "false");
            debug.put("api_key_has_proper_length", (apiKey.length() >= 51) ? "true" : "false");
        }
        
        // Check Spring profile
        String[] activeProfiles = System.getProperty("spring.profiles.active", "").split(",");
        debug.put("active_profiles", String.join(",", activeProfiles));
        
        return ResponseEntity.ok(debug);
    }
    
    @PostMapping("/test-openai")
    public ResponseEntity<Map<String, Object>> testOpenAiConnection(@RequestBody(required = false) Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Simple test with a very short prompt to minimize costs
            String model = (body != null && body.get("model") != null) ? body.get("model") : "gpt-4o-mini";
            AiRequest testRequest = new AiRequest("Hi", model, 5);
            AiResponse response = aiService.processPrompt(testRequest);
            
            result.put("success", true);
            result.put("response", response.getResponse());
            result.put("tokens_used", response.getTokensUsed());
            result.put("status", response.getStatus());
            result.put("model_tested", model);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("error_type", e.getClass().getSimpleName());
            
            // Get more detailed error information
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            result.put("root_cause", rootCause.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/test-models")
    public ResponseEntity<Map<String, Object>> testMultipleModels() {
        Map<String, Object> result = new HashMap<>();
        String[] modelsToTest = {"gpt-4o-mini", "gpt-3.5-turbo", "gpt-4o", "o1-mini"};
        
        for (String model : modelsToTest) {
            Map<String, Object> modelResult = new HashMap<>();
            try {
                AiRequest testRequest = new AiRequest("Hi", model, 5);
                AiResponse response = aiService.processPrompt(testRequest);
                
                modelResult.put("success", true);
                modelResult.put("response", response.getResponse());
                modelResult.put("status", response.getStatus());
                modelResult.put("tokens_used", response.getTokensUsed());
            } catch (Exception e) {
                modelResult.put("success", false);
                modelResult.put("error", e.getMessage());
                
                // Check if it's a specific model error
                if (e.getMessage().contains("model")) {
                    modelResult.put("error_type", "MODEL_ERROR");
                } else if (e.getMessage().contains("quota")) {
                    modelResult.put("error_type", "QUOTA_ERROR");
                } else if (e.getMessage().contains("rate")) {
                    modelResult.put("error_type", "RATE_LIMIT_ERROR");
                } else {
                    modelResult.put("error_type", "OTHER_ERROR");
                }
            }
            result.put(model, modelResult);
        }
        
        return ResponseEntity.ok(result);
    }
}
