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

# --cpus="2" --memory="2g"
echo "Run docker!"
docker run -d --network scheduler_app-network -p 8090:8090 \
        --name api-server bknote71/api-server
#      --ulimit nofile=1048576:1048576 \
#      --sysctl net.core.somaxconn=4096 \
#      --sysctl net.ipv4.tcp_max_syn_backlog=4096 \


echo "All tasks completed!"