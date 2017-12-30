#!/bin/sh

docker exec -it gkesample_app_1 python /app/main.py publish "$1"
