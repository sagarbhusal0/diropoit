@echo off
setlocal enabledelayedexpansion

REM Kill all Java processes
echo Stopping all Java processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 5 /nobreak >nul

REM Check for Java 17 in common installation paths
set "JAVA_PATHS=C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot;C:\Program Files\Java\jdk-17;C:\Program Files\Eclipse Adoptium\jdk-17;C:\Program Files\BellSoft\LibericaJDK-17"

echo Checking for Java 17 installation...
set "JAVA_FOUND=false"
for %%p in (%JAVA_PATHS%) do (
    if exist "%%p\bin\java.exe" (
        set "JAVA_HOME=%%p"
        set "JAVA_FOUND=true"
        goto :found_java
    )
)

:found_java
if "%JAVA_FOUND%"=="false" (
    echo Java 17 not found in common paths.
    echo Please download and install Java 17 from:
    echo https://adoptium.net/temurin/releases/?version=17
    echo.
    echo After installation, update JAVA_HOME in this script to point to your Java 17 installation
    pause
    exit /b 1
)

REM Set optimized memory limits for 8GB RAM system
set GRADLE_OPTS=-Xmx2G -Xms1G -XX:MaxMetaspaceSize=512M -XX:+UseParallelGC -XX:ParallelGCThreads=4
set JAVA_OPTS=-Xmx2G -Xms1G -XX:MaxMetaspaceSize=512M -XX:+UseParallelGC -XX:ParallelGCThreads=4

REM Verify Java version
echo Checking Java version...
"%JAVA_HOME%\bin\java" -version 2>&1 | findstr /i "version"
if errorlevel 1 (
    echo Error: Failed to run Java
    pause
    exit /b 1
)

REM Check if it's Java 17
"%JAVA_HOME%\bin\java" -version 2>&1 | findstr /i "version" | findstr /i "17" >nul
if errorlevel 1 (
    echo Error: Java 17 is required for this project
    echo Current Java version:
    "%JAVA_HOME%\bin\java" -version
    pause
    exit /b 1
)

REM Set PATH
set PATH=%JAVA_HOME%\bin;%PATH%

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
    pause
    exit /b 1
)

REM Download Gradle wrapper properties
echo Downloading wrapper properties...
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.properties', 'gradle/wrapper/gradle-wrapper.properties')"
if not exist "gradle\wrapper\gradle-wrapper.properties" (
    echo Failed to download gradle-wrapper.properties
    pause
    exit /b 1
)

REM Initialize Gradle wrapper
echo Initializing Gradle wrapper...
call gradle wrapper --gradle-version 8.5 --no-daemon
if errorlevel 1 (
    echo Failed to initialize Gradle wrapper
    pause
    exit /b 1
)

REM Verify wrapper exists
if not exist "gradlew.bat" (
    echo Error: Gradle wrapper was not created properly
    pause
    exit /b 1
)

REM Run Gradle build with optimized settings
echo Starting build...
call .\gradlew.bat --no-daemon --parallel --max-workers=4 clean build

if errorlevel 1 (
    echo Build failed! Please check the error messages above.
    pause
    exit /b 1
) else (
    echo Build completed successfully!
)

pause
endlocal
