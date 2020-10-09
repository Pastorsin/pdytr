/*
* RemoteClass.java
* Just implements the RemoteMethod interface as an extension to
* UnicastRemoteObject
*
*/
/* Needed for implementing remote method/s */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
/* This class implements the interface with remote methods */
public class RemoteClass extends UnicastRemoteObject implements IfaceRemoteClass
{
	protected RemoteClass() throws RemoteException
	{
		super();
	}
	/* Remote method implementation */
	public byte[] sendThisBack(byte[] data) throws RemoteException
	{
		System.out.println("Data back to client");
		return data;
	}

	public int leer(String nombre, int cantByte, int pos, byte[] data) throws RemoteException{

	}

	public int escribir(String nombre, int cantByte, byte[] data) throws RemoteException{
		try{

			FileOutputStream fileOutput = new FileOutputStream ("servidor/"nombre);
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);

			bufferedOutput.write(data,0,cantByte);
			
		}
	}catch (Exception e) {
		e.printStackTrace();
	} finally {
		bufferedOutput.close();
	}
}