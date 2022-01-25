#!/usr/bin/env bash
set -euo pipefail
. "${0%/*}"/aws.env

aws --region eu-central-1 ec2 create-security-group \
    --description "$SECURITY_GROUP_NAME" \
    --group-name "$SECURITY_GROUP_NAME" \
    --vpc-id "$DB_VPC_ID" \
    --no-cli-pager

aws --region eu-central-1 ec2 authorize-security-group-ingress \
    --group-name "$SECURITY_GROUP_NAME" \
    --protocol tcp \
    --port 5432 \
    --cidr 0.0.0.0/0 \
    --no-cli-pager
