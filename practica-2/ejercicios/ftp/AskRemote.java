/*
* AskRemote.java
* a) Looks up for the remote object
* b) "Makes" the RMI
*/
import java.rmi.Naming; /* lookup */
import java.rmi.registry.Registry; /* REGISTRY_PORT */

import java.io.IOException;
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

        /* Verificacion de los parametros */
        if (args.length != 3) {
            System.err.println("Se necesitan 3 argumentos: archivo_original copia_cliente copia_servidor");
            System.exit(1);
        }

        try {
            /* Binding con el remoto */
            String rname = "//localhost:" + Registry.REGISTRY_PORT + "/remote";
            remote = (IfaceRemoteClass) Naming.lookup(rname);

            /* Operaciones de escritura - lectura */
            String archivoOriginal = args[0];
            String copiaCliente = args[1];
            String copiaServidor = args[2];

            /* Se lee el archivo original desde el servidor y
             * se guarda en el archivo "copiaCliente" en el cliente.
             */
            leer(archivoOriginal, copiaCliente);

            /* Se escribe el archivo "copiaCliente" desde el cliente
             * en el archivo "copiaServidor" del servidor.
             */
            escribir(copiaCliente, copiaServidor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}