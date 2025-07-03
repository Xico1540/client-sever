#!/bin/bash

DB_HOST="$PGHOST"
DB_USER="$PGUSER"
DB_PASSWORD="$PGPASSWORD"
DB_NAME="$PGDATABASE"

sql_query="SELECT CASE WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'hfj_resource') THEN 'OK' ELSE 'Error' END AS result;"

result=$(psql -h "$DB_HOST" -U "$DB_USER" -d "$DB_NAME" -c "$sql_query" -tA)

if [ "$result" = "OK" ]; then
  exit 0
else
  echo "Healthcheck error: $result"
  exit 1
fi

echo "pg-restorer-healthcheck.sh: $result"
