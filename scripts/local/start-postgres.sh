#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )/../.." &> /dev/null && pwd )"

docker run \
  --name "todoapp-db" \
  -e POSTGRES_USER="postgres" \
  -e POSTGRES_PASSWORD="postgres" \
  -p 5432:5432 \
  --rm \
  -v "${ROOT_DIR}/temp/pgdata":/var/lib/postgresql/data \
  postgres:13.3