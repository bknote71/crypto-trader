#!/bin/zsh
set -e

# 입력 인자가 "--no-cache"일 경우 NO_CACHE 변수에 해당 옵션 설정
NO_CACHE=""
if [ -n "$1" ] && [ "$1" = "--no-cache" ]; then
  NO_CACHE="--no-cache"
  echo "Building without cache..."
else
  echo "Building with cache..."
fi

echo "Cleaning and building the api-server module..."
./gradlew clean :api-server:build

echo "Navigating to the api-server directory..."
cd api-server

echo "Building and pushing the Docker image..."
#docker buildx build --platform linux/amd64 -t bknote71/api-server:latest $NO_CACHE --push .

docker build -t bknote71/api-server:latest $NO_CACHE .
docker push bknote71/api-server:latest

echo "Api-server Docker image has been built, tagged, and pushed successfully!"
