#!/bin/bash

# Docker build script for Notification service
# This script builds the Docker image for the notification service

set -e

# Configuration
IMAGE_NAME="${IMAGE_NAME:-notification-service}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
FULL_IMAGE_NAME="${IMAGE_NAME}:${IMAGE_TAG}"

echo "======================================"
echo "Building Notification Service Docker Image"
echo "======================================"
echo "Image: ${FULL_IMAGE_NAME}"
echo "======================================"

# Check if distribution exists
DIST_FILE="modules/notification/service/target/notification-service-1.0.0-dist.zip"
if [ ! -f "$DIST_FILE" ]; then
    echo "ERROR: Distribution file not found: $DIST_FILE"
    echo "Please run ./build-notification.sh first"
    exit 1
fi

# Build Docker image from project root
echo "Building Docker image..."
docker build -f build/notification/Dockerfile -t "${FULL_IMAGE_NAME}" .

echo "======================================"
echo "Docker Build Complete!"
echo "======================================"
echo "Image: ${FULL_IMAGE_NAME}"
echo ""
echo "To run the container:"
echo "  docker run -p 9000:9000 ${FULL_IMAGE_NAME}"
echo ""
echo "To push to registry:"
echo "  docker push ${FULL_IMAGE_NAME}"
