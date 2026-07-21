#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

LOG_DIR="./bore-logs"
mkdir -p "$LOG_DIR"

# --- 0. Make sure nothing stale is running ---
pkill -f "bore local" 2>/dev/null || true
sleep 1

# --- 1. Load .env (for REDIS_PASSWORD etc.) ---
if [ -f .env ]; then
  set -a
  source .env
  set +a
fi

echo "Starting bore tunnels..."

bore local 27017 --to bore.pub > "$LOG_DIR/mongo-payment.log" 2>&1 &
bore local 27018 --to bore.pub > "$LOG_DIR/mongo-notification.log" 2>&1 &
bore local 6379  --to bore.pub > "$LOG_DIR/redis.log" 2>&1 &
bore local 9092  --to bore.pub > "$LOG_DIR/kafka.log" 2>&1 &
bore local 8081  --to bore.pub > "$LOG_DIR/kafka-ui.log" 2>&1 &
bore local 5540  --to bore.pub > "$LOG_DIR/redis-insight.log" 2>&1 &

# Give bore a moment to connect and print its assigned port
sleep 3

get_port () {
  grep -o 'bore.pub:[0-9]*' "$1" | head -n1 | cut -d':' -f2
}

MONGO_PAYMENT_PORT=$(get_port "$LOG_DIR/mongo-payment.log")
MONGO_NOTIF_PORT=$(get_port "$LOG_DIR/mongo-notification.log")
REDIS_PORT=$(get_port "$LOG_DIR/redis.log")
KAFKA_PORT=$(get_port "$LOG_DIR/kafka.log")
KAFKA_UI_PORT=$(get_port "$LOG_DIR/kafka-ui.log")
REDIS_INSIGHT_PORT=$(get_port "$LOG_DIR/redis-insight.log")

if [[ -z "$MONGO_PAYMENT_PORT" || -z "$MONGO_NOTIF_PORT" || -z "$REDIS_PORT" || -z "$KAFKA_PORT" || -z "$KAFKA_UI_PORT" || -z "$REDIS_INSIGHT_PORT" ]]; then
  echo "One or more tunnels failed to start. Check logs in $LOG_DIR/"
  exit 1
fi

# --- 2. Patch .env with the live Kafka advertised address, then restart kafka ---
KAFKA_ADDR="bore.pub:${KAFKA_PORT}"

if grep -q '^KAFKA_EXTERNAL_ADVERTISED_ADDR=' .env; then
  sed -i.bak "s|^KAFKA_EXTERNAL_ADVERTISED_ADDR=.*|KAFKA_EXTERNAL_ADVERTISED_ADDR=${KAFKA_ADDR}|" .env
else
  echo "KAFKA_EXTERNAL_ADVERTISED_ADDR=${KAFKA_ADDR}" >> .env
fi

echo "Restarting kafka container with advertised listener = ${KAFKA_ADDR} ..."
docker compose up -d --force-recreate kafka

# --- 3. Save the port map for other scripts / re-use ---
cat > .bore-ports.env <<EOF
MONGO_PAYMENT_PORT=${MONGO_PAYMENT_PORT}
MONGO_NOTIF_PORT=${MONGO_NOTIF_PORT}
REDIS_PORT=${REDIS_PORT}
KAFKA_PORT=${KAFKA_PORT}
KAFKA_UI_PORT=${KAFKA_UI_PORT}
REDIS_INSIGHT_PORT=${REDIS_INSIGHT_PORT}
EOF

# --- 4. Print summary ---
REDIS_PW="${REDIS_PASSWORD:-changeMeStrongPassword}"

cat <<SUMMARY

============================================================
 Tunnels are up. Connection details:
============================================================

mongo-payment:
  mongodb://admin:password@bore.pub:${MONGO_PAYMENT_PORT}/payment?authSource=admin

mongo-notification:
  mongodb://admin:password@bore.pub:${MONGO_NOTIF_PORT}/notification?authSource=admin

redis:
  redis://:${REDIS_PW}@bore.pub:${REDIS_PORT}

kafka (bootstrap server):
  bore.pub:${KAFKA_PORT}

kafka-ui (browser):
  http://bore.pub:${KAFKA_UI_PORT}

redis-insight (browser):
  http://bore.pub:${REDIS_INSIGHT_PORT}

------------------------------------------------------------
Note: bore assigns a NEW random port every time a tunnel
restarts. Re-run this script after any restart and use the
freshly printed values — old ones will stop working.
============================================================
SUMMARY
