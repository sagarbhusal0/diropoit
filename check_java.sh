#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}Checking Java installation...${NC}"
echo

# Function to check if Java 17 is installed
check_java_17() {
    java_version=$(java -version 2>&1 | grep "version" | awk -F '"' '{print $2}' | awk -F '.' '{print $1"."$2}')
    if [[ "$java_version" == "17."* ]]; then
        return 0
    else
        return 1
    fi
}

echo -e "${YELLOW}1. Checking Java version:${NC}"
echo "------------------------"
if check_java_17; then
    echo -e "${GREEN}Found Java 17: $(java -version 2>&1 | head -n 1)${NC}"
else
    echo -e "${RED}Java 17 not found. Current version:${NC}"
    java -version 2>&1
    echo
    echo -e "${YELLOW}To install Java 17:${NC}"
    echo "------------------------"
    echo "Windows: Download from https://adoptium.net/temurin/releases/?version=17"
    echo "Ubuntu/Debian: sudo apt install openjdk-17-jdk"
    echo "macOS: brew install openjdk@17"
    echo "Fedora/RHEL: sudo dnf install java-17-openjdk-devel"
    echo "Or run ./install_java17.sh (Linux only)"
fi
echo

echo -e "${YELLOW}2. Checking Java compiler version:${NC}"
echo "------------------------"
javac -version 2>&1
echo

echo -e "${YELLOW}3. Checking JAVA_HOME:${NC}"
echo "------------------------"
if [ -n "$JAVA_HOME" ]; then
    echo -e "${GREEN}JAVA_HOME is set to: $JAVA_HOME${NC}"
    if [[ "$JAVA_HOME" == *"17"* ]]; then
        echo -e "${GREEN}JAVA_HOME points to Java 17${NC}"
    else
        echo -e "${RED}Warning: JAVA_HOME might not point to Java 17${NC}"
        echo "Please update JAVA_HOME to point to your Java 17 installation"
    fi
else
    echo -e "${RED}JAVA_HOME is not set. Please set it to your Java 17 installation path:${NC}"
    echo "Windows: set JAVA_HOME=C:\\Program Files\\Eclipse Adoptium\\jdk-17.x.x"
    echo "Unix/macOS: export JAVA_HOME=/path/to/java-17"
fi
echo

echo -e "${YELLOW}4. Checking Java in PATH:${NC}"
echo "------------------------"
which java
which javac
echo

echo -e "${YELLOW}5. All Java installations:${NC}"
echo "------------------------"
# For Linux
if [ -f /etc/debian_version ]; then
    echo "Debian/Ubuntu systems:"
    update-alternatives --list java 2>/dev/null
    dpkg -l | grep -i "jdk.*17" 2>/dev/null
    dpkg -l | grep -i "jre.*17" 2>/dev/null
elif [ -f /etc/redhat-release ]; then
    echo "Red Hat/CentOS systems:"
    rpm -qa | grep -i "java.*17" 2>/dev/null
    alternatives --display java 2>/dev/null
fi

# For macOS
if [ "$(uname)" == "Darwin" ]; then
    echo "macOS systems:"
    /usr/libexec/java_home -V 2>&1
    ls -l /Library/Java/JavaVirtualMachines/ 2>/dev/null
fi

echo -e "\n${YELLOW}6. Java version details:${NC}"
echo "------------------------"
java -XshowSettings:properties -version 2>&1 | grep -E "java.version|java.home|java.vm"

echo -e "\n${YELLOW}7. Minecraft Mod Development Requirements:${NC}"
echo "------------------------"
if check_java_17; then
    echo -e "${GREEN}✓ Java 17 is installed (required for Minecraft 1.20.1)${NC}"
else
    echo -e "${RED}✗ Java 17 is required for Minecraft 1.20.1 mod development${NC}"
    echo -e "${YELLOW}Please install Java 17 using one of the methods shown above${NC}" 