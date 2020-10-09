/*
* AskRemote.java
* a) Looks up for the remote object
* b) "Makes" the RMI
*/
import java.rmi.Naming; /* lookup */
import java.rmi.registry.Registry; /* REGISTRY_PORT */
public class AskRemote
{
	public static void main(String[] args)
	{
		/* Look for hostname and msg length in the command line */
		if (args.length != 1)
		{
			System.out.println("1 argument needed: (remote) hostname");
			System.exit(1);
		}
		try {
			String rname = "//" + args[0] + ":" + Registry.REGISTRY_PORT + "/remote";
			IfaceRemoteClass remote = (IfaceRemoteClass) Naming.lookup(rname);
			int bufferlength = 1024;
			byte[] buffer = new byte[bufferlength];
			buffer = remote.leer("archivo.txt",bufferlength,0);
			System.out.println(new String(buffer));
			remote.escribir("nuevo.txt",bufferlength,buffer);
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}