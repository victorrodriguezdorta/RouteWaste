#!/bin/bash

IMAGE_NAME="kaizten/sheriff"

# Check if the image exists
if docker images --format "{{.Repository}}" | grep -q "^${IMAGE_NAME}$"; then
  echo "Image '${IMAGE_NAME}' found. Deleting..."
  docker rmi "${IMAGE_NAME}" --force
  echo "Image '${IMAGE_NAME}' has been deleted."
fi

# Back-end
docker run -v $(pwd):/data ${IMAGE_NAME} --test JAVA_HEXAGONAL --uri file:/data --component back-end
# Front-end
docker run -v $(pwd):/data ${IMAGE_NAME} --test TYPESCRIPT_HEXAGONAL --uri file:/data --component front-end
# Summary
docker run -v $(pwd):/data ${IMAGE_NAME} --summary
