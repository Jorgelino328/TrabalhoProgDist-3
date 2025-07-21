package br.ufrn.imd.microservicemcp.controller;

import br.ufrn.imd.microservicemcp.dto.McpRequest;
import br.ufrn.imd.microservicemcp.dto.McpResponse;
import br.ufrn.imd.microservicemcp.service.McpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mcp")
public class McpController {
    
    @Autowired
    private McpService mcpService;
    
    @PostMapping("/execute")
    public ResponseEntity<McpResponse> executeCommand(@RequestBody McpRequest request) {
        McpResponse response = mcpService.executeCommand(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status")
    public ResponseEntity<McpResponse> getStatus() {
        McpResponse response = mcpService.getStatus();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<McpResponse> health() {
        return ResponseEntity.ok(new McpResponse("Service is healthy", "SUCCESS"));
    }
}
