package br.ufrn.imd.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/mcp")
    public ResponseEntity<Map<String, String>> mcpFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "MCP Service is currently unavailable", "status", "503"));
    }

    @GetMapping("/ai")
    public ResponseEntity<Map<String, String>> aiFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "AI Service is currently unavailable", "status", "503"));
    }

    @GetMapping("/serverless")
    public ResponseEntity<Map<String, String>> serverlessFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Serverless Service is currently unavailable", "status", "503"));
    }
}
