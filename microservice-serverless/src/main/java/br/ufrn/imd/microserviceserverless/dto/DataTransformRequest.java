package br.ufrn.imd.microserviceserverless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataTransformRequest {
    @JsonProperty("data")
    private Object data;
    
    @JsonProperty("transformation_type")
    private String transformationType;
    
    @JsonProperty("parameters")
    private Object parameters;
    
    public DataTransformRequest() {}
    
    public DataTransformRequest(Object data, String transformationType, Object parameters) {
        this.data = data;
        this.transformationType = transformationType;
        this.parameters = parameters;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getTransformationType() {
        return transformationType;
    }
    
    public void setTransformationType(String transformationType) {
        this.transformationType = transformationType;
    }
    
    public Object getParameters() {
        return parameters;
    }
    
    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }
}
