cd @echo off
echo Starting Vehicle Rental System...

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

REM Check if classes directory exists
if not exist "classes" (
    echo Error: Classes directory not found!
    echo Please run compile.bat first to compile the application.
    pause
    exit /b 1
)

REM Run the application
echo Starting application...
java -cp "classes;%MYSQL_JAR%" frontend.Main

pause
