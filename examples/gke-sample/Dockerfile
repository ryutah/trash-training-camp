FROM python:alpine3.7

ARG BUILD_DEPS="linux-headers alpine-sdk"

RUN apk add --no-cache ${BUILD_DEPS} \
 && rm -rf /tmp/*

COPY . /app

RUN pip install -r app/requirements.txt

CMD ["python", "/app/main.py"]
