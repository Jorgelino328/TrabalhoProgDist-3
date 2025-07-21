package br.ufrn.imd.microserviceai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiResponse {
    @JsonProperty("response")
    private String response;
    
    @JsonProperty("model_used")
    private String modelUsed;
    
    @JsonProperty("tokens_used")
    private Integer tokensUsed;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("timestamp")
    private long timestamp;
    
    public AiResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public AiResponse(String response, String modelUsed, Integer tokensUsed, String status) {
        this.response = response;
        this.modelUsed = modelUsed;
        this.tokensUsed = tokensUsed;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getModelUsed() {
        return modelUsed;
    }
    
    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }
    
    public Integer getTokensUsed() {
        return tokensUsed;
    }
    
    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
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
