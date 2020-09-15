#!/bin/bash

TIMES=100
BUFFER_SIZES=(1000000 5000000 10000000 50000000 100000000 500000000 900000000)
FILE="metrics.csv"

start_server() {
	$SERVER_SCRIPT $1
}

start_client() {
	# Wait server
	until ss -tln | grep ":$1"; do
		echo "Waiting $1"
	done

	time=$($CLIENT_SCRIPT localhost $1 $2)

	echo "$2,$time" 1>> $FILE
}

main() {	
	echo "Size,Time" > $FILE

	port=3000

	for size in ${BUFFER_SIZES[@]}; do

		if [[ $FLAG_C -eq 1 ]]; then
			gcc c/client.c -o c/client.out -lssl -lcrypto -D BUFFER_SIZE=$size
			gcc c/server.c -o c/server.out -lssl -lcrypto -D BUFFER_SIZE=$size
		fi

		for (( i = 0; i < $TIMES; i++ )); do

			port=$(($port + 1))	

			port_available=$(ss -tan | grep ":$port")	

			if [ "$port_available" == "" ]
			then
				start_server $port >&2 | start_client $port $size
			fi

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
	export FLAG_C=0

	javac -classpath java java/Client.java
	javac -classpath java java/Server.java

elif [[ $1 == "-c" ]]; then
	export SERVER_SCRIPT="./c/server.out"
	export CLIENT_SCRIPT="./c/client.out"
	export FLAG_C=1
fi

main

exit 0
