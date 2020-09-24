/*
 * Client.java
 * Just sends stdin read data to and receives back some data from the server
 *
 * usage:
 * java Client serverhostname port
 */

import java.io.*;
import java.net.*;

import java.lang.Math;


public class Client
{

    public static void main(String[] args) throws IOException
    {
        /* Check the number of command line parameters */
        if ((args.length != 2) || (Integer.valueOf(args[1]) <= 0) )
        {
            System.out.println("2 arguments needed: serverhostname port");
            System.exit(1);
        }

        /* The socket to connect to the echo server */
        Socket socketwithserver = null;

        try /* Connection with the server */
        {
            socketwithserver = new Socket(args[0], Integer.valueOf(args[1]));
        }
        catch (Exception e)
        {
            System.out.println("ERROR connecting");
            System.exit(1);
        }

        /* Streams from/to server */
        DataInputStream  fromserver;
        DataOutputStream toserver;

        /* Streams for I/O through the connected socket */
        fromserver = new DataInputStream(socketwithserver.getInputStream());
        toserver   = new DataOutputStream(socketwithserver.getOutputStream());

        byte[] buffer;

        /* Send 4 messages to server, from 10^3 until 10^6 bytes */
        for (byte i = 3; i <= 6; i++)
        {
            int bufferSize = (int)Math.pow(10, i);

            buffer = new byte[bufferSize];

            // Fill the buffer with actual index
            for (int j = 0; j < bufferSize; j++)
                buffer[j] = i;

            toserver.write(buffer, 0, buffer.length);

            System.out.println("Bytes send: " + bufferSize);
            System.out.println("------------------------------------------");
        }

        /* Recv data back from server (get space) */
        buffer = new byte[256];
        fromserver.read(buffer);

        /* Show data received from server */
        String resp = new String(buffer);
        System.out.println(resp);

        fromserver.close();
        toserver.close();
        socketwithserver.close();
    }
}
