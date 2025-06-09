#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}Checking Java installation...${NC}"
echo

echo -e "${YELLOW}1. Checking Java version:${NC}"
echo "------------------------"
java -version 2>&1
echo

echo -e "${YELLOW}2. Checking Java compiler version:${NC}"
echo "------------------------"
javac -version 2>&1
echo

echo -e "${YELLOW}3. Checking JAVA_HOME:${NC}"
echo "------------------------"
if [ -n "$JAVA_HOME" ]; then
    echo -e "${GREEN}JAVA_HOME is set to: $JAVA_HOME${NC}"
else
    echo "JAVA_HOME is not set"
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
    dpkg -l | grep -i jdk 2>/dev/null
    dpkg -l | grep -i jre 2>/dev/null
elif [ -f /etc/redhat-release ]; then
    echo "Red Hat/CentOS systems:"
    rpm -qa | grep -i java 2>/dev/null
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