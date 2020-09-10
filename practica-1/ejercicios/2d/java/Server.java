/*
 * EchoServer.java
 * Just receives some data and sends back a "message" to a client
 *
 * Usage:
 * java Server port
 */
import java.io.*;
import java.net.*;

import java.util.Arrays;

import java.security.*;

import checksum.MD5Checksum;


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
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(Integer.valueOf(args[0]));
        }
        catch (Exception e)
        {
            System.out.println("Error on server socket");
            System.exit(1);
        }

        /* The socket to be created on the connection with the client */
        Socket connected_socket = null;

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

        String response = "I got your message";
        byte[] bufferResponse = response.getBytes();


        // Read the first 4 bytes -> size of message
        int bufferSize = fromclient.readInt();

        // Read 16 bytes -> MD5 checksum
        byte[] checksum = new byte[16];
        fromclient.read(checksum);


        // Read message until complet the buffer
        int totalBytesRead = 0;

        byte[] buffer = new byte[bufferSize];

        while (totalBytesRead < bufferSize)
        {
            int bytesRead = fromclient.read(
                                  buffer,
                                  totalBytesRead,
                                  bufferSize - totalBytesRead
                              );

            if ( bytesRead < 0 )
            {
                System.err.println("Error to read buffer");
                System.exit(1);
            }

            totalBytesRead += bytesRead;
        }

        /* Send the bytes back */
        toclient.write(bufferResponse, 0, bufferResponse.length);

        System.out.println(">> Correct bytes: " + MD5Checksum.isValid(checksum, buffer));
        System.out.println(">> Total bytes read: " + totalBytesRead);

        for (int i = 0; i < bufferSize; i++) {
            System.out.println(buffer[i]);            
        }

        /* Close everything related to the client connection */
        fromclient.close();
        toclient.close();
        connected_socket.close();
        serverSocket.close();
    }
}
