#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Attempting to install Java 17 (Temurin)...${NC}"

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for existing Java 17
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$JAVA_VERSION" == "17."* ]]; then
        echo -e "${GREEN}Java 17 is already installed.${NC}"
        java -version
        exit 0
    fi
fi

# Detect distribution
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$NAME
else
    echo -e "${RED}Cannot detect Linux distribution.${NC}"
    exit 1
fi

# Install Java 17 based on distribution
case $OS in
    "Ubuntu"|"Debian GNU/Linux")
        echo -e "${YELLOW}Detected Debian/Ubuntu-based system.${NC}"
        sudo apt-get update
        sudo apt-get install -y wget apt-transport-https
        wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo apt-key add -
        echo "deb https://packages.adoptium.net/artifactory/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/adoptium.list
        sudo apt-get update
        sudo apt-get install -y temurin-17-jdk
        ;;
    "Fedora")
        echo -e "${YELLOW}Detected Fedora system.${NC}"
        sudo dnf install -y java-17-openjdk-devel
        ;;
    "CentOS Linux"|"Red Hat Enterprise Linux")
        echo -e "${YELLOW}Detected CentOS/RHEL system.${NC}"
        sudo yum install -y java-17-openjdk-devel
        ;;
    "Arch Linux")
        echo -e "${YELLOW}Detected Arch Linux system.${NC}"
        sudo pacman -Syu --noconfirm jdk17-openjdk
        ;;
    *)
        echo -e "${RED}Unsupported distribution: $OS${NC}"
        echo "Please install Java 17 manually from: https://adoptium.net/temurin/releases/?version=17"
        exit 1
        ;;
esac

# Verify Java installation
if command_exists java && [[ "$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')" == "17."* ]]; then
    echo -e "${GREEN}Java 17 installation completed successfully!${NC}"
    echo -e "${YELLOW}Please set it as your default Java version or configure your JAVA_HOME environment variable.${NC}"
    echo "Example: export JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64"
else
    echo -e "${RED}Java 17 installation failed. Please check the output above for errors.${NC}"
    exit 1
fi 