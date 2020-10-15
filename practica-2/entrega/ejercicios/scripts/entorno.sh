#!/bin/bash

# Este script inicia y finaliza el entorno de ejecución

# Verifica los parámetros
if [[ $# -ne 1 ]]; then
	echo "Se necesitan los parámetros: operacion"
fi


if [[ $1 == "-start" ]]; then
	echo "INFO - Iniciando entorno"

	cd ../ftp

	# Levanta un rmiregistry en el directorio ftp
	if [[ ! -z $(ss -tl | grep rmiregistry) ]]; then
		killall rmiregistry
		while [[ ! -z $(ss -tl | grep rmiregistry) ]]; do
			echo "INFO - Cerrando puerto RMI de otros directorios"
			sleep 1
		done
	fi

	echo "INFO - Abriendo puerto RMI en el directorio ftp"
	rmiregistry &

	# Compila los archivos
	javac *java
	echo "INFO - Archivos compilados"

	# Inicia el objeto remoto
	echo "INFO - Lanzando objeto remoto"

	java StartRemoteObject > /dev/null &
	
	cd ../scripts

	# Espera a que el objeto remoto sea iniciado
	until [ $(ss -ta | grep -v TIME-WAIT | grep ":rmiregistry" | wc -l) -ge 3 ]; do
		echo "INFO - Esperando objeto remoto"
		sleep 1
	done

elif [[ $1 == "-stop" ]]; then
	echo "INFO - Finalizando entorno"
	killall rmiregistry
	killall java
	
else
	echo "Operacion inválida."
	exit 1
fi;

exit 0
