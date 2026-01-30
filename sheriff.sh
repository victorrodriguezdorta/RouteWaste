#!/bin/bash

IMAGE_NAME="kaizten/sheriff"

if ! docker image inspect "$IMAGE_NAME" &>/dev/null; then
  echo -e "\xE2\x9D\x8C Image '${IMAGE_NAME}' not found locally."
  echo -e "\xF0\x9F\x9A\x80 Pulling image '${IMAGE_NAME}' from Docker Hub..."
  if ! docker pull "$IMAGE_NAME"; then
    echo -e "\xF0\x9F\x9A\x80 Failed to pull image '${IMAGE_NAME}'. Exiting."
    exit 1
  fi
fi

LOCAL_DIGEST=$(docker image inspect "$IMAGE_NAME" --format '{{index .RepoDigests 0}}' 2>/dev/null | cut -d@ -f2)
if [ -z "$LOCAL_DIGEST" ]; then
  LOCAL_DIGEST=$(docker image inspect "$IMAGE_NAME" --format '{{.Id}}')
fi

REMOTE_DIGEST=$(docker manifest inspect --verbose "$IMAGE_NAME" \
  | sed -n 's/.*"digest"[[:space:]]*:[[:space:]]*"\(sha256:[^"]*\)".*/\1/p' \
  | head -n1)

if [ -n "$REMOTE_DIGEST" ]; then
  if [ "$LOCAL_DIGEST" != "$REMOTE_DIGEST" ]; then
    echo -e "\xE2\x9C\x85 Remote changed! (different digest)"
    docker rmi "${IMAGE_NAME}" --force
    echo -e "\xE2\x9D\x8C Image '${IMAGE_NAME}' has been deleted."
    echo -e "\xF0\x9F\x9A\x80 Pulling image '${IMAGE_NAME}' from Docker Hub..."
    if ! docker pull "$IMAGE_NAME"; then
      echo -e "\xF0\x9F\x9A\x80 Failed to pull image '${IMAGE_NAME}'. Exiting."
      exit 1
    fi
  fi
fi

# Back-end
docker run -v $(pwd):/data ${IMAGE_NAME} --test JAVA_HEXAGONAL --uri file:/data --component back-end
# Front-end
docker run -v $(pwd):/data ${IMAGE_NAME} --test TYPESCRIPT_HEXAGONAL --uri file:/data --component front-end
# Summary
docker run -v $(pwd):/data ${IMAGE_NAME} --summary
