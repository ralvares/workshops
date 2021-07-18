#!/bin/sh
. .workshop/settings.sh

export REGISTRY=quay.io
export REGISTRY_USER_ID=ralvares
export IMAGE_NAME=workshops
export IMAGE_VERSION=mesh
export FROM_IMAGE=quay.io/ralvares/workshops:dashboard
