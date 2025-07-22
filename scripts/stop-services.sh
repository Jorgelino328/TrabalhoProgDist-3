#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}Stopping all microservices...${NC}"

# Method 1: Kill all Maven Spring Boot processes
echo -e "${YELLOW}1. Killing all Maven Spring Boot processes...${NC}"
pkill -f "spring-boot:run" && echo -e "${GREEN}✓ Maven processes killed${NC}" || echo -e "${YELLOW}No Maven processes found${NC}"

# Method 2: Kill all Java processes that look like Spring Boot apps
echo -e "${YELLOW}2. Killing Spring Boot Java processes...${NC}"
for pid in $(ps aux | grep -E "(eureka-server|config-server|api-gateway|microservice-|mcp-server)" | grep java | grep -v grep | awk '{print $2}'); do
    if kill -9 "$pid" 2>/dev/null; then
        echo -e "${GREEN}✓ Killed process $pid${NC}"
    fi
done

# Method 3: Kill processes using specific ports
echo -e "${YELLOW}3. Killing processes on microservice ports...${NC}"
for port in 8761 8888 8080 8081 8082 8083 3000; do
    pid=$(lsof -ti:$port 2>/dev/null)
    if [ ! -z "$pid" ]; then
        if kill -9 "$pid" 2>/dev/null; then
            echo -e "${GREEN}✓ Killed process on port $port (PID: $pid)${NC}"
        fi
    fi
done

# Wait a moment for processes to fully terminate
sleep 2

# Verify all services are down
echo -e "${YELLOW}4. Verifying all services are stopped...${NC}"
echo -e "${YELLOW}Checking ports...${NC}"
active_ports=$(netstat -tuln 2>/dev/null | grep -E "(8080|8081|8082|8083|8761|8888|3000)" | wc -l)

if [ "$active_ports" -eq 0 ]; then
    echo -e "${GREEN}✓ All microservice ports are free${NC}"
else
    echo -e "${RED}⚠ Some ports are still in use:${NC}"
    netstat -tuln | grep -E "(8080|8081|8082|8083|8761|8888|3000)"
fi

# Check for remaining Spring Boot processes
remaining_processes=$(ps aux | grep -E "(spring-boot:run|eureka-server|config-server|api-gateway|microservice-|mcp-server)" | grep java | grep -v grep | wc -l)

if [ "$remaining_processes" -eq 0 ]; then
    echo -e "${GREEN}✓ No Spring Boot processes running${NC}"
else
    echo -e "${RED}⚠ Some Spring Boot processes are still running:${NC}"
    ps aux | grep -E "(spring-boot:run|eureka-server|config-server|api-gateway|microservice-|mcp-server)" | grep java | grep -v grep
fi

echo ""
echo -e "${GREEN}🎯 Services shutdown complete!${NC}"
echo -e "${YELLOW}You can now safely restart services with:${NC}"
echo -e "  ./scripts/start-services.sh"
