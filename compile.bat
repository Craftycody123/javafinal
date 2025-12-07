
k@echo off
echo Compiling Vehicle Rental System...

REM Set the path to MySQL Connector/J JAR file
REM Update this path to match your MySQL Connector/J location
set MYSQL_JAR=mysql-connector-j-9.4.0.jar

REM Check if MySQL JAR exists
if not exist "%MYSQL_JAR%" (
    echo Error: MySQL Connector/J JAR file not found!
    echo Please download mysql-connector-j-9.4.0.jar and place it in this directory.
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    pause
    exit /b 1
)

REM Create classes directory if it doesn't exist
if not exist "classes" mkdir classes

REM Compile all backend Java files
echo Compiling backend classes...
javac -cp "%MYSQL_JAR%" -d classes backend/db/*.java backend/models/*.java backend/dao/*.java backend/util/*.java

REM Compile all frontend Java files
echo Compiling frontend classes...
javac -cp "classes;%MYSQL_JAR%" -d classes frontend/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo To run the application, use: run.bat
) else (
    echo Compilation failed!
    pause
    exit /b 1
)

pause
