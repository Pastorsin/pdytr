#!/bin/bash

OUTPUT="../outputs/ejercicio-5a-test.csv"
TIMES=10

# Se limpia el output
echo "Time in ns" > $OUTPUT

# Se posiciona en el directorio del ejercicio
cd "../ejercicio-5a"

# Ejecuta el servidor
mvn -DskipTests package exec:java -Dexec.mainClass=pdytr.example.grpc.App &
pid=$!

# Espera a que el objeto remoto sea iniciado
until [ $(netstat -tulpn | grep "8080" | wc -l) -ge 1 ]; do
      echo "INFO - Esperando que inicie el servidor"
      sleep 1
done

# La opcion -q muestra solo excepciones y outputs del .java
for (( i = 0; i < $TIMES; i++ )); do
        mvn -DskipTests exec:java -Dexec.mainClass=pdytr.example.grpc.Client -q >> $OUTPUT
        echo "Tiempo de la ejecución $i finalizado"
done

kill -9 $pid


