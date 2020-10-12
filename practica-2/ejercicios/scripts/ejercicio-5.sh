#!/bin/bash

OUTPUT="../outputs/ejercicio-5.csv"
TIMES=100

# Inicia el entorno
./entorno.sh -start

for (( i = 0; i < $TIMES; i++ )); do
	echo "Time in ms," > $OUTPUT
	java -cp "../ftp" AskRemote -ejercicio5 >> $OUTPUT
	echo "Tiempo de la ejecución $i finalizado"
done

# Finaliza el entorno
./entorno.sh -stop