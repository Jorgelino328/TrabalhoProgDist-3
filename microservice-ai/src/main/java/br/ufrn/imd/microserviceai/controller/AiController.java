package br.ufrn.imd.microserviceai.controller;

import br.ufrn.imd.microserviceai.dto.AiRequest;
import br.ufrn.imd.microserviceai.dto.AiResponse;
import br.ufrn.imd.microserviceai.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
