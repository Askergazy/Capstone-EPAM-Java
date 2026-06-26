#!/bin/bash

# Change to script directory
cd "$(dirname "$0")"

echo "================================================"
echo "Starting Spring Shop Application"
echo "================================================"
echo ""
echo "Current directory: $(pwd)"
echo ""
echo "Building project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo ""
    echo "Build failed! Exiting..."
    exit 1
fi

echo ""
echo "Starting Tomcat server on http://localhost:8080"
echo "Press Ctrl+C to stop"
echo ""
mvn cargo:run
