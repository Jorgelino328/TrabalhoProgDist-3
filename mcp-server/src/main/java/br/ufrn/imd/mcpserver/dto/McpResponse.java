package br.ufrn.imd.mcpserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class McpResponse {
    @JsonProperty("result")
    private Object result;
    
    @JsonProperty("error")
    private Object error;
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    public McpResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public McpResponse(Object result, String id) {
        this();
        this.result = result;
        this.id = id;
    }

    public McpResponse(Object error, String id, boolean isError) {
        this();
        if (isError) {
            this.error = error;
        } else {
            this.result = error;
        }
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
