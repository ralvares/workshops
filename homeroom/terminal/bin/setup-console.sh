#!/bin/bash

set -x

set -eo pipefail

# Setup environment, including login.

. /usr/local/bin/setup-environ.sh

# Copy console script to shared directory.

if [ -d $TOKEN_DIRECTORY ]; then
    cp /usr/local/bin/start-console.sh $TOKEN_DIRECTORY
fi
