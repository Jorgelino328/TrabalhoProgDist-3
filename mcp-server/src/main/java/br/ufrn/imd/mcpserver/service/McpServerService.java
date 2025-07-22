package br.ufrn.imd.mcpserver.service;

import br.ufrn.imd.mcpserver.dto.McpRequest;
import br.ufrn.imd.mcpserver.dto.McpResponse;
import br.ufrn.imd.mcpserver.strategy.McpOperationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class McpServerService {

    private static final Logger logger = LoggerFactory.getLogger(McpServerService.class);

    @Autowired
    private List<McpOperationStrategy> operationStrategies;

    public McpResponse processRequest(McpRequest request) {
        logger.info("Processing MCP request: method={}, id={}", request.getMethod(), request.getId());

        try {
            McpOperationStrategy strategy = findStrategy(request.getMethod());
            if (strategy == null) {
                Map<String, Object> error = Map.of(
                    "code", -32601,
                    "message", "Method not found: " + request.getMethod()
                );
                return new McpResponse(error, request.getId(), true);
            }

            Object result = strategy.execute(request);
            logger.info("MCP request processed successfully: id={}", request.getId());
            return new McpResponse(result, request.getId());

        } catch (Exception e) {
            logger.error("Error processing MCP request: id={}, error={}", request.getId(), e.getMessage());
            Map<String, Object> error = Map.of(
                "code", -32000,
                "message", "Internal error: " + e.getMessage()
            );
            return new McpResponse(error, request.getId(), true);
        }
    }

    public McpResponse getStatus() {
        Map<String, Object> status = Map.of(
            "server", "MCP Server",
            "version", "1.0.0",
            "status", "running",
            "supported_methods", getSupportedMethods()
        );
        return new McpResponse(status, "status");
    }

    private McpOperationStrategy findStrategy(String method) {
        return operationStrategies.stream()
                .filter(strategy -> strategy.supports(method))
                .findFirst()
                .orElse(null);
    }

    private List<String> getSupportedMethods() {
        return operationStrategies.stream()
                .map(strategy -> {
                    if (strategy instanceof br.ufrn.imd.mcpserver.strategy.impl.FileSystemOperationStrategy) {
                        return "filesystem";
                    } else if (strategy instanceof br.ufrn.imd.mcpserver.strategy.impl.SystemOperationStrategy) {
                        return "system";
                    }
                    return "unknown";
                })
                .toList();
    }
}
