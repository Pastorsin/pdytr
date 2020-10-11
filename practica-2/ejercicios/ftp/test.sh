#!/bin/bash

# Verifica los parámetros
if [[ $# -ne 1 ]]; then
	echo "ERROR - Se necesita 1 parámetro: filename"
	exit 1
fi

# Verifica que el puerto rmi estea abierto
if [[ -z $(ss -tl | grep rmiregistry) ]]; then	
	rmiregistry &
	RMI_REGISTRY_PID=$!
fi

# Compila los archivos
javac *java
echo "INFO - Archivos compilados"

# Inicia el objeto remoto
echo "INFO - Lanzando objeto remoto"
java StartRemoteObject > /dev/null &
REMOTE_OBJECT_PID=$!

# Espera a que el objeto remoto sea iniciado
sleep 2

# Ubicacion de los archivos
ORIGINAL="database/server/originales/$1"
COPIA_CLIENTE="database/client/copias/$1"
COPIA_SERVIDOR="database/server/copias/$1"

# Elimina las copias si existen
[[ -f $COPIA_CLIENTE ]] && rm $COPIA_CLIENTE
[[ -f $COPIA_SERVIDOR ]] && rm $COPIA_SERVIDOR

# Inicia el cliente
echo "INFO - Lanzando cliente"
java AskRemote $ORIGINAL $COPIA_CLIENTE $COPIA_SERVIDOR
CLIENT_ID=$!

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

# Elimina los procesos creados
[[ ! -z "$RMI_REGISTRY_PID" ]] && kill -9 $RMI_REGISTRY_PID
kill -9 $REMOTE_OBJECT_PID
kill -9 $CLIENT_ID

exit 0
