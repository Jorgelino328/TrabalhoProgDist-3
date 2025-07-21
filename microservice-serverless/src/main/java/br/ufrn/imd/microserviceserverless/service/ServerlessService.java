package br.ufrn.imd.microserviceserverless.service;

import br.ufrn.imd.microserviceserverless.dto.DataTransformRequest;
import br.ufrn.imd.microserviceserverless.dto.ServerlessResponse;
import br.ufrn.imd.microserviceserverless.functions.DataTransformFunction;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ServerlessService {
    
    private static final Logger logger = LoggerFactory.getLogger(ServerlessService.class);
    
    @Autowired
    private DataTransformFunction dataTransformFunction;
    
    @CircuitBreaker(name = "serverlessService", fallbackMethod = "fallbackExecuteFunction")
    @Retry(name = "serverlessService")
    public ServerlessResponse executeFunction(String functionName, DataTransformRequest request) {
        logger.info("Executing serverless function: {}", functionName);
        
        long startTime = System.currentTimeMillis();
        
        try {
            ServerlessResponse response;
            
            switch (functionName.toLowerCase()) {
                case "datatransform":
                case "data-transform":
                    response = dataTransformFunction.apply(request);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown function: " + functionName);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Serverless function {} executed in {} ms", functionName, executionTime);
            
            return response;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Error executing serverless function {}: {}", functionName, e.getMessage());
            throw new RuntimeException("Failed to execute serverless function", e);
        }
    }
    
    public ServerlessResponse fallbackExecuteFunction(String functionName, DataTransformRequest request, Exception ex) {
        logger.warn("Serverless service fallback triggered for function: {}", functionName);
        return new ServerlessResponse(
            Map.of("message", "Serverless function temporarily unavailable", "function", functionName),
            0,
            "FALLBACK",
            functionName
        );
    }
    
    public ServerlessResponse validateData(Object data) {
        long startTime = System.currentTimeMillis();
        logger.info("Validating data");
        
        try {
            Map<String, Object> validationResult = Map.of(
                "is_valid", data != null,
                "type", data != null ? data.getClass().getSimpleName() : "null",
                "size", data instanceof java.util.Collection ? ((java.util.Collection<?>) data).size() : 1
            );
            
            long executionTime = System.currentTimeMillis() - startTime;
            return new ServerlessResponse(validationResult, executionTime, "SUCCESS", "validateData");
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Error validating data: {}", e.getMessage());
            return new ServerlessResponse(
                Map.of("error", e.getMessage()),
                executionTime,
                "ERROR",
                "validateData"
            );
        }
    }
}
