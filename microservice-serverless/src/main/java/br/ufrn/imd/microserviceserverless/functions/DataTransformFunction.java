package br.ufrn.imd.microserviceserverless.functions;

import br.ufrn.imd.microserviceserverless.dto.DataTransformRequest;
import br.ufrn.imd.microserviceserverless.dto.ServerlessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component("dataTransformFunction")
public class DataTransformFunction implements Function<DataTransformRequest, ServerlessResponse> {
    
    private static final Logger logger = LoggerFactory.getLogger(DataTransformFunction.class);
    
    @Override
    public ServerlessResponse apply(DataTransformRequest request) {
        long startTime = System.currentTimeMillis();
        logger.info("Executing data transformation function: {}", request.getTransformationType());
        
        try {
            Object result = performTransformation(request);
            long executionTime = System.currentTimeMillis() - startTime;
            
            logger.info("Data transformation completed in {} ms", executionTime);
            return new ServerlessResponse(result, executionTime, "SUCCESS", "dataTransformFunction");
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Error in data transformation: {}", e.getMessage());
            return new ServerlessResponse(
                Map.of("error", e.getMessage()),
                executionTime,
                "ERROR",
                "dataTransformFunction"
            );
        }
    }
    
    private Object performTransformation(DataTransformRequest request) {
        String transformationType = request.getTransformationType();
        Object data = request.getData();
        
        // Check for null transformation type
        if (transformationType == null) {
            throw new IllegalArgumentException("Transformation type cannot be null");
        }
        
        switch (transformationType.toLowerCase()) {
            case "uppercase":
                return transformToUpperCase(data);
            case "lowercase":
                return transformToLowerCase(data);
            case "reverse":
                return reverseData(data);
            case "sort":
                return sortData(data);
            case "filter":
                return filterData(data, request.getParameters());
            case "aggregate":
                return aggregateData(data);
            default:
                throw new IllegalArgumentException("Unknown transformation type: " + transformationType);
        }
    }
    
    private Object transformToUpperCase(Object data) {
        if (data instanceof String) {
            return ((String) data).toUpperCase();
        } else if (data instanceof List) {
            List<Object> result = new ArrayList<>();
            for (Object item : (List<?>) data) {
                if (item instanceof String) {
                    result.add(((String) item).toUpperCase());
                } else {
                    result.add(item);
                }
            }
            return result;
        }
        return data;
    }
    
    private Object transformToLowerCase(Object data) {
        if (data instanceof String) {
            return ((String) data).toLowerCase();
        } else if (data instanceof List) {
            List<Object> result = new ArrayList<>();
            for (Object item : (List<?>) data) {
                if (item instanceof String) {
                    result.add(((String) item).toLowerCase());
                } else {
                    result.add(item);
                }
            }
            return result;
        }
        return data;
    }
    
    private Object reverseData(Object data) {
        if (data instanceof String) {
            return new StringBuilder((String) data).reverse().toString();
        } else if (data instanceof List) {
            List<Object> result = new ArrayList<>((List<?>) data);
            Collections.reverse(result);
            return result;
        }
        return data;
    }
    
    private Object sortData(Object data) {
        if (data instanceof List) {
            List<Object> result = new ArrayList<>((List<?>) data);
            result.sort((a, b) -> {
                if (a instanceof Comparable && b instanceof Comparable) {
                    return ((Comparable<Object>) a).compareTo(b);
                }
                return a.toString().compareTo(b.toString());
            });
            return result;
        }
        return data;
    }
    
    private Object filterData(Object data, Object parameters) {
        if (data instanceof List && parameters instanceof Map) {
            Map<String, Object> filterParams = (Map<String, Object>) parameters;
            String filterType = (String) filterParams.get("type");
            Object filterValue = filterParams.get("value");
            
            List<Object> result = new ArrayList<>();
            for (Object item : (List<?>) data) {
                if (matchesFilter(item, filterType, filterValue)) {
                    result.add(item);
                }
            }
            return result;
        }
        return data;
    }
    
    private boolean matchesFilter(Object item, String filterType, Object filterValue) {
        switch (filterType.toLowerCase()) {
            case "contains":
                return item.toString().contains(filterValue.toString());
            case "startswith":
                return item.toString().startsWith(filterValue.toString());
            case "endswith":
                return item.toString().endsWith(filterValue.toString());
            case "equals":
                return item.equals(filterValue);
            default:
                return true;
        }
    }
    
    private Object aggregateData(Object data) {
        if (data instanceof List) {
            List<?> list = (List<?>) data;
            Map<String, Object> result = new HashMap<>();
            result.put("count", list.size());
            result.put("first", list.isEmpty() ? null : list.get(0));
            result.put("last", list.isEmpty() ? null : list.get(list.size() - 1));
            
            List<Number> numbers = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof Number) {
                    numbers.add((Number) item);
                }
            }
            
            if (!numbers.isEmpty()) {
                double sum = numbers.stream().mapToDouble(Number::doubleValue).sum();
                result.put("sum", sum);
                result.put("average", sum / numbers.size());
                result.put("min", numbers.stream().mapToDouble(Number::doubleValue).min().orElse(0.0));
                result.put("max", numbers.stream().mapToDouble(Number::doubleValue).max().orElse(0.0));
            }
            
            return result;
        }
        return Map.of("error", "Data is not a list");
    }
}
