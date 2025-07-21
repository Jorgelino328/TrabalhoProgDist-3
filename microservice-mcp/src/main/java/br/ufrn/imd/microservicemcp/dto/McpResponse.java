package br.ufrn.imd.microservicemcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class McpResponse {
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    public McpResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public McpResponse(Object result, String status) {
        this.result = result;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
