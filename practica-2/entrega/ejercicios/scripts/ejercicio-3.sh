#!/bin/bash

# Verifica los parámetros
if [[ $# -ne 1 ]]; then
	echo "ERROR - Se necesita 1 parámetro: filename"
	exit 1
fi

# Inicia el entorno
./entorno.sh -start

# Ubicacion de los archivos
ORIGINAL="../ftp/database/server/originales/$1"
COPIA_CLIENTE="../ftp/database/client/copias/$1"
COPIA_SERVIDOR="../ftp/database/server/copias/$1"

# Inicia el cliente
echo "INFO - Lanzando cliente"
java -cp "../ftp" AskRemote -ejercicio3 $ORIGINAL $COPIA_CLIENTE $COPIA_SERVIDOR

# Se testea que el contenido de los 3 archivos no difiera
echo "TEST - Comparación de contenido entre $ORIGINAL $COPIA_CLIENTE $COPIA_SERVIDOR"

diff1=$(diff $ORIGINAL $COPIA_CLIENTE)
diff2=$(diff $COPIA_CLIENTE $COPIA_SERVIDOR)

if [[ $diff1 || $diff2 ]]; then
	echo "ERROR - Los archivos no coinciden"
	echo "$diff1"
	echo "$diff2"
else
	echo "OK - Los archivos coinciden"
fi

# Cierra el entorno
./entorno.sh -stop

exit 0
