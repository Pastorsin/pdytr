#!/bin/bash

TIMES=250
BUFFER_SIZES=(150000000 250000000 550000000 750000000 950000000)
FILE="metrics.csv"

start_server() {
	$SERVER_SCRIPT $1
}

start_client() {
	# Wait server and log listen port
	until ss -ta | grep "*:$1"; do
		:
	done

	time=$($CLIENT_SCRIPT localhost $1 $2)

	echo "$2,$time" >> $FILE
}

main() {	
	echo "Size,Time" > $FILE

	for size in ${BUFFER_SIZES[@]}; do

		for (( i = 0; i < $TIMES; i++ )); do
			port=$((30000 + $i))

			start_server $port &
			start_client $port $size &

			wait
		done

	done
}


# Verify args
if [[ $# -ne 1 && ($1 != "-java" || $1 != "-c") ]]; then
	echo "1 arguments needed: [-c -java]"
	echo "Example: ./metrics.sh -java"

	exit 1

elif [[ $1 == "-java" ]]; then
	export SERVER_SCRIPT="java -classpath java Server"
	export CLIENT_SCRIPT="java -classpath java Client"

	javac -classpath java java/Client.java
	javac -classpath java java/Server.java

elif [[ $1 == "-c" ]]; then
	export SERVER_SCRIPT="./c/server.out"
	export CLIENT_SCRIPT="./c/client.out"

	gcc c/client.c -o c/client.out -lssl -lcrypto
	gcc c/server.c -o c/server.out -lssl -lcrypto

fi

main

exit 0
