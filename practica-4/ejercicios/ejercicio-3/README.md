# Instrucciones
## Dependencias:
`mkdir bin`  
`wget -O bin/jade.jar https://gist.github.com/Pastorsin/e2ec5fc34708c5702353aaf67c952412/raw/689050a5c834ffe1abb2fac8c2caf76a3a996c8b/jade.jar`  


## Compilación:
`mkdir classes`  
`javac -cp bin/jade.jar:src -d classes src/AgenteMovil.java`

## Ejecución:
### Boot:
`java -cp bin/jade.jar jade.Boot -gui`
### Agente modo Lectura (Ejercicio 3a):
`java -cp bin/jade.jar:classes jade.Boot -gui -container -host localhost -agents mol:AgenteMovil'(-r, Main-Container, database/cliente.jpg, database/server.jpg)'`
### Agente modo Escritura (Ejercicio 3a):
`java -cp bin/jade.jar:classes jade.Boot -gui -container -host localhost -agents mol:AgenteMovil'(-w, Main-Container, database/cliente.jpg, database/server.jpg)'`
### Agente modo Lectura/Escritura (Ejercicio 3b):
`java -cp bin/jade.jar:classes jade.Boot -gui -container -host localhost -agents mol:AgenteMovil'(-rw, Main-Container, database/cliente.jpg, database/server.jpg, database/server-copia.jpg)'`
