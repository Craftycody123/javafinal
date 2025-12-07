#!/bin/bash

echo "Starting Vehicle Rental System..."

# Set the path to MySQL Connector/J JAR file (updated for version 9.4.0)
MYSQL_JAR="mysql-connector-j-9.4.0.jar"

# Check if MySQL JAR exists
if [ ! -f "$MYSQL_JAR" ]; then
    echo "Error: MySQL Connector/J JAR file not found!"
    echo "Please download mysql-connector-j-9.4.0.jar and place it in this directory."
    echo "Download from: https://dev.mysql.com/downloads/connector/j/"
    exit 1
fi

# Check if classes directory exists
if [ ! -d "classes" ]; then
    echo "Error: Classes directory not found!"
    echo "Please run ./compile.sh first to compile the application."
    exit 1
fi

# Run the application
echo "Starting application..."
java -cp "classes:$MYSQL_JAR" frontend.Main
