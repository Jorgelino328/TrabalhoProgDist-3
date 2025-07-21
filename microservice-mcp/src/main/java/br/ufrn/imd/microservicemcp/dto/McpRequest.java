package br.ufrn.imd.microservicemcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class McpRequest {
    @JsonProperty("command")
    private String command;
    
    @JsonProperty("parameters")
    private Object parameters;
    
    public McpRequest() {}
    
    public McpRequest(String command, Object parameters) {
        this.command = command;
        this.parameters = parameters;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public Object getParameters() {
        return parameters;
    }
    
    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }
}
