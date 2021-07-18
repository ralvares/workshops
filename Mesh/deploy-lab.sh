#!/bin/sh

. ./image-env.sh

PROJECT_NAME=mesh
WORKSHOP_IMAGE=quay.io/ralvares/workshops:mesh

SUBDOMAIN=$(oc get ingresses.config.openshift.io cluster -o jsonpath='{.spec.domain}')

oc new-project ${PROJECT_NAME}

oc new-app -n ${PROJECT_NAME} https://raw.githubusercontent.com/ralvares/workshop-spawner/develop/templates/hosted-workshop-production.json \
 --param CLUSTER_SUBDOMAIN="${SUBDOMAIN}" \
 --param SPAWNER_NAMESPACE="${PROJECT_NAME}" \
 --param WORKSHOP_NAME="${PROJECT_NAME}" \
 --param WORKSHOP_IMAGE="${WORKSHOP_IMAGE}" \
 --param OC_VERSION="${OC_VERSION}"
