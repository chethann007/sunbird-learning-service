# Sunbird Learning Service - Build Guide

This repository contains the unified Sunbird Learning Service, which supports multiple deployment modes:
- **Microservices mode**: Build individual services (userorg, lms, notification) separately
- **Monolithic mode**: Build all services together (future)

## Project Structure

```
sunbird-learning-service/
├── pom.xml                          # Root POM with build profiles
├── core/                            # Shared utilities (always built)
│   ├── sunbird-platform-common/
│   ├── sunbird-cassandra-utils/
│   ├── sunbird-es-utils/
│   ├── sunbird-actor-utils/
│   ├── sunbird-notification-utils/
│   └── sunbird-redis-utils/
├── modules/
│   ├── userorg/                     # User & Organization service
│   │   ├── pom.xml                  # Userorg parent POM
│   │   ├── service/                 # Business logic
│   │   └── controller/              # Play Framework web layer
│   ├── lms/                         # Learning Management service (future)
│   └── notification/                # Notification service (future)
└── build/
    ├── userorg/Dockerfile
    ├── lms/
    └── notification/
```

## Building Services

### Prerequisites
- Java 11
- Maven 3.6+
- Docker (for containerization)

### Build Userorg Service

#### Option 1: Using build script (Recommended)
```bash
./build-userorg.sh
```

#### Option 2: Manual build
```bash
# Build core and userorg modules
mvn clean install -P userorg

# Create Play distribution
cd modules/userorg/controller
mvn play2:dist
cd ../../..
```

The distribution artifact will be created at:
```
modules/userorg/controller/target/userorg-service-1.0-SNAPSHOT-dist.zip
```

### Build Docker Image

#### Option 1: Using Docker build script (Recommended)
```bash
./build-userorg-docker.sh
```

This script will:
1. Check if the distribution artifact exists
2. Build the Docker image from project root using the Dockerfile at `build/userorg/Dockerfile`
3. Tag as `chethann07/userorg-service:refactored_service_final`

#### Option 2: Manual Docker build
```bash
# Build from project root (references modules/userorg/controller/target directly)
docker build -f build/userorg/Dockerfile -t chethann07/userorg-service:refactored_service_final .
```

**Note**: The Dockerfile references the distribution artifact directly from `modules/userorg/controller/target/`, so the build must be run from the project root directory.

#### Run the Docker container
```bash
docker run -p 9000:9000 chethann07/userorg-service:refactored_service_final
```

#### Push to Docker Hub
```bash
docker push chethann07/userorg-service:refactored_service_final
```

### Build LMS Service (Future)
```bash
mvn clean install -P lms
cd modules/lms/controller
mvn play2:dist
```

### Build Notification Service (Future)
```bash
mvn clean install -P notification
cd modules/notification/controller
mvn play2:dist
```

### Build All Services (Monolithic Mode - Future)
```bash
mvn clean install -P monolithic
```

## Maven Profiles

| Profile | Description | Modules Built |
|---------|-------------|---------------|
| `userorg` | Build userorg service only | core + modules/userorg |
| `lms` | Build lms service only | core + modules/lms |
| `notification` | Build notification service only | core + modules/notification |
| `monolithic` | Build all services together | core + all modules |

## Development Workflow

### Building for Development
```bash
# Build without tests
mvn clean install -P userorg -DskipTests

# Build with tests
mvn clean install -P userorg
```

### Running Tests
```bash
# Run tests for userorg service
mvn test -P userorg

# Run tests for specific module
cd modules/userorg/service
mvn test
```

### Creating Distribution
```bash
# The play2:dist goal must be run from the controller directory
cd modules/userorg/controller
mvn play2:dist
```

## Backward Compatibility

This build structure maintains backward compatibility with the original userorg repository:
- Same artifact name: `userorg-service`
- Same build process: `mvn clean install` → `cd controller` → `mvn play2:dist`
- Same distribution artifact location and name
- Same Docker build process

## Migration Path

### Phase 1: Userorg Service ✓
- Restructured POMs for multi-service support
- Added Maven profiles for build modes
- Created build scripts

### Phase 2: LMS Service (Planned)
- Add LMS modules under `modules/lms/`
- Create `modules/lms/pom.xml`
- Add LMS profile to root POM

### Phase 3: Notification Service (Planned)
- Add Notification modules under `modules/notification/`
- Create `modules/notification/pom.xml`
- Add Notification profile to root POM

### Phase 4: Monolithic Mode (Planned)
- Configure monolithic profile to build all services
- Create unified distribution (if needed)

## Troubleshooting

### Build fails with "Could not find artifact"
Make sure you're using the correct profile:
```bash
mvn clean install -P userorg
```

### Play distribution fails
Ensure you're in the controller directory:
```bash
cd modules/userorg/controller
mvn play2:dist
```

### Docker build fails
Verify the distribution artifact exists:
```bash
ls -lh modules/userorg/controller/target/userorg-service-1.0-SNAPSHOT-dist.zip
```
