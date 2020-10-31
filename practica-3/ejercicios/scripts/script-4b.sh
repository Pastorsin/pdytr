#!/bin/bash

OUTPUT="../outputs/ejercicio-4b-test.csv"
TIMES=20

ARCHIVO_SERVER="../ejercicio-4/database/server/experimento.txt"
ARCHIVO_CLIENTE1="../ejercicio-4/database/client/experimento1.txt"
ARCHIVO_CLIENTE2="../ejercicio-4/database/client/experimento2.txt"

# Se limpia el output
echo "Resultado" > $OUTPUT

# Se posiciona en el directorio del ejercicio
cd "/pdytr/practica-3/ejercicios/ejercicio-4"

# Ejecuta el servidor
mvn -DskipTests package exec:java -Dexec.mainClass=pdytr.example.grpc.App &
SERVER_PID=$!

# Espera a que el objeto remoto sea iniciado
until [ $(netstat -tulpn | grep "8080" | wc -l) -ge 1 ]; do
      echo "INFO - Esperando que inicie el servidor"
      sleep 1
done

# La opcion -q muestra solo excepciones y outputs del .java
for (( i = 0; i < $TIMES; i++ )); do
        mvn -DskipTests exec:java -Dexec.mainClass=pdytr.example.grpc.Client -Dexec.args="-escribir ${ARCHIVO_SERVER} ${ARCHIVO_CLIENTE1}" -q & 
       	CLIENT_1_PID=$!

       	mvn -DskipTests exec:java -Dexec.mainClass=pdytr.example.grpc.Client -Dexec.args="-escribir ${ARCHIVO_SERVER} ${ARCHIVO_CLIENTE2}" -q & 
       	CLIENT_2_PID=$!

       	wait $CLIENT_1_PID
       	wait $CLIENT_2_PID

       	echo "------------------" >> $OUTPUT
       	echo "Ejecución $i: " >> $OUTPUT
       	echo $(cat $ARCHIVO_SERVER) >> $OUTPUT
done

kill -9 $SERVER_PID