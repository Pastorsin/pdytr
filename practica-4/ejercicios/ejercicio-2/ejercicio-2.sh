#!/bin/bash
javac -cp bin/jade.jar:src -d classes src/AgenteMovil.java

java -cp bin/jade.jar jade.Boot -gui &
SERVER_PID=$!

until [ $(netstat -tulpn | grep "1099" | wc -l) -ge 1 ]; do
      echo "INFO - Esperando que inicie el servidor"
      sleep 1
done

java -cp bin/jade.jar:classes jade.Boot -gui -container -host localhost -agents mol:AgenteMovil'(Main-Container, database/numeros.csv)' &
CLIENT_PID=$!

#Espera unos segundos hasta que finalice la ejecución.
sleep 1

kill -9 $CLIENT_PID
kill -9 $SERVER_PID