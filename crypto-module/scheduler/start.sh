#!/bin/bash

# 1. Bring down Docker Compose
echo "1. Bringing down Docker Compose..."
docker compose down

# 2. Pull the latest bknote71/scheduler image
echo "2. Pulling the latest bknote71/scheduler image..."
docker pull bknote71/scheduler

# 3. Remove all other bknote71/scheduler images except the latest one
echo "3. Removing old bknote71/scheduler images..."
# Get the image ID of the latest pulled image
latest_scheduler_image=$(docker images bknote71/scheduler --format "{{.ID}}" | head -n 1)
# List all bknote71/scheduler images and remove the older ones
docker images bknote71/scheduler --format "{{.ID}}" | grep -v "$latest_scheduler_image" | xargs -r docker rmi

# 4. Bring up Docker Compose
echo "4. Bringing up Docker Compose..."
docker compose up -d

echo "All tasks completed!"