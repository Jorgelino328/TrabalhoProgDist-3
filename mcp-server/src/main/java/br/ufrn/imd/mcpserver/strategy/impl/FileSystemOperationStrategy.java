package br.ufrn.imd.mcpserver.strategy.impl;

import br.ufrn.imd.mcpserver.dto.McpRequest;
import br.ufrn.imd.mcpserver.strategy.McpOperationStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FileSystemOperationStrategy implements McpOperationStrategy {

    private static final String BASE_PATH = "/tmp/mcp-files";

    @Override
    public Object execute(McpRequest request) {
        Map<String, Object> params = (Map<String, Object>) request.getParams();
        String operation = (String) params.get("operation");

        try {
            switch (operation.toLowerCase()) {
                case "list":
                    return listFiles((String) params.get("path"));
                case "read":
                    return readFile((String) params.get("path"));
                case "write":
                    return writeFile((String) params.get("path"), (String) params.get("content"));
                case "delete":
                    return deleteFile((String) params.get("path"));
                case "create_directory":
                    return createDirectory((String) params.get("path"));
                default:
                    throw new IllegalArgumentException("Unknown filesystem operation: " + operation);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", -1);
            error.put("message", "Filesystem operation failed: " + e.getMessage());
            return error;
        }
    }

    @Override
    public boolean supports(String method) {
        return "filesystem".equals(method);
    }

    private Object listFiles(String pathStr) throws IOException {
        Path path = Paths.get(BASE_PATH, pathStr != null ? pathStr : "");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        List<Map<String, Object>> files = Files.list(path)
                .map(p -> {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", p.getFileName().toString());
                    fileInfo.put("type", Files.isDirectory(p) ? "directory" : "file");
                    try {
                        fileInfo.put("size", Files.size(p));
                    } catch (IOException e) {
                        fileInfo.put("size", 0);
                    }
                    return fileInfo;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("files", files);
        result.put("path", path.toString());
        return result;
    }

    private Object readFile(String pathStr) throws IOException {
        Path path = Paths.get(BASE_PATH, pathStr);
        if (!Files.exists(path)) {
            throw new RuntimeException("File not found: " + pathStr);
        }

        String content = Files.readString(path);
        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("path", pathStr);
        result.put("size", content.length());
        return result;
    }

    private Object writeFile(String pathStr, String content) throws IOException {
        Path path = Paths.get(BASE_PATH, pathStr);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "File written successfully");
        result.put("path", pathStr);
        result.put("size", content.length());
        return result;
    }

    private Object deleteFile(String pathStr) throws IOException {
        Path path = Paths.get(BASE_PATH, pathStr);
        if (!Files.exists(path)) {
            throw new RuntimeException("File not found: " + pathStr);
        }

        Files.delete(path);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "File deleted successfully");
        result.put("path", pathStr);
        return result;
    }

    private Object createDirectory(String pathStr) throws IOException {
        Path path = Paths.get(BASE_PATH, pathStr);
        Files.createDirectories(path);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Directory created successfully");
        result.put("path", pathStr);
        return result;
    }
}
