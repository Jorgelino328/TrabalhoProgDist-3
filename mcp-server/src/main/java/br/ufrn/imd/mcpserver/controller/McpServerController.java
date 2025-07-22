package br.ufrn.imd.mcpserver.controller;

import br.ufrn.imd.mcpserver.dto.McpRequest;
import br.ufrn.imd.mcpserver.dto.McpResponse;
import br.ufrn.imd.mcpserver.service.McpServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class McpServerController {

    @Autowired
    private McpServerService mcpServerService;

    @PostMapping("/")
    public ResponseEntity<McpResponse> processRequest(@RequestBody McpRequest request) {
        McpResponse response = mcpServerService.processRequest(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<McpResponse> getStatus() {
        McpResponse response = mcpServerService.getStatus();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "MCP Server"));
    }
}
