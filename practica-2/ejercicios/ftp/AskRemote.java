/*
* AskRemote.java
* a) Looks up for the remote object
* b) "Makes" the RMI
*/
import java.rmi.Naming; /* lookup */
import java.rmi.registry.Registry; /* REGISTRY_PORT */

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;

public class AskRemote {

    private static int VENTANA = 1024;
    private static IfaceRemoteClass remote;

    private static void leer(String fuente, String destino) throws RemoteException {
        /* Lee el archivo Fuente (servidor) y copia el contenido en el archivo Destino (cliente) */

        int totalBytesLeidos = 0;
        int bytesLeidos = 0;

        /* Comienzo de la lectura */
        try {
            FileOutputStream streamOut = new FileOutputStream(destino);

            do {
                // Operacion remota de lectura
                byte[] bufferIn = remote.leer(fuente, totalBytesLeidos, VENTANA);
                ByteBuffer streamIn = ByteBuffer.wrap(bufferIn);

                // Se leen los primeros 4 bytes -> Bytes leidos del archivo
                bytesLeidos = streamIn.getInt();

                if (bytesLeidos > 0) {
                    // Se leen los bytes restantes -> Contenido del archivo
                    bufferIn = new byte[bytesLeidos];
                    streamIn.get(bufferIn);

                    // Escribe en el archivo destino el contenido recibido
                    streamOut.write(bufferIn);
                    totalBytesLeidos += bytesLeidos;
                }

            } while(bytesLeidos > 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void escribir(String fuente, String destino) throws RemoteException {
        /* Escribe el contenido del archivo Fuente (cliente) en el archivo Destino (servidor) */

        long totalBytesEscritos = 0;

        try {
            File archivoFuente = new File(fuente);
            long tamanoArchivo = archivoFuente.length();

            FileInputStream streamIn = new FileInputStream(fuente);

            byte[] bufferOut = new byte[VENTANA];

            do {
                long bytesFaltantes = tamanoArchivo - totalBytesEscritos;

                // Se calcula la cantidad de bytes a enviar
                int cantidadEnviar = (bytesFaltantes < VENTANA) ? (int)bytesFaltantes : VENTANA;

                // Se lee el archivo
                streamIn.read(bufferOut);

                // Operacion remota de escritura
                totalBytesEscritos = remote.escribir(destino, cantidadEnviar, bufferOut);

            } while (totalBytesEscritos < tamanoArchivo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {


        try {
            /* Binding con el remoto */
            String rname = "//localhost:" + Registry.REGISTRY_PORT + "/remote";
            remote = (IfaceRemoteClass) Naming.lookup(rname);

            String operacion = args[0];

            switch (operacion) {
            case "-ejercicio3" :
                /* Verificacion de los parametros */
                if (args.length != 4) {
                    System.err.println("Se necesitan 3 argumentos:");
                    System.err.println("java AskRemote -ejercicio3 archivo_original copia_cliente copia_servidor");

                    System.exit(1);
                }

                /* Operaciones de escritura - lectura */
                String archivoOriginal = args[1];
                String copiaCliente = args[2];
                String copiaServidor = args[3];

                /* Se lee el archivo original desde el servidor y
                 * se guarda en el archivo "copiaCliente" en el cliente.
                 */
                leer(archivoOriginal, copiaCliente);

                /* Se escribe el archivo "copiaCliente" desde el cliente
                 * en el archivo "copiaServidor" del servidor.
                 */
                escribir(copiaCliente, copiaServidor);
                break;

            case "-ejercicio4" :
                /* Experimento del ejercicio 4.
                 * Escribe el archivo fuente en el archivo destino del servidor.
                 * La idea es que se ejecuten N clientes en paralelo ejecutando
                 * con el mismo nombre del archivo fuente.
                 */
                if (args.length != 3) {
                    System.err.println("Se necesitan 2 argumentos:");
                    System.err.println("java AskRemote -ejercicio4 archivo_fuente archivo_destino");

                    System.exit(1);
                }

                VENTANA = 2;

                String fuente = args[1];
                String destino = args[2];

                escribir(fuente, destino);
                break;

            case "-ejercicio5":
                /* Experimento del ejercicio 5.
                 * Toma el tiempo minimo de una invocacion RMI
                 */

                long startTime = System.currentTimeMillis();
                remote.invocacion();
                System.out.println(System.currentTimeMillis() - startTime);
                break;
            case "-ejercicio5b":
                /* Experimento del ejercicio 5b.
                * Se realiza una consulta al servidor, la cual
                * nunca sera respondida.
                */
                try{
                    remote.infiniteLoop();
                }catch (RemoteException e){
                    System.err.println("Tiempo de espera de respuesta agotado");
                }   
                break;

            default:
                System.err.println("Operacion invalida");
                System.err.println("Especificar: -operacion parametros_de_la_operacion");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}