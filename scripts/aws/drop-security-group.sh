#!/usr/bin/env bash
set -euo pipefail
. "${0%/*}"/aws.env

aws --region eu-central-1 ec2 delete-security-group \
    --group-name "$SECURITY_GROUP_NAME"
