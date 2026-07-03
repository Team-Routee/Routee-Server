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
  mkdir -p nginx
  if [ ! -f nginx/service-url.inc ]; then
    echo "set \$service_url app-blue:8080;" > nginx/service-url.inc
  fi
  docker compose -f docker-compose.yml up -d nginx redis dozzle

  echo "Waiting for Redis to be ready..."
  for i in {1..30}; do
    if docker exec routee-redis redis-cli ping 2>/dev/null | grep -q PONG; then
      echo "Redis is ready"
      break
    fi
    if [ "$i" -eq 30 ]; then
      echo "Redis healthcheck failed"
      docker logs routee-redis --tail 50
      exit 1
    fi
    sleep 1
  done
fi

if docker ps --format '{{.Names}}' | grep -q "^routee-app-blue$"; then
  CURRENT="app-blue"
elif docker ps --format '{{.Names}}' | grep -q "^routee-app-green$"; then
  CURRENT="app-green"
else
  CURRENT=""
fi

if [ "$CURRENT" = "app-blue" ]; then
  TARGET="app-green"
  OLD="app-blue"
else
  TARGET="app-blue"
  OLD="app-green"
fi

echo "Current app: $CURRENT"
echo "Deploy target: $TARGET"

docker compose -f docker-compose.yml pull $TARGET
docker compose -f docker-compose.yml up -d --no-deps $TARGET

echo "Step 1: Waiting for $TARGET container healthcheck..."
for i in {1..40}; do
  STATUS=$(docker inspect --format='{{.State.Health.Status}}' routee-$TARGET 2>/dev/null || echo "unknown")
  if [ "$STATUS" = "healthy" ]; then
    echo "$TARGET container is healthy"
    break
  fi
  if [ "$STATUS" = "unhealthy" ]; then
    echo "$TARGET container is unhealthy"
    docker logs routee-$TARGET --tail 50
    exit 1
  fi
  if [ "$i" -eq 40 ]; then
    echo "$TARGET container healthcheck failed (status: $STATUS)"
    docker logs routee-$TARGET --tail 50
    exit 1
  fi
  sleep 3
done

echo "Step 2: Verifying $TARGET is reachable from nginx..."
for i in {1..10}; do
  if docker exec routee-nginx wget -qO- http://$TARGET:8080/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
    echo "$TARGET is reachable from nginx"
    break
  fi
  if [ "$i" -eq 10 ]; then
    echo "$TARGET not reachable from nginx"
    docker logs routee-$TARGET --tail 50
    exit 1
  fi
  sleep 2
done

echo "Step 3: Switching traffic to $TARGET..."
echo "set \$service_url $TARGET:8080;" > nginx/service-url.inc
docker exec routee-nginx nginx -s reload
echo "Traffic switched to $TARGET"

echo "Step 4: Removing old container ($OLD)..."
docker compose -f docker-compose.yml stop $OLD || true
docker compose -f docker-compose.yml rm -f $OLD || true

docker image prune -f

echo "$APP_IMAGE" > .last-deployed-tag

echo "Deploy completed. Active app: $TARGET"
