#!/bin/bash

###############################################################################
# Build script for Userorg Service
# This script builds the userorg service in microservices mode
###############################################################################

set -e  # Exit on error

echo "========================================="
echo "Building Userorg Service"
echo "========================================="

# Get the script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo ""
echo "Step 1: Building core dependencies and userorg modules..."
mvn clean install -P userorg

echo ""
echo "Step 2: Creating Play distribution..."
cd modules/userorg/controller
mvn play2:dist

echo ""
echo "========================================="
echo "Build completed successfully!"
echo "========================================="
echo ""
echo "Distribution artifact created at:"
echo "  modules/userorg/controller/target/userorg-service-1.0-SNAPSHOT-dist.zip"
echo ""
echo "To build Docker image:"
echo "  docker build -f build/userorg/Dockerfile -t chethann07/userorg-service:refactored_service_final ."
echo ""
echo "Or run the Docker build script:"
echo "  ./build-userorg-docker.sh"
echo ""
