#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get the absolute path of the project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Function to log messages
log() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] $1${NC}"
}

# Function to check if a service is ready
check_service_ready() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    log "Waiting for $service_name at $url to be ready..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" > /dev/null; then
            log "${GREEN}$service_name is ready!${NC}"
            return 0
        fi
        log "Attempt $attempt of $max_attempts..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    log "${RED}$service_name failed to start within the expected time${NC}"
    return 1
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    log "Starting $service_name..."
    
    # Check if service directory exists
    if [ ! -d "$PROJECT_ROOT/$service_dir" ]; then
        log "${RED}Error: $service_dir directory not found${NC}"
        return 1
    fi
    
    # Change to service directory
    cd "$PROJECT_ROOT/$service_dir" || {
        log "${RED}Error: Failed to change to $service_dir directory${NC}"
        return 1
    }
    
    # Start the service in the background
    log "Running mvn spring-boot:run for $service_name..."
    mvn spring-boot:run > "$PROJECT_ROOT/$service_name.log" 2>&1 &
    
    # Store the process ID
    echo $! > "$PROJECT_ROOT/$service_name.pid"
    
    # Wait for the service to be ready
    if ! check_service_ready "http://localhost:$port" "$service_name"; then
        log "${RED}Failed to start $service_name${NC}"
        # Kill the process if it exists
        if [ -f "$PROJECT_ROOT/$service_name.pid" ]; then
            kill $(cat "$PROJECT_ROOT/$service_name.pid") 2>/dev/null || true
            rm "$PROJECT_ROOT/$service_name.pid"
        fi
        return 1
    fi
    
    return 0
}

# Function to cleanup processes
cleanup() {
    log "Cleaning up processes..."
    for service in service-registry auth-service order-service item-service gateway-service api-gateway; do
        if [ -f "$PROJECT_ROOT/$service.pid" ]; then
            log "Stopping $service..."
            kill $(cat "$PROJECT_ROOT/$service.pid") 2>/dev/null || true
            rm "$PROJECT_ROOT/$service.pid"
        fi
    done
    exit 0
}

# Set up trap for cleanup on script exit
trap cleanup EXIT INT TERM

# Kill any existing processes
log "Cleaning up any existing processes..."
for service in service-registry auth-service order-service item-service gateway-service api-gateway; do
    if [ -f "$PROJECT_ROOT/$service.pid" ]; then
        kill $(cat "$PROJECT_ROOT/$service.pid") 2>/dev/null || true
        rm "$PROJECT_ROOT/$service.pid"
    fi
done

# Start services in order
log "Starting services..."

# Start service registry first
if ! start_service "service-registry" "service-registry" "8761"; then
    log "${RED}Failed to start service registry. Exiting...${NC}"
    exit 1
fi

# Start API Gateway
if ! start_service "api-gateway" "api-gateway" "8080"; then
    log "${RED}Failed to start API Gateway. Exiting...${NC}"
    exit 1
fi

# Start other services
if ! start_service "gateway-service" "gateway-service" "8081"; then
    log "${RED}Failed to start Gateway Service. Exiting...${NC}"
    exit 1
fi

if ! start_service "auth-service" "auth-service" "8082"; then
    log "${RED}Failed to start Auth Service. Exiting...${NC}"
    exit 1
fi

if ! start_service "order-service" "order-service" "8083"; then
    log "${RED}Failed to start Order Service. Exiting...${NC}"
    exit 1
fi

if ! start_service "item-service" "item-service" "8084"; then
    log "${RED}Failed to start Item Service. Exiting...${NC}"
    exit 1
fi

log "${GREEN}All services have been started successfully!${NC}"
echo -e "\nService URLs:"
echo "Service Registry: http://localhost:8761"
echo "API Gateway: http://localhost:8080"
echo "Gateway Service: http://localhost:8081"
echo "Auth Service: http://localhost:8082"
echo "Order Service: http://localhost:8083"
echo "Item Service: http://localhost:8084"

# Keep the script running
log "Press Ctrl+C to stop all services"
wait 