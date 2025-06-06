@echo off
setlocal enabledelayedexpansion

REM Kill all Java processes
echo Stopping all Java processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 5 /nobreak >nul

REM Set local Java path
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot

REM Set higher memory limits for Gradle and disable daemon
set GRADLE_OPTS=-Xmx8G -Xms4G -XX:MaxMetaspaceSize=2G -XX:+HeapDumpOnOutOfMemoryError -Dorg.gradle.daemon=false
set JAVA_OPTS=-Xmx8G -Xms4G -XX:MaxMetaspaceSize=2G -XX:+HeapDumpOnOutOfMemoryError

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
rmdir /s /q ".fabric" 2>nul
del /f /q gradlew >nul 2>&1
del /f /q gradlew.bat >nul 2>&1
timeout /t 2 /nobreak >nul

REM Create fresh directories
echo Creating fresh directories...
mkdir gradle 2>nul
mkdir "gradle\wrapper" 2>nul
mkdir ".fabric" 2>nul

REM Download Gradle wrapper files
echo Downloading Gradle wrapper files...
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.jar')"
powershell -Command "(New-Object Net.WebClient).DownloadFile('https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.properties', 'gradle/wrapper/gradle-wrapper.properties')"

REM Verify downloads
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Failed to download gradle-wrapper.jar
    exit /b 1
)
if not exist "gradle\wrapper\gradle-wrapper.properties" (
    echo Failed to download gradle-wrapper.properties
    exit /b 1
)

REM Initialize Gradle wrapper with specific version
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

REM Run initial setup tasks
echo Running initial setup...
call .\gradlew.bat --no-daemon --console=plain --info clean
if errorlevel 1 goto error

echo Running Fabric setup...
call .\gradlew.bat --no-daemon --console=plain --info setupDecompWorkspace
if errorlevel 1 goto error

echo Running configuration...
call .\gradlew.bat --no-daemon --console=plain --info tasks
if errorlevel 1 goto error

echo Configuration completed successfully!
goto end

:error
echo Configuration failed! Check the error messages above.
exit /b 1

:end
endlocal 