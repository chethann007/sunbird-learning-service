#!/bin/bash

###############################################################################
# Docker build script for LMS Service
# This script builds the Docker image for LMS service
###############################################################################

set -e  # Exit on error

echo "========================================="
echo "Building LMS Service Docker Image"
echo "========================================="

# Get the script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Check if distribution exists
DIST_FILE="modules/lms/service/target/lms-service-1.0-SNAPSHOT-dist.zip"
if [ ! -f "$DIST_FILE" ]; then
    echo "Error: Distribution file not found at $DIST_FILE"
    echo "Please run ./build-lms.sh first to build the service"
    exit 1
fi

echo ""
echo "Step 1: Building Docker image from project root..."
echo "Using Dockerfile: build/lms/Dockerfile"
echo "Distribution: $DIST_FILE"
docker build -f build/lms/Dockerfile -t chethann07/lms-service:refactored_service_final .

echo ""
echo "========================================="
echo "Docker image built successfully!"
echo "========================================="
echo ""
echo "Image: chethann07/lms-service:refactored_service_final"
echo ""
echo "To run the container:"
echo "  docker run -p 9000:9000 chethann07/lms-service:refactored_service_final"
echo ""
echo "To push to Docker Hub:"
echo "  docker push chethann07/lms-service:refactored_service_final"
echo ""
