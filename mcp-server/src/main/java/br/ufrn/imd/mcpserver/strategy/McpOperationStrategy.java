package br.ufrn.imd.mcpserver.strategy;

import br.ufrn.imd.mcpserver.dto.McpRequest;

public interface McpOperationStrategy {
    Object execute(McpRequest request);
    boolean supports(String method);
}
