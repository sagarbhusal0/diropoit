#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Installing Java 17 (Temurin) on Linux${NC}"

# Detect distribution
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$NAME
    VER=$VERSION_ID
else
    echo -e "${RED}Cannot detect Linux distribution${NC}"
    exit 1
fi

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Install Java 17 based on distribution
case $OS in
    *"Ubuntu"*|*"Debian"*)
        echo -e "${YELLOW}Detected Debian/Ubuntu-based system${NC}"
        
        # Check if wget is installed
        if ! command_exists wget; then
            echo "Installing wget..."
            sudo apt-get update
            sudo apt-get install -y wget
        fi
        
        # Add Adoptium repository
        echo "Adding Adoptium repository..."
        wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo tee /etc/apt/trusted.gpg.d/adoptium.asc
        echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list
        
        # Install Java 17
        sudo apt-get update
        sudo apt-get install -y temurin-17-jdk
        ;;
        
    *"Fedora"*)
        echo -e "${YELLOW}Detected Fedora system${NC}"
        sudo dnf install -y java-17-openjdk-devel
        ;;
        
    *"CentOS"*|*"Red Hat"*)
        echo -e "${YELLOW}Detected CentOS/RHEL system${NC}"
        sudo yum install -y java-17-openjdk-devel
        ;;
        
    *"Arch"*)
        echo -e "${YELLOW}Detected Arch Linux system${NC}"
        sudo pacman -Sy jdk17-openjdk
        ;;
        
    *)
        echo -e "${RED}Unsupported distribution: $OS${NC}"
        echo "Please install Java 17 manually from: https://adoptium.net/temurin/releases/?version=17"
        exit 1
        ;;
esac

# Verify Java installation
if command_exists java; then
    JAVA_VER=$(java -version 2>&1 | head -n 1)
    echo -e "${GREEN}Java installed successfully:${NC}"
    echo "$JAVA_VER"
    
    # Set JAVA_HOME
    if [[ -d "/usr/lib/jvm/temurin-17-jdk-amd64" ]]; then
        JAVA_HOME="/usr/lib/jvm/temurin-17-jdk-amd64"
    elif [[ -d "/usr/lib/jvm/java-17-openjdk" ]]; then
        JAVA_HOME="/usr/lib/jvm/java-17-openjdk"
    else
        JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    fi
    
    # Add JAVA_HOME to bashrc if not already present
    if ! grep -q "JAVA_HOME=$JAVA_HOME" "$HOME/.bashrc"; then
        echo "export JAVA_HOME=$JAVA_HOME" >> "$HOME/.bashrc"
        echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> "$HOME/.bashrc"
        echo -e "${GREEN}Added JAVA_HOME to .bashrc${NC}"
    fi
    
    echo -e "${GREEN}Java 17 installation completed successfully!${NC}"
    echo -e "${YELLOW}Note: This version is required for Minecraft 1.20.1 mod development${NC}"
else
    echo -e "${RED}Java installation failed${NC}"
    exit 1
fi 