# Instrucciones

## Compilación:
`mkdir classes`  
`javac -classpath bin/jade/lib/jade.jar -d classes src/AgenteMovil.java`

## Ejecución:
### Boot:
`java -cp bin/jade/lib/jade.jar:classes jade.Boot -gui`
### Agente:
`java -cp bin/jade/lib/jade.jar:classes jade.Boot -gui -container -host localhost -agents mol:AgenteMovil`
