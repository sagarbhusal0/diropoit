#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Starting build...${NC}"

# Ensure gradlew is executable
chmod +x gradlew

# Run build using the Gradle wrapper
# The wrapper will automatically download the correct Gradle version if needed.
# The build.gradle file already contains a check for Java 17.
./gradlew build

# Check build result
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Build completed successfully!${NC}"
    JAR_FILE=$(find build/libs -name "*.jar" | grep -v "sources.jar" | grep -v "dev.jar")
    if [ -n "$JAR_FILE" ]; then
        echo -e "${GREEN}JAR file location: $JAR_FILE${NC}"
    fi
else
    echo -e "${RED}Build failed! See logs for details.${NC}"
    exit 1
fi
