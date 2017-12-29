#!/bin/sh -u

gcloud beta container \
  --project ${PROJECT_ID} \
  clusters create "pubsub-cluster" \
  --zone "us-central1-a" \
  --username="admin" \
  --cluster-version "1.8.4-gke.1" \
  --machine-type "g1-small" \
  --image-type "COS" \
  --disk-size "10" \
  --scopes "https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.read_only","https://www.googleapis.com/auth/logging.write","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management.readonly","https://www.googleapis.com/auth/trace.append" \
  --preemptible \
  --num-nodes "1" \
  --network "default" \
  --enable-cloud-logging \
  --enable-cloud-monitoring \
  --subnetwork "default"
