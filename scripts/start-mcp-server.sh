#!/bin/bash

cd "$(dirname "$0")/.."

mkdir -p logs

npx @modelcontextprotocol/server-filesystem --port 3000 --root $(pwd) > logs/mcp-server.log 2>&1 &
echo $! > mcp-server.pid

echo "Filesystem MCP Server started on port 3000"
echo "PID: $(cat mcp-server.pid)"
echo "Logs: logs/mcp-server.log"
echo "Allowed root directory: $(pwd)"
