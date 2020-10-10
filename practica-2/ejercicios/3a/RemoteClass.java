/*
* RemoteClass.java
* Just implements the RemoteMethod interface as an extension to
* UnicastRemoteObject
*
*/
/* Needed for implementing remote method/s */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.ByteBuffer;

/* This class implements the interface with remote methods */
public class RemoteClass extends UnicastRemoteObject implements IfaceRemoteClass {
    protected RemoteClass() throws RemoteException {
        super();
    }

    public byte[] leer(String nombre, int posicion, int cantidadBytes) throws RemoteException {
        FileInputStream stream = null;

        int bytesLeidos = 0;
        byte[] contenidoArchivo = null;

        ByteBuffer buffer = null;

        try {
            stream = new FileInputStream("server/" + nombre);
        } catch (FileNotFoundException e) {
            System.err.println("El archivo no existe.");
            e.printStackTrace();
        }

        try {
            System.out.println("Posicion: " + posicion);
            System.out.println("Ventana: " + cantidadBytes);

            // Se lee el archivo
            contenidoArchivo = new byte[cantidadBytes];
            stream.skip(posicion);
            bytesLeidos = stream.read(contenidoArchivo, 0, cantidadBytes);

            System.out.println("Bytes leidos: " + bytesLeidos);
            System.out.println("----");

            /* Aloca el tamano para la cantidad de bytes leidos
            y la longitud del contenido del archivo */
            buffer = ByteBuffer.allocate(cantidadBytes + 4);

            // En los primeros 4 bytes se colocan la cantidad de bytes leidos
            buffer.putInt(bytesLeidos);
            // En los siguientes bytes se coloca el contenido del archivo
            buffer.put(contenidoArchivo);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer.array();
    }

    public int escribir(String nombre, int cantidadBytes, byte[] data) throws RemoteException {
        System.out.println("");
        return 1;
    }
}