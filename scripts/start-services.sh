#!/bin/bash

# Change to project root directory
cd /home/jorgelino/TrabalhoProgDist-3

set -e
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}Starting Microservices System...${NC}"

# Load environment variables
if [ -f .env ]; then
    echo -e "${YELLOW}Loading environment variables from .env file...${NC}"
    export $(cat .env | grep -v ^# | xargs)
    echo -e "${GREEN}Environment variables loaded successfully${NC}"
else
    echo -e "${RED}Warning: .env file not found. Some services may fail to start.${NC}"
fi

# Validate critical environment variables
if [ -z "$OPENAI_API_KEY" ]; then
    echo -e "${RED}Error: OPENAI_API_KEY is not set. Please check your .env file.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ OPENAI_API_KEY is configured${NC}"

# Create logs directory if it doesn't exist
mkdir -p logs

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    echo -e "${YELLOW}Waiting for $service_name to be ready...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ $service_name is ready${NC}"
            return 0
        fi
        echo -e "${YELLOW}Attempt $attempt/$max_attempts - $service_name not ready yet...${NC}"
        sleep 2
        ((attempt++))
    done
    
    echo -e "${RED}✗ $service_name failed to start within timeout${NC}"
    return 1
}

# Start services in order
echo -e "${YELLOW}1. Starting Eureka Server...${NC}"
(cd /home/jorgelino/TrabalhoProgDist-3/eureka-server && mvn spring-boot:run > ../logs/eureka.log 2>&1) &
sleep 5

# Wait for Eureka to be ready
wait_for_service "http://localhost:8761/actuator/health" "Eureka Server"

echo -e "${YELLOW}2. Starting Config Server...${NC}"
(cd /home/jorgelino/TrabalhoProgDist-3/config-server && mvn spring-boot:run > ../logs/config-server.log 2>&1) &
sleep 5

# Wait for Config Server
wait_for_service "http://localhost:8888/actuator/health" "Config Server"

echo -e "${YELLOW}3. Starting MCP Server...${NC}"
(cd /home/jorgelino/TrabalhoProgDist-3/mcp-server && mvn spring-boot:run > ../logs/mcp-server.log 2>&1) &
sleep 5

wait_for_service "http://localhost:3000/health" "MCP Server"

echo -e "${YELLOW}4. Starting API Gateway...${NC}"
(cd /home/jorgelino/TrabalhoProgDist-3/api-gateway && mvn spring-boot:run > ../logs/api-gateway.log 2>&1) &
sleep 5

echo -e "${YELLOW}5. Starting Microservice MCP...${NC}"
(cd /home/jorgelino/TrabalhoProgDist-3/microservice-mcp && mvn spring-boot:run > ../logs/mcp.log 2>&1) &
sleep 5

echo -e "${YELLOW}6. Starting Microservice AI...${NC}"
(cd /home/jorgelino/TrabalhoProgDist-3/microservice-ai && mvn spring-boot:run -Dspring-boot.run.profiles=local > ../logs/ai.log 2>&1) &
sleep 5

echo -e "${YELLOW}7. Starting Microservice Serverless...${NC}"
(cd /home/jorgelino/TrabalhoProgDist-3/microservice-serverless && mvn spring-boot:run > ../logs/serverless.log 2>&1) &

# Wait for all services to register with Eureka
echo -e "${YELLOW}Waiting for all services to start and register...${NC}"
sleep 30

echo -e "${GREEN}All services started!${NC}"
echo ""
echo -e "${YELLOW}Service URLs:${NC}"
echo -e "  Eureka Dashboard: http://localhost:8761"
echo -e "  API Gateway: http://localhost:8080"
echo -e "  Config Server: http://localhost:8888"
echo -e "  MCP Server: http://localhost:3000"
echo -e "  AI Service: http://localhost:8082"
echo -e "  MCP Service: http://localhost:8081"
echo -e "  Serverless Service: http://localhost:8083"
echo ""
echo -e "${YELLOW}Now run your health check to verify all services are up:${NC}"
echo -e "  ./scripts/check-health.sh"
