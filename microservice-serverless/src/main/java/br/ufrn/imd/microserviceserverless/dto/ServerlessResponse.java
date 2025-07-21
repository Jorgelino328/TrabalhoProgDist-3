package br.ufrn.imd.microserviceserverless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerlessResponse {
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("execution_time_ms")
    private long executionTimeMs;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("function_name")
    private String functionName;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    public ServerlessResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ServerlessResponse(Object result, long executionTimeMs, String status, String functionName) {
        this.result = result;
        this.executionTimeMs = executionTimeMs;
        this.status = status;
        this.functionName = functionName;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
