#!/bin/bash

cd "$(dirname "$0")/.."

if [ -f mcp-server.pid ]; then
    PID=$(cat mcp-server.pid)
    if kill -0 $PID 2>/dev/null; then
        echo "Stopping MCP Server (PID: $PID)..."
        kill $PID
        rm mcp-server.pid
        echo "MCP Server stopped"
    else
        echo "MCP Server process not found"
        rm mcp-server.pid
    fi
else
    echo "MCP Server PID file not found"
fi
