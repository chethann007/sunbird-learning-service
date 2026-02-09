#!/bin/bash

###############################################################################
# Build script for LMS Service
# This script builds the LMS service in microservices mode
###############################################################################

set -e  # Exit on error

echo "========================================="
echo "Building LMS Service"
echo "========================================="

# Get the script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo ""
echo "Step 1: Building core dependencies and LMS modules..."
mvn clean install -P lms

echo ""
echo "Step 2: Creating Play distribution..."
cd modules/lms/service
mvn play2:dist

echo ""
echo "========================================="
echo "Build completed successfully!"
echo "========================================="
echo ""
echo "Distribution artifact created at:"
echo "  modules/lms/service/target/lms-service-1.0-SNAPSHOT-dist.zip"
echo ""
echo "To build Docker image:"
echo "  docker build -f build/lms/Dockerfile -t chethann07/lms-service:refactored_service_final ."
echo ""
echo "Or run the Docker build script:"
echo "  ./build-lms-docker.sh"
echo ""
