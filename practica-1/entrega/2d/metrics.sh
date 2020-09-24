#!/bin/bash

TIMES=100
FILE="metrics.csv"

start_server() {
	./server.out $1
}

start_client() {
	# Wait server
	until ss -tln | grep ":$1"; do
		echo "Waiting $1"
	done

	time=$(./client.out localhost $1)

	echo "$time" 1>> $FILE
}

main() {
	gcc client.c -o client.out
	gcc server.c -o server.out

	echo "Time" > $FILE

	port=3000

	for (( i = 0; i < $TIMES; i++ )); do

		port=$(($port + 1))	

		port_available=$(ss -tan | grep ":$port")	

		if [ "$port_available" == "" ]
		then
			start_server $port >&2 | start_client $port
		fi

	done

}

main

exit 0
