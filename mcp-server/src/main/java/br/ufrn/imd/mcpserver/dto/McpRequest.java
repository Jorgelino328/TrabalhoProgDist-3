package br.ufrn.imd.mcpserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class McpRequest {
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("params")
    private Object params;
    
    @JsonProperty("id")
    private String id;

    public McpRequest() {}

    public McpRequest(String method, Object params, String id) {
        this.method = method;
        this.params = params;
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
