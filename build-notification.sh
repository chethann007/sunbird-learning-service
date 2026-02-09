#!/bin/bash

# Build script for Notification service
# This script builds the notification service using Maven profiles

set -e

echo "======================================"
echo "Building Notification Service"
echo "======================================"

# Build with notification profile
echo "Running Maven build with notification profile..."
mvn clean install -P notification -DskipTests

# Create Play distribution
echo "Creating Play distribution..."
cd modules/notification/service
mvn play2:dist
cd ../../..

echo "======================================"
echo "Build Complete!"
echo "======================================"
echo "Distribution created at: modules/notification/service/target/notification-service-1.0.0-dist.zip"
echo ""
echo "To build Docker image, run:"
echo "  ./build-notification-docker.sh"
echo ""
echo "Or manually:"
echo "  docker build -f build/notification/Dockerfile -t <your-tag> ."
