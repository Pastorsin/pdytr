#!/bin/bash

# Inicia el entorno
./entorno.sh -start

java -cp "../ftp"  -Dsun.rmi.transport.tcp.responseTimeout=3000 AskRemote -ejercicio5b

#finaliza el entorno
./entorno.sh -stop