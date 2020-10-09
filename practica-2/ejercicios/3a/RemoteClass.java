/*
* RemoteClass.java
* Just implements the RemoteMethod interface as an extension to
* UnicastRemoteObject
*
*/
/* Needed for implementing remote method/s */
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import static java.lang.System.out;
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

	public byte [] leer(String nombre, int cantByte, int pos) throws RemoteException{
		int leidosTotales = 0;
		byte[] data = new byte[cantByte];
		try
		{
            // Se abre el fichero original para lectura
			FileInputStream fileInput = new FileInputStream("servidor/" + nombre);
			BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
			
			byte[] buffer = new byte[cantByte];

			int leidos = bufferedInput.read(buffer);

			while (leidos > 0)
			{
				leidosTotales += leidos;
				System.arraycopy(buffer,0,data,leidosTotales, leidos);
				 buffer = new byte[cantByte];
				leidos=bufferedInput.read(buffer);
			}

			// Cierre de los ficheros
			bufferedInput.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return data;
	}

	public int escribir(String nombre, int cantByte, byte[] data) throws RemoteException{
		FileOutputStream fileOutput = null;
		BufferedOutputStream bufferedOutput = null;
		try{

			fileOutput = new FileOutputStream ("servidor/" + nombre);
			bufferedOutput = new BufferedOutputStream(fileOutput);
			bufferedOutput.write(data,0,cantByte);

		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			//bufferedOutput.close();
		}
		return cantByte;
	}
}