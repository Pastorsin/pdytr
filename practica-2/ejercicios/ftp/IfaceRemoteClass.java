/*

* IfaceRemoteClass.java
* Interface defining only one method which can be invoked remotely
*
*/
/* Needed for defining remote method/s */
import java.rmi.Remote;
import java.rmi.RemoteException;

/* This interface will need an implementing class */
public interface IfaceRemoteClass extends Remote {
    public byte[] leer(String nombre, int posicion, int cantidadBytes) throws RemoteException;

    public long escribir(String nombre, int cantidadBytes, byte[] data) throws RemoteException;

    public void invocacion() throws RemoteException;

    public void infiniteLoop() throws RemoteException;
}