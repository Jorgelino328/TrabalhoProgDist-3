package br.ufrn.imd.microserviceserverless.controller;

import br.ufrn.imd.microserviceserverless.dto.DataTransformRequest;
import br.ufrn.imd.microserviceserverless.dto.ServerlessResponse;
import br.ufrn.imd.microserviceserverless.service.ServerlessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/serverless")
public class ServerlessController {
    
    @Autowired
    private ServerlessService serverlessService;
    
    @PostMapping("/function/{functionName}")
    public ResponseEntity<ServerlessResponse> executeFunction(
            @PathVariable String functionName,
            @RequestBody DataTransformRequest request) {
        ServerlessResponse response = serverlessService.executeFunction(functionName, request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<ServerlessResponse> validateData(@RequestBody Object data) {
        ServerlessResponse response = serverlessService.validateData(data);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<ServerlessResponse> health() {
        return ResponseEntity.ok(new ServerlessResponse(
            "Serverless service is healthy", 
            0, 
            "SUCCESS", 
            "health-check"
        ));
    }
}
