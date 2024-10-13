#!/bin/bash

docker stop api-server
docker rm api-server

echo "Pulling the latest bknote71/api-server image..."
docker pull bknote71/api-server

echo "Removing old bknote71/api-server images..."
# Get the image ID of the latest pulled image
latest_api_server_image=$(docker images bknote71/api-server --format "{{.ID}}" | head -n 1)
# List all bknote71/scheduler images and remove the older ones
docker images bknote71/scheduler --format "{{.ID}}" | grep -v "$latest_api_server_image" | xargs -r docker rmi

echo "Run docker!"
docker run --network scheduler_app-network -d -p 8090:8090 --name api-server bknote71/api-server

echo "All tasks completed!"