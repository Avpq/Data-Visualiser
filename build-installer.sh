#!/bin/bash
echo "Building CSV Visualizer installer for Mac..."

# Step 1: Build JAR
mvn clean package

# Step 2: Create installer
jpackage \
  --input target \
  --name "CSV Visualizer" \
  --main-jar visualizer-1.0-SNAPSHOT.jar \
  --main-class com.avez.visualizer.GUIWrapper \
  --type dmg \
  --app-version 1.0.0 \
  --icon '/Users/avisahai/datas/Projects/Data Visualizer/aa Build Materials/Data Visualizer Icon.icns' \
  --vendor "Avez" \
  --description "CSV Data Visualization Tool" \
  --mac-package-name "CSV Visualizer by avez"

echo "Done! Installer created: CSV Visualizer-1.0.0.dmg"
