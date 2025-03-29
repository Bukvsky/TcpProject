#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <port> <number>"
    exit 1
fi

PORT=$1
NUMBER=$2

if ! [[ "$PORT" =~ ^[0-9]+$ ]] || ! [[ "$NUMBER" =~ ^-?[0-9]+$ ]]; then
    echo "Error: Both <port> and <number> must be integers."
    exit 1
fi

MAIN_CLASS="DAS"
JAVA_FILE="${MAIN_CLASS}.java"

if [ ! -f "$JAVA_FILE" ]; then
    echo "Error: File '$JAVA_FILE' not found."
    exit 1
fi

echo "Compiling $JAVA_FILE..."
javac "$JAVA_FILE"
if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check your Java code."
    exit 1
fi


echo "Running $MAIN_CLASS..."
java "$MAIN_CLASS" "$PORT" "$NUMBER" &
MASTER_PID=$!


sleep 2

for i in {1..10}
do
    RANDOM_NUMBER=$((RANDOM % 99 + 1)) # Generowanie losowej liczby (1-99)
    echo "Sending random number $RANDOM_NUMBER to port $PORT"
    java "$MAIN_CLASS" "$PORT" "$RANDOM_NUMBER"
    sleep 1
done

echo "Sending number 0 to port $PORT"
java "$MAIN_CLASS" "$PORT" "0"


sleep 2
echo "Sending number -1 to port $PORT"
java "$MAIN_CLASS" "$PORT" "-1"  #


wait $MASTER_PID

EXIT_CODE=$?


if [ $EXIT_CODE -eq 0 ]; then
    echo "DAS application exited successfully."
else
    echo "DAS application exited with error code: $EXIT_CODE."
fi
