package br.ufrn.imd.microserviceai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiRequest {
    @JsonProperty("prompt")
    private String prompt;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    public AiRequest() {}
    
    public AiRequest(String prompt, String model, Integer maxTokens) {
        this.prompt = prompt;
        this.model = model;
        this.maxTokens = maxTokens;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}
