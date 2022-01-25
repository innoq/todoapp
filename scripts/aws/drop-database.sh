#!/usr/bin/env bash
set -euo pipefail
. "${0%/*}"/aws.env

aws --region eu-central-1 rds delete-db-instance \
    --db-instance-identifier "$DB_INSTANCE_NAME" \
    --skip-final-snapshot \
    --no-cli-pager
