#!/usr/bin/env bash

echo "Waiting for startup.."
until curl http://mongo0:28017/serverStatus\?text\=1 2>&1 | grep uptime | head -1; do
  printf '.'
  sleep 1
done

echo curl http://mongo0:28017/serverStatus\?text\=1 2>&1 | grep uptime | head -1
echo "Started.."


mongo --host mongo0:27017 <<EOF
    config = {"_id":"rs0","members":[{"_id":0,"host":"mongo0:27017"},{"_id":1,"host":"mongo1:27017"},{"_id":2,"host":"mongo2:27017"}]};
    rs.initiate(config);
EOF
