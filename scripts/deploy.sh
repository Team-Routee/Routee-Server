#!/bin/bash

set -e

APP_IMAGE=$1

if [ -z "$APP_IMAGE" ]; then
  echo "APP_IMAGE is required"
  exit 1
fi

export APP_IMAGE=$APP_IMAGE

if ! docker ps --format '{{.Names}}' | grep -q "^routee-nginx$"; then
  echo "Initial setup: starting nginx, redis, dozzle..."
  docker compose -f docker-compose.prod.yml up -d nginx redis dozzle
  sleep 5
fi

CURRENT=$(grep -o "app-[a-z]*" nginx/service-url.inc 2>/dev/null || echo "")

if [ "$CURRENT" = "app-blue" ]; then
  TARGET="app-green"
  OLD="app-blue"
else
  TARGET="app-blue"
  OLD="app-green"
fi

echo "Current app: $CURRENT"
echo "Deploy target: $TARGET"

docker compose -f docker-compose.prod.yml pull $TARGET
docker compose -f docker-compose.prod.yml up -d --no-deps $TARGET

echo "Waiting for $TARGET health check..."

for i in {1..20}; do
  if docker exec routee-nginx wget -qO- http://$TARGET:8080/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
    echo "$TARGET is healthy"
    break
  fi

  if [ "$i" -eq 20 ]; then
    echo "$TARGET health check failed"
    docker logs routee-$TARGET
    exit 1
  fi

  sleep 3
done

echo "set \$service_url $TARGET:8080;" > nginx/service-url.inc

docker exec routee-nginx nginx -s reload

docker compose -f docker-compose.prod.yml stop $OLD || true

echo "Deploy completed. Active app: $TARGET"
