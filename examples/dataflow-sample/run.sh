#!/bin/sh -eux

docker run -it --rm -e PROJECT_ID=$PROJECT_ID -e GOOGLE_APPLICATION_CREDENTIALS=/app/key.json -v $(pwd):/app -w /app gradle:4.4-jdk8 gradle run
