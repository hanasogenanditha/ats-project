#!/bin/bash

################################################################################
# ATS Project - Automated Setup Script
# This script builds and runs the entire ATS system with a single command
################################################################################

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}============================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}============================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# Check prerequisites
check_prerequisites() {
    print_header "Checking Prerequisites"
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker not found. Please install Docker from https://www.docker.com"
        exit 1
    fi
    print_success "Docker installed"
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose not found. Please install Docker Compose"
        exit 1
    fi
    print_success "Docker Compose installed"
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_warning "Maven not found locally - will use ./mvnw instead"
    else
        print_success "Maven installed"
    fi
    
    # Check Git
    if ! command -v git &> /dev/null; then
        print_warning "Git not found (optional - only needed for version control)"
    else
        print_success "Git installed"
    fi
}

# Clean previous builds
clean_builds() {
    print_header "Cleaning Previous Builds"
    
    print_info "Removing old target directories and containers..."
    docker-compose down -v 2>/dev/null || true
    
    find . -type d -name "target" -path "*/ATS-Project/*" -exec rm -rf {} + 2>/dev/null || true
    print_success "Clean complete"
}

# Build shared module first (required by other services)
build_shared_module() {
    print_header "Building Shared Module"
    
    cd shared-module
    print_info "Running: mvn clean install"
    
    if [ -f "./mvnw" ]; then
        ./mvnw clean install -q
    else
        mvn clean install -q
    fi
    
    print_success "Shared module built successfully"
    cd ..
}

# Build individual services
build_services() {
    print_header "Building Microservices"
    
    services=("job-service" "application-service" "screening-service" "notification-service" "api-gateway")
    
    for service in "${services[@]}"; do
        print_info "Building $service..."
        cd "$service"
        
        if [ -f "./mvnw" ]; then
            ./mvnw clean package -q -DskipTests
        else
            mvn clean package -q -DskipTests
        fi
        
        print_success "$service built"
        cd ..
    done
}

# Additional build step for Docker images
build_docker_images() {
    print_header "Building Docker Images"
    
    print_info "Running: docker-compose build"
    docker-compose build 2>&1 | grep -E "(Building|built|Successfully)" || true
    
    print_success "Docker images built successfully"
}

# Check Gmail configuration
check_gmail_config() {
    print_header "Email Configuration Check"
    
    config_file="notification-service/src/main/resources/application.properties"
    
    if grep -q "your-email@gmail.com" "$config_file"; then
        print_warning "Gmail email address not configured"
        print_warning "Before running, configure:"
        print_warning "  File: $config_file"
        print_warning "  Set: spring.mail.username=your-actual-email@gmail.com"
        print_warning "  Set: spring.mail.password=your-google-app-password"
        print_info "See README.md for detailed Gmail setup instructions"
        echo ""
        read -p "Continue without email? (y/n) " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    else
        print_success "Email configured"
    fi
}

# Start services
start_services() {
    print_header "Starting Services"
    
    print_info "Pulling latest images..."
    docker-compose pull 2>&1 | grep -E "(Pulling|Digest)" || true
    
    print_info "Starting services... (this may take 30-60 seconds)"
    docker-compose up -d
    
    print_success "Services started"
}

# Wait for services to be ready
wait_for_services() {
    print_header "Waiting for Services to Be Ready"
    
    services=("application-service" "job-service" "screening-service" "notification-service" "api-gateway")
    max_wait=60
    elapsed=0
    
    for service in "${services[@]}"; do
        print_info "Waiting for $service to start..."
        
        while [ $elapsed -lt $max_wait ]; do
            if docker-compose logs "$service" 2>/dev/null | grep -q "Started\|Application startup"; then
                print_success "$service is ready"
                break
            fi
            sleep 2
            elapsed=$((elapsed + 2))
        done
        
        if [ $elapsed -ge $max_wait ]; then
            print_warning "$service took longer than expected - check logs with: docker-compose logs $service"
        fi
        elapsed=0
    done
}

# Display startup info
display_startup_info() {
    print_header "Services Ready!"
    
    echo ""
    echo -e "${GREEN}ATS System is now running!${NC}"
    echo ""
    echo -e "${BLUE}Access Points:${NC}"
    echo "  API Gateway:         http://localhost:8080"
    echo "  Application Service: http://localhost:8081"
    echo "  Screening Service:   http://localhost:8082"
    echo "  Notification Service:http://localhost:8083"
    echo "  Job Service:         http://localhost:8084"
    echo ""
    echo -e "${BLUE}Useful Commands:${NC}"
    echo "  View logs:           docker-compose logs -f"
    echo "  View specific log:   docker-compose logs -f [service-name]"
    echo "  Stop services:       docker-compose down"
    echo "  Stop & clean:        docker-compose down -v"
    echo ""
    echo -e "${BLUE}Test Endpoints:${NC}"
    echo "  Create Job:"
    echo "    curl -X POST http://localhost:8084/jobs \\"
    echo "      -H 'Content-Type: application/json' \\"
    echo "      -d '{\"title\":\"Dev\",\"description\":\"Java dev\"}'"
    echo ""
    echo "  Submit Application:"
    echo "    curl -X POST http://localhost:8081/apply \\"
    echo "      -H 'Content-Type: application/json' \\"
    echo "      -d '{\"candidateName\":\"John\",\"candidateEmail\":\"john@test.com\",\"resumeText\":\"Resume text\",\"jobId\":1}'"
    echo ""
    echo -e "${BLUE}Documentation:${NC}"
    echo "  See README.md for detailed API examples and troubleshooting"
    echo ""
}

# Main execution
main() {
    clear
    
    print_header "ATS Project - Automated Setup"
    echo "Starting automated build and deployment..."
    echo ""
    
    # Run all setup steps
    check_prerequisites
    clean_builds
    build_shared_module
    build_services
    build_docker_images
    check_gmail_config
    start_services
    wait_for_services
    display_startup_info
    
    print_success "Setup complete! Services are running."
}

# Run main function
main

# Keep script from exiting if run directly
exit 0
