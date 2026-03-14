#!/bin/bash

IMAGE_NAME="kaizten/sheriff:latest"

if ! docker image inspect "$IMAGE_NAME" &>/dev/null; then
  echo -e "🔄 Local Docker image '${IMAGE_NAME}' not found. Pulling from Docker Hub..."
  if ! docker pull "$IMAGE_NAME"; then
    echo -e "🛑 Failed to pull image '${IMAGE_NAME}'. Exiting."
    exit 1
  fi
  echo -e "✅ Docker image '${IMAGE_NAME}' has been downloaded."
  docker run -v $(pwd):/data ${IMAGE_NAME} --reset
else
  echo -e "🔍 Checking for updates to Docker image '${IMAGE_NAME}'..."
  LOCAL_DIGEST=$(docker image inspect "$IMAGE_NAME" --format '{{index .RepoDigests 0}}' 2>/dev/null | cut -d@ -f2)
  REMOTE_DIGEST=$(docker buildx imagetools inspect "$IMAGE_NAME" | grep -oP 'Digest:\s+\Ksha256:[a-f0-9]{64}')
  echo "Local Docker digest:  $LOCAL_DIGEST"
  echo "Remote Docker digest: $REMOTE_DIGEST"
  if [[ "$LOCAL_DIGEST" != "$REMOTE_DIGEST" ]]; then
    echo -e "⚠️  Local Docker image is outdated. It must be updated to ensure you have the latest features and fixes."
    docker rmi "${IMAGE_NAME}" --force
    echo -e "✅ Local Docker image '${IMAGE_NAME}' has been deleted."
    echo -e "🔄 Pulling Docker image '${IMAGE_NAME}' from Docker Hub..."
    if ! docker pull "$IMAGE_NAME"; then
      echo -e "🛑 Failed to pull image '${IMAGE_NAME}'. Exiting."
      exit 1
    fi
    echo -e "✅ Docker image '${IMAGE_NAME}' has been updated."
    docker run -v $(pwd):/data ${IMAGE_NAME} --reset
  fi
fi

# Back-end
# docker run -v $(pwd):/data ${IMAGE_NAME} --test JAVA_HEXAGONAL --uri file:/data --component back-end
# Front-end
docker run -v $(pwd):/data ${IMAGE_NAME} --test TYPESCRIPT_HEXAGONAL --uri file:/data --component front-end
# Summary
docker run -v $(pwd):/data ${IMAGE_NAME} --summary
