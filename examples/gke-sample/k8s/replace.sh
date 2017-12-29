#!/bin/sh -eux
sed -i -e"s/\$PROJECT_ID/$PROJECT_ID/g" k8s/deployment.yaml
sed -i -e"s/\$BUILD_ID/$BUILD_ID/g" k8s/deployment.yaml
