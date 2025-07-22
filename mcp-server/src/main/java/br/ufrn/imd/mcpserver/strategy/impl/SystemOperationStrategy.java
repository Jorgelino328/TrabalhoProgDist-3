package br.ufrn.imd.mcpserver.strategy.impl;

import br.ufrn.imd.mcpserver.dto.McpRequest;
import br.ufrn.imd.mcpserver.strategy.McpOperationStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SystemOperationStrategy implements McpOperationStrategy {

    @Override
    public Object execute(McpRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParams();
        String operation = (String) params.get("operation");

        try {
            switch (operation.toLowerCase()) {
                case "status":
                    return getSystemStatus();
                case "info":
                    return getSystemInfo();
                case "ping":
                    return ping();
                default:
                    throw new IllegalArgumentException("Unknown system operation: " + operation);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", -1);
            error.put("message", "System operation failed: " + e.getMessage());
            return error;
        }
    }

    @Override
    public boolean supports(String method) {
        return "system".equals(method);
    }

    private Object getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("uptime", System.currentTimeMillis());
        status.put("memory_usage", getMemoryUsage());
        status.put("available_processors", Runtime.getRuntime().availableProcessors());
        return status;
    }

    private Object getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("java_version", System.getProperty("java.version"));
        info.put("os_name", System.getProperty("os.name"));
        info.put("os_version", System.getProperty("os.version"));
        info.put("user_name", System.getProperty("user.name"));
        info.put("user_dir", System.getProperty("user.dir"));
        return info;
    }

    private Object ping() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "pong");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    private Map<String, Object> getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("total", runtime.totalMemory());
        memory.put("free", runtime.freeMemory());
        memory.put("used", runtime.totalMemory() - runtime.freeMemory());
        memory.put("max", runtime.maxMemory());
        return memory;
    }
}
