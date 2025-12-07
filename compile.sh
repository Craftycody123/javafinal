#!/bin/bash

echo "Compiling Vehicle Rental System..."

# Set the path to MySQL Connector/J JAR file (updated for version 9.4.0)
MYSQL_JAR="mysql-connector-j-9.4.0.jar"

# Check if MySQL JAR exists
if [ ! -f "$MYSQL_JAR" ]; then
    echo "Error: MySQL Connector/J JAR file not found!"
    echo "Please download mysql-connector-j-9.4.0.jar and place it in this directory."
    echo "Download from: https://dev.mysql.com/downloads/connector/j/"
    exit 1
fi

# Create classes directory if it doesn't exist
mkdir -p classes

# Compile all Java files
echo "Compiling backend classes..."
javac -cp "$MYSQL_JAR" -d classes backend/db/*.java backend/models/*.java backend/dao/*.java backend/util/*.java

echo "Compiling frontend classes..."
javac -cp "classes:$MYSQL_JAR" -d classes frontend/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "To run the application, use: ./run.sh"
else
    echo "Compilation failed!"
    exit 1
fi
