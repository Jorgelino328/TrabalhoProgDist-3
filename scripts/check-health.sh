#!/bin/bash

echo "=== CHECKING IF SERVICES ARE ACTUALLY RUNNING ==="

echo -n "1. Eureka (8761): "
if curl -s --connect-timeout 3 http://localhost:8761/actuator/health > /dev/null 2>&1; then
    echo "RUNNING"
else
    echo "NOT RESPONDING"
fi

echo -n "2. Config Server (8888): "
if curl -s --connect-timeout 3 http://localhost:8888/actuator/health > /dev/null 2>&1; then
    echo "RUNNING"
else
    echo "NOT RESPONDING"
fi

echo -n "3. API Gateway (8080): "
if curl -s --connect-timeout 3 http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "RUNNING"
else
    echo "NOT RESPONDING"
fi

echo -n "4. MCP Server (3000): "
if curl -s --connect-timeout 3 http://localhost:3000/health > /dev/null 2>&1; then
    echo "RUNNING"
else
    echo "NOT RESPONDING"
fi

echo -n "5. MCP Service (8081): "
if curl -s --connect-timeout 3 http://localhost:8081/mcp/health > /dev/null 2>&1; then
    echo "RUNNING"
else
    echo "NOT RESPONDING"
fi

echo -n "6. AI Service (8082): "
if curl -s --connect-timeout 3 http://localhost:8082/ai/health > /dev/null 2>&1; then
    echo "RUNNING"
else
    echo "NOT RESPONDING"
fi

echo -n "7. Serverless (8083): "
if curl -s --connect-timeout 3 http://localhost:8083/actuator/health > /dev/null 2>&1; then
    echo "RUNNING"
else
    echo "NOT RESPONDING"
fi

echo ""
echo "=== PORT CHECK ==="
netstat -tuln | grep -E "(8080|8081|8082|8083|8761|8888|3000)" | sort