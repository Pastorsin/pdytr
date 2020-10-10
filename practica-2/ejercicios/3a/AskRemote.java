/*
* AskRemote.java
* a) Looks up for the remote object
* b) "Makes" the RMI
*/
import java.rmi.Naming; /* lookup */
import java.rmi.registry.Registry; /* REGISTRY_PORT */

import java.io.IOException;
import java.nio.ByteBuffer;

import java.rmi.RemoteException;

public class AskRemote {
    static public int VENTANA = 1024;

    public static void leer(IfaceRemoteClass remote) throws RemoteException {
        int totalBytesLeidos = 0;
        int bytesLeidos = 0;

        ByteBuffer contenidoArchivo = ByteBuffer.allocate(0);

        do {
            byte[] buffer = remote.leer("test.txt", totalBytesLeidos, VENTANA);

            ByteBuffer stream = ByteBuffer.wrap(buffer);

            // Leer los primeros 4 bytes - Bytes leidos del archivo
            bytesLeidos = stream.getInt();

            if (bytesLeidos > 0) {
          	  	// Leer el resto - Contenido del archivo
            	buffer = new byte[bytesLeidos];
            	stream.get(buffer);

            	// Concatena el contenido del archivo
	            totalBytesLeidos += bytesLeidos;
                contenidoArchivo = ByteBuffer.allocate(totalBytesLeidos).put(contenidoArchivo.array());
                contenidoArchivo.put(buffer);
            }

        } while(bytesLeidos > 0);

        System.out.println(new String(contenidoArchivo.array()));

    }

    public static void main(String[] args) {
        /* Look for hostname and msg length in the command line */
        if (args.length != 1) {
            System.out.println("1 argument needed: (remote) hostname");
            System.exit(1);
        }
        try {
            String rname = "//" + args[0] + ":" + Registry.REGISTRY_PORT + "/remote";
            IfaceRemoteClass remote = (IfaceRemoteClass) Naming.lookup(rname);

            leer(remote);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}