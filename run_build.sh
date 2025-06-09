#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get system memory in GB
TOTAL_MEM=$(free -g | awk '/^Mem:/{print $2}')
# Calculate memory limits (use 25% of total memory)
MAX_MEM=$((TOTAL_MEM / 4))
INIT_MEM=$((MAX_MEM / 2))
# Ensure minimum values
if [ $MAX_MEM -lt 2 ]; then MAX_MEM=2; fi
if [ $INIT_MEM -lt 1 ]; then INIT_MEM=1; fi

echo -e "${YELLOW}Building with memory settings: Initial=${INIT_MEM}G, Max=${MAX_MEM}G${NC}"

# Kill any running Java processes (optional, uncomment if needed)
# echo "Stopping Java processes..."
# pkill java

# Set memory limits
export GRADLE_OPTS="-Xmx${MAX_MEM}G -Xms${INIT_MEM}G -XX:MaxMetaspaceSize=512M -XX:+UseParallelGC -XX:ParallelGCThreads=4"
export JAVA_OPTS="-Xmx${MAX_MEM}G -Xms${INIT_MEM}G -XX:MaxMetaspaceSize=512M -XX:+UseParallelGC -XX:ParallelGCThreads=4"

# Check for Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}Java not found. Please run ./install_java17.sh first${NC}"
    exit 1
fi

# Verify Java version
JAVA_VER=$(java -version 2>&1 | head -n 1)
if [[ ! $JAVA_VER =~ "17" ]]; then
    echo -e "${RED}Warning: Java 17 is required. Current version:${NC}"
    echo "$JAVA_VER"
    echo -e "${YELLOW}Please run ./install_java17.sh to install Java 17${NC}"
    exit 1
fi

# Clean old files
echo -e "${YELLOW}Cleaning old build files...${NC}"
rm -rf .gradle gradle build
rm -f gradlew gradlew.bat

# Create directories
mkdir -p gradle/wrapper

# Download Gradle wrapper files
echo -e "${YELLOW}Downloading Gradle wrapper...${NC}"
curl -s -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar
curl -s -o gradle/wrapper/gradle-wrapper.properties https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.properties

# Make gradlew executable
echo -e "${YELLOW}Setting up Gradle wrapper...${NC}"
gradle wrapper --gradle-version 8.5 --no-daemon
chmod +x gradlew

# Run build
echo -e "${YELLOW}Starting build...${NC}"
./gradlew --no-daemon --parallel --max-workers=4 clean build

# Check build result
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Build completed successfully!${NC}"
    echo -e "${GREEN}JAR file location: $(find build/libs -name "*.jar")${NC}"
else
    echo -e "${RED}Build failed!${NC}"
    exit 1
fi
