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

import java.io.FileOutputStream;
import java.io.File;

import java.nio.ByteBuffer;

/* This class implements the interface with remote methods */
public class RemoteClass extends UnicastRemoteObject implements IfaceRemoteClass {
    protected RemoteClass() throws RemoteException {
        super();
    }

    public byte[] leer(String nombre, int posicion, int cantidadBytesEscritos) throws RemoteException {
        FileInputStream stream = null;

        int bytesLeidos = 0;
        byte[] contenidoArchivo = null;

        ByteBuffer buffer = null;

        try {
            stream = new FileInputStream(nombre);

            System.out.println("Posicion: " + posicion);
            System.out.println("Ventana: " + cantidadBytesEscritos);

            // Se lee el archivo
            contenidoArchivo = new byte[cantidadBytesEscritos];
            stream.skip(posicion);
            bytesLeidos = stream.read(contenidoArchivo, 0, cantidadBytesEscritos);

            System.out.println("Bytes leidos: " + bytesLeidos);
            System.out.println("----");

            /* Aloca el tamano para la cantidad de bytes leidos
            y la longitud del contenido del archivo */
            buffer = ByteBuffer.allocate(cantidadBytesEscritos + 4);

            // En los primeros 4 bytes se colocan la cantidad de bytes leidos
            buffer.putInt(bytesLeidos);
            // En los siguientes bytes se coloca el contenido del archivo
            buffer.put(contenidoArchivo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer.array();
    }

    public long escribir(String nombre, int cantidadBytes, byte[] data) throws RemoteException {
        FileOutputStream stream = null;
        File archivo = null;

        try {
            archivo = new File(nombre);

            /* Si el archivo existe entonces abre el contenido sin sobreescribir
            Si el archivo no existe entonces se lo crea */
            stream = new FileOutputStream(archivo, archivo.exists());

            // Escribimos en el archivo
            stream.write(data, 0, cantidadBytes);

            // Cerramos el stream de datos
            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return archivo.length();
    }

    public void invocacion() throws RemoteException {
        try {
            Thread.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void infiniteLoop() throws RemoteException {
        try {
            System.out.println("Se conecta el cliente");
            while (true){

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}