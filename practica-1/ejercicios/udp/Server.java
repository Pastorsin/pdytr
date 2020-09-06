/*
 * EchoServer.java
 * Just receives some data and sends back a "message" to a client
 *
 * Usage:
 * java Server port
 */

import java.io.*;
import java.net.*;

public class Server
{
    public static void main(String[] args) throws IOException
    {
        /* Check the number of command line parameters */
        if ((args.length != 1) || (Integer.valueOf(args[0]) <= 0) )
        {
            System.out.println("1 arguments needed: port");
            System.exit(1);
        }

        /* The server socket */
        DatagramSocket serverSocket = null;
        try
        {
            serverSocket = new DatagramSocket(Integer.valueOf(args[0]));
        }
        catch (Exception e)
        {
            System.out.println("Error on server socket");
            System.exit(1);
        }

        /* The socket to be created on the connection with the client */
        DatagramSocket connected_socket = null;

        try /* To wait for a connection with a client */
        {
            connected_socket = serverSocket.accept();
        }
        catch (IOException e)
        {
            System.err.println("Error on Accept");
            System.exit(1);
        }

        /* Streams from/to client */
        DataInputStream fromclient;
        DataOutputStream toclient;

        /* Get the I/O streams from the connected socket */
        fromclient = new DataInputStream(connected_socket.getInputStream());
        toclient   = new DataOutputStream(connected_socket.getOutputStream());

        byte[] buffer;

        String receivedData;
        long receivedDataInBytes;

        for (int i = 3; i <= 6; i++)
        {
            int bufferSize = (int)Math.pow(10, i);

            buffer = new byte[bufferSize];

            System.out.println("In process to read " + buffer.length + " bytes.");

            fromclient.read(buffer);

            receivedData = new String(buffer);
            receivedDataInBytes = receivedData.chars().filter(c -> c == 'a').count();

            System.out.println("Readed " + receivedDataInBytes + " bytes.");
        }

        fromclient.read(new byte[0]);

        /* Close everything related to the client connection */
        fromclient.close();
        toclient.close();
        connected_socket.close();
        serverSocket.close();
    }
}
