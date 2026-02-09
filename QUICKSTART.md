# Quick Reference - Sunbird Learning Service Build

## Build Commands

### Userorg Service
```bash
# Quick build (recommended)
./build-userorg.sh

# Manual build
mvn clean install -P userorg
cd modules/userorg/controller && mvn play2:dist

# Skip tests
mvn clean install -P userorg -DskipTests
```

### Docker
```bash
# Build image (from project root)
docker build -f build/userorg/Dockerfile -t chethann07/userorg-service:refactored_service_final .

# Or use the build script
./build-userorg-docker.sh

# Run container
docker run -p 9000:9000 chethann07/userorg-service:refactored_service_final
```

## Maven Profiles

| Profile | Command | Purpose |
|---------|---------|---------|
| `userorg` | `mvn clean install -P userorg` | Build userorg service |
| `lms` | `mvn clean install -P lms` | Build LMS service (future) |
| `notification` | `mvn clean install -P notification` | Build notification service (future) |
| `monolithic` | `mvn clean install -P monolithic` | Build all services (future) |

## Key Files

- **Root POM**: `pom.xml` (sunbird-learning-service)
- **Userorg Parent**: `modules/userorg/pom.xml`
- **Build Script**: `build-userorg.sh`
- **Documentation**: `BUILD.md`
- **Distribution**: `modules/userorg/controller/target/userorg-service-1.0-SNAPSHOT-dist.zip`

## Troubleshooting

**Build fails**: Ensure you're using the correct profile
```bash
mvn clean install -P userorg
```

**Distribution missing**: Run from controller directory
```bash
cd modules/userorg/controller
mvn play2:dist
```

**Docker build fails**: Verify artifact exists
```bash
ls -lh modules/userorg/controller/target/userorg-service-1.0-SNAPSHOT-dist.zip
```

## Next Steps

See [BUILD.md](file:///home/sanketika2/Downloads/sanketika/sunbird_lern/merge/final_merge/final_merged_service/BUILD.md) for complete documentation.
