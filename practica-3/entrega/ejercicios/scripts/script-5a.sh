#!/bin/bash

# Verifica los parámetros
if [[ $# -ne 1 ]]; then
        echo "Seleccionar opcion: [ -time | -ejercicio5a | -ejercicio5b ]"
        exit 1
fi

#Configurando el script para ejecutar el inciso deseado
if [[ $1 == "-time" ]]; then
	OUTPUT="../outputs/time-test.csv"
	DEADLINE=99999	#Valor muy alto para que no aborte el cliente
	TIMES=50
elif [[ $1 == "-ejercicio5a" ]]; then
	OUTPUT="../outputs/ejercicio5a-test.csv"
	DEADLINE=497	#Promedio de tiempo calculado anteriormente
	TIMES=10
elif [[ $1 == "-ejercicio5b" ]]; then
	OUTPUT="../outputs/ejercicio5b-test.csv"
	DEADLINE=447  	#Redujimos 10% el tiempo calculado en 5a
	TIMES=10
else
	echo "Opcion invalida, usar: [ -time | -ejercicio5a | -ejercicio5b ]"
	exit 1
fi

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
        mvn -DskipTests exec:java -Dexec.mainClass=pdytr.example.grpc.Client -Dexec.args="${DEADLINE}" -q >> $OUTPUT
        echo "Tiempo de la ejecución $i finalizado"
done

kill -9 $pid


