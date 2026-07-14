#!/bin/bash

set -Eeuo pipefail

APP_IMAGE=${1:-}

if [ -z "$APP_IMAGE" ]; then
  echo "APP_IMAGE is required"
  exit 1
fi

export APP_IMAGE

container_is_running() {
  docker ps --format '{{.Names}}' | grep -qx "routee-$1"
}

read_nginx_target() {
  if [ ! -f nginx/service-url.inc ]; then
    return 0
  fi

  awk '
    $1 == "set" && $2 == "$service_url" && ($3 == "app-blue:8080;" || $3 == "app-green:8080;") {
      sub(/:8080;$/, "", $3)
      print $3
      exit
    }
  ' nginx/service-url.inc
}

write_nginx_target() {
  local service=$1

  if ! printf 'set $service_url %s:8080;\n' "$service" > nginx/service-url.inc; then
    return 1
  fi

  chmod 0644 nginx/service-url.inc
}

reload_prometheus() {
  if ! command -v curl >/dev/null 2>&1; then
    echo "Warning: curl not found; skipping Prometheus config reload"
    return 0
  fi

  for i in {1..30}; do
    if curl -fsS http://127.0.0.1:9090/-/ready >/dev/null 2>&1; then
      break
    fi
    if [ "$i" -eq 30 ]; then
      echo "Warning: Prometheus did not become ready; skipping config reload"
      return 0
    fi
    sleep 1
  done

  # Prometheus는 실행 중에 prometheus.yml을 다시 읽지 않는다.
  # 배포로 갱신된 설정을 반영하려면 명시적으로 reload해야 한다.
  if curl -fsS -X POST http://127.0.0.1:9090/-/reload >/dev/null 2>&1; then
    echo "Prometheus configuration reloaded"
  else
    echo "Warning: Prometheus config reload failed"
  fi
}

cleanup_container() {
  local service=$1

  docker compose -f docker-compose.yml stop "$service" >/dev/null 2>&1 || true
  docker compose -f docker-compose.yml rm -f "$service" >/dev/null 2>&1 || true
}

print_app_logs() {
  local service=$1

  docker logs "routee-$service" --tail 50 2>/dev/null || true
}

rollback_traffic() {
  local service=$1

  write_nginx_target "$service" &&
    docker exec routee-nginx nginx -t &&
    docker exec routee-nginx nginx -s reload
}

mkdir -p nginx

if [ ! -f nginx/service-url.inc ]; then
  write_nginx_target "app-blue"
fi

echo "Starting shared infrastructure..."
docker compose -f docker-compose.yml up -d nginx redis dozzle tempo prometheus

# Grafana provisioning(datasource)은 기동 시 1회만 적용된다. bind mount된 파일 내용이
# 바뀌어도 Compose는 컨테이너를 재생성하지 않으므로 강제로 재생성한다.
# Grafana는 데이터 볼륨이 없어 잃을 상태가 없다.
docker compose -f docker-compose.yml up -d --force-recreate grafana

reload_prometheus

echo "Waiting for Redis to be ready..."
for i in {1..30}; do
  if docker exec routee-redis redis-cli ping 2>/dev/null | grep PONG >/dev/null; then
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

ROUTED_TARGET=$(read_nginx_target)

if [ -n "$ROUTED_TARGET" ] && container_is_running "$ROUTED_TARGET"; then
  CURRENT=$ROUTED_TARGET
elif container_is_running "app-blue"; then
  CURRENT="app-blue"
elif container_is_running "app-green"; then
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

docker compose -f docker-compose.yml pull "$TARGET"
docker compose -f docker-compose.yml up -d --no-deps "$TARGET"

echo "Step 1: Waiting for $TARGET container healthcheck..."
for i in {1..40}; do
  STATUS=$(docker inspect --format='{{.State.Health.Status}}' "routee-$TARGET" 2>/dev/null || echo "unknown")
  if [ "$STATUS" = "healthy" ]; then
    echo "$TARGET container is healthy"
    break
  fi
  if [ "$STATUS" = "unhealthy" ]; then
    echo "$TARGET container is unhealthy"
    print_app_logs "$TARGET"
    cleanup_container "$TARGET"
    exit 1
  fi
  if [ "$i" -eq 40 ]; then
    echo "$TARGET container healthcheck failed (status: $STATUS)"
    print_app_logs "$TARGET"
    cleanup_container "$TARGET"
    exit 1
  fi
  sleep 3
done

echo "Step 2: Verifying $TARGET is reachable from nginx..."
for i in {1..10}; do
  if docker exec routee-nginx wget -qO- "http://$TARGET:8080/actuator/health" 2>/dev/null | grep '"status":"UP"' >/dev/null; then
    echo "$TARGET is reachable from nginx"
    break
  fi
  if [ "$i" -eq 10 ]; then
    echo "$TARGET not reachable from nginx"
    print_app_logs "$TARGET"
    cleanup_container "$TARGET"
    exit 1
  fi
  sleep 2
done

PREVIOUS_TARGET=$ROUTED_TARGET
if [ -z "$PREVIOUS_TARGET" ]; then
  PREVIOUS_TARGET=${CURRENT:-$TARGET}
fi

echo "Step 3: Switching traffic to $TARGET..."
if ! write_nginx_target "$TARGET"; then
  echo "Failed to write Nginx upstream configuration"
  cleanup_container "$TARGET"
  exit 1
fi

if ! docker exec routee-nginx nginx -t; then
  echo "Nginx configuration validation failed"
  if rollback_traffic "$PREVIOUS_TARGET"; then
    cleanup_container "$TARGET"
  else
    echo "Failed to restore Nginx configuration; keeping $TARGET container for recovery"
  fi
  exit 1
fi

if ! docker exec routee-nginx nginx -s reload; then
  echo "Nginx reload failed"
  if rollback_traffic "$PREVIOUS_TARGET"; then
    cleanup_container "$TARGET"
  else
    echo "Failed to restore Nginx configuration; keeping $TARGET container for recovery"
  fi
  exit 1
fi
echo "Traffic switched to $TARGET"

echo "Step 4: Removing old container ($OLD)..."
docker compose -f docker-compose.yml stop "$OLD" || true
docker compose -f docker-compose.yml rm -f "$OLD" || true

docker image prune -f

echo "$APP_IMAGE" > .last-deployed-tag

echo "Deploy completed. Active app: $TARGET"
