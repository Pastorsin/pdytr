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

public class Server
{
    static int correctBytes(byte[] buffer, byte aByte)
    {
        int correctBytes = 0;
        for (int j = 0; j < buffer.length; j++)
        {
            if (buffer[j] == aByte)
                correctBytes ++;
        }

        return correctBytes;
    }

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

        for (byte i = 3; i <= 6; i++)
        {
            int bufferSize = (int)Math.pow(10, i);

            /* Read message */

            System.out.println("------------------------------------------");
            System.out.println("10 ^ " + i);
            System.out.println("------------------------------------------");
            // fromclient.readFully(buffer, 0, bufferSize);

            int totalBytesReaded = 0;

            byte[] buffer = new byte[bufferSize];

            while (totalBytesReaded < bufferSize)
            {
                int bytesRemaining = bufferSize - totalBytesReaded;

                byte[] auxBuffer = new byte[bytesRemaining];

                int bytesReaded = fromclient.read(auxBuffer);

                if ( bytesReaded > 0 ) {
                    /* Copy the elements readed into buffer */
                    for (int j = 0; j < bytesReaded; j++)
                    {
                        int index = totalBytesReaded + j;
                        buffer[index] = auxBuffer[j];
                    }                    
                } else {
                    System.err.println("Error to read buffer");
                    System.exit(1);
                }

                totalBytesReaded += bytesReaded;

                System.out.println("Readed bytes : " + bytesReaded);
                System.out.println("Remaining bytes : " + (bufferSize - totalBytesReaded));
                System.out.println();
            }

            System.out.println(">> Correct bytes: " + correctBytes(buffer, i));
            System.out.println(">> Total bytes read: " + totalBytesReaded);
        }

        /* Close everything related to the client connection */
        fromclient.close();
        toclient.close();
        connected_socket.close();
        serverSocket.close();
    }
}
