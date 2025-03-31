#!/bin/bash

if [ $# -ne 3 ]; then
    echo "Użycie: $0 <slave-port> <master-port> <number>"
    exit 1
fi

SLAVE_PORT=$1
MASTER_PORT=$2
NUMBER=$3

echo "[Slave]: Wysyłam liczbę $NUMBER do Mastera na porcie $MASTER_PORT"
echo $NUMBER | nc -u -w1 127.0.0.1 $MASTER_PORT

echo "[Slave]: Oczekuję na potwierdzenie od Mastera..."
ACK=$(nc -lu -w1 $SLAVE_PORT)

if [ "$ACK" == "ACK" ]; then
    echo "[Slave]: Otrzymałem ACK, wysyłam liczbę 0"
    echo 0 | nc -u -w1 127.0.0.1 $MASTER_PORT
    echo "[Slave]: Wysłano liczbę 0"

    echo "[Slave]: Wysyłam liczbę -1"
    echo -1 | nc -u -w1 127.0.0.1 $MASTER_PORT
    echo "[Slave]: Wysłano liczbę -1"
fi
