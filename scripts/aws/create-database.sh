#!/usr/bin/env bash
set -euo pipefail
. "${0%/*}"/aws.env

SECURITY_GROUP_ID=$(aws --region eu-central-1 ec2 describe-security-groups \
    --group-names "$SECURITY_GROUP_NAME" \
    --no-cli-pager | jq -r '.SecurityGroups[0].GroupId')

read -s -p "Root User PW: " DB_ROOT_USER_PWD

aws --region eu-central-1 rds create-db-instance \
    --db-instance-identifier "$DB_INSTANCE_NAME" \
    --db-instance-class "$DB_INSTANCE_CLASS" \
    --allocated-storage "$DB_STORAGE" \
    --engine "$DB_ENGINE" \
    --engine-version "$DB_ENGINE_VERSION" \
    --master-username "$DB_ROOT_USER_NAME" \
    --master-user-password "$DB_ROOT_USER_PWD" \
    --vpc-security-group-ids "$SECURITY_GROUP_ID" \
    --storage-encrypted \
    --tags "Key=servicedef,Value=$DB_INSTANCE_NAME" \
    --no-cli-pager

