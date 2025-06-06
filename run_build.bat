@echo off
setlocal enabledelayedexpansion

REM Kill all Java processes
echo Stopping all Java processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 5 /nobreak >nul

REM Set local Java path
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot

REM Set optimized memory limits for 8GB RAM system
set GRADLE_OPTS=-Xmx2G -Xms1G -XX:MaxMetaspaceSize=512M -XX:+UseParallelGC -XX:ParallelGCThreads=4
set JAVA_OPTS=-Xmx2G -Xms1G -XX:MaxMetaspaceSize=512M -XX:+UseParallelGC -XX:ParallelGCThreads=4

REM Check if Java exists
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Error: Java not found at %JAVA_HOME%
    echo Please update JAVA_HOME in this script to point to your Java 21 installation
    exit /b 1
)

REM Set PATH
set PATH=%JAVA_HOME%\bin;%PATH%

REM Verify Java version
"%JAVA_HOME%\bin\java" -version
if errorlevel 1 (
    echo Error: Failed to run Java
    exit /b 1
)

REM Clean everything
echo Performing deep clean...
rmdir /s /q ".gradle" 2>nul
rmdir /s /q "gradle" 2>nul
rmdir /s /q "build" 2>nul
del /f /q gradlew >nul 2>&1
del /f /q gradlew.bat >nul 2>&1
timeout /t 2 /nobreak >nul

REM Create fresh gradle directory
mkdir gradle 2>nul
mkdir "gradle\wrapper" 2>nul

REM Download Gradle wrapper jar manually
echo Downloading Gradle wrapper...
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.jar')"
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Failed to download gradle-wrapper.jar
    exit /b 1
)

REM Download Gradle wrapper properties
echo Downloading wrapper properties...
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.properties', 'gradle/wrapper/gradle-wrapper.properties')"
if not exist "gradle\wrapper\gradle-wrapper.properties" (
    echo Failed to download gradle-wrapper.properties
    exit /b 1
)

REM Initialize Gradle wrapper
echo Initializing Gradle wrapper...
call gradle wrapper --gradle-version 8.5 --no-daemon
if errorlevel 1 (
    echo Failed to initialize Gradle wrapper
    exit /b 1
)

REM Verify wrapper exists
if not exist "gradlew.bat" (
    echo Error: Gradle wrapper was not created properly
    exit /b 1
)

REM Run Gradle build with optimized settings
echo Starting build...
call .\gradlew.bat --no-daemon --parallel --max-workers=4 clean build

endlocal
