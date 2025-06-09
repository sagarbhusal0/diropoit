@echo off
echo Checking Java installation...
echo.

echo 1. Checking Java version:
echo ------------------------
java -version
echo.

echo 2. Checking Java compiler version:
echo ------------------------
javac -version
echo.

echo 3. Checking JAVA_HOME:
echo ------------------------
if defined JAVA_HOME (
    echo JAVA_HOME is set to: %JAVA_HOME%
) else (
    echo JAVA_HOME is not set
)
echo.

echo 4. Checking Java in PATH:
echo ------------------------
where java
where javac
echo.

echo 5. Available Java installations:
echo ------------------------
reg query "HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft" /s 2>nul
wmic product where "name like '%%Java%%'" get name,version 2>nul

pause 