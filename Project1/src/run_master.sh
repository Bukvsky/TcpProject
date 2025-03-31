#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Użycie: $0 <master-port> <number>"
    exit 1
fi

MASTER_PORT=$1
NUMBER=$2

echo "[Master]: Uruchamiam tryb Master na porcie $MASTER_PORT z liczbą $NUMBER"

nc -lu $MASTER_PORT &
MASTER_PID=$!

while :; do
    MESSAGE=$(nc -lu $MASTER_PORT)

    if [ "$MESSAGE" == "0" ]; then
        echo "[Master]: Otrzymałem liczbę 0, kończę komunikację z Slave."
        break
    fi

    echo "[Master]: Otrzymałem liczbę $MESSAGE od Slave."

    echo "[Master]: Wysyłam ACK"
    echo "ACK" | nc -u -w1 $SLAVE_IP $SLAVE_PORT
done

kill $MASTER_PID
