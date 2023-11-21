#!/bin/bash

# Kill all java processes
killall java

# Find the line containing "appUrl" in the application.conf file
LINE=$(grep "appUrl" ./conf/application.conf)

# Extract the value of appUrl using cut and command substitution
LOCAL_HOST_URL=$(echo "$LINE" | cut -d' ' -f3 | tr -d '"')

sbt run &
sm2 --start API_DOCUMENTATION_FRONTEND_STUBMODE
sleep 20

# DIRECTORY path
DIRECTORY="./resources/public/api/conf"

# Loop through the folders with the specified names
for FOLDER in "$DIRECTORY"/*; do
  if [[ -d "$FOLDER" ]]; then
    FOLDER_NAME=$(basename "$FOLDER")
    if [[ $FOLDER_NAME =~ ^[0-9]+\.[0-9]+$ ]]; then
      echo "Opening version: $FOLDER_NAME"

      URL="http://localhost:9680/api-documentation/docs/openapi/preview/action?url=$LOCAL_HOST_URL/api/conf/$FOLDER_NAME/application.yaml"
      python3 -m webbrowser $URL

    fi
  fi
done
