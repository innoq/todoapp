#!/usr/bin/env bash
set -euox pipefail

DB_HOST="host.docker.internal"
DB_ROOT_USER_NAME="postgres"
DB_ROOT_USER_PWD="postgres"
DB_APP_USER_NAME="todoapp"
DB_APP_USER_PWD="todoapp"
DB_DATABASE_NAME="todoapp"

docker run \
    -e PGHOST=${DB_HOST} \
    -e PGUSER=${DB_ROOT_USER_NAME} \
    -e PGPASSWORD=${DB_ROOT_USER_PWD} \
    postgres:13.3 psql \
    -c "CREATE ROLE ${DB_APP_USER_NAME} LOGIN CREATEDB PASSWORD '${DB_APP_USER_PWD}'" \
    -c "SELECT usename FROM pg_user WHERE usename = '${DB_APP_USER_NAME}'" \
    -c "SET ROLE ${DB_APP_USER_NAME}" \
    -c "CREATE DATABASE ${DB_DATABASE_NAME} OWNER ${DB_APP_USER_NAME}" \
    -c "SELECT datname FROM pg_database WHERE datname = '${DB_DATABASE_NAME}'" \
    -c "GRANT ALL PRIVILEGES ON DATABASE ${DB_DATABASE_NAME} to ${DB_APP_USER_NAME}"
