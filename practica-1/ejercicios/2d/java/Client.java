/*
 * Client.java
 * Just sends stdin read data to and receives back some data from the server
 *
 * usage:
 * java Client serverhostname port
 */
import java.io.*;
import java.net.*;

import java.security.*;

import java.lang.Math;

import checksum.MD5Checksum;

import java.time.*;


public class Client
{

    public static void main(String[] args) throws IOException
    {
        /* Check the number of command line parameters */
        if ((args.length != 3) || (Integer.valueOf(args[1]) <= 0) || (Integer.valueOf(args[2]) <= 0) )
        {
            System.out.println("3 arguments needed: serverhostname port sizeofmessage");
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

        /* Send message to server */
        int bufferSize = Integer.valueOf(args[2]);

        byte[] bufferOut = new byte[bufferSize];
        byte[] bufferIn = new byte[bufferSize];
        byte[] checksum;

        byte content = 1;

        // Fill the bufferOut with '1's
        for (int j = 0; j < bufferSize; j++)
            bufferOut[j] = content;

        checksum = MD5Checksum.generate(bufferOut);

        long startTime = System.currentTimeMillis();
        // Write the first 4 bytes -> size of message
        toserver.writeInt(bufferSize);
        // Write 16 bytes -> MD5 checksum
        toserver.write(checksum, 0, checksum.length);
        // Write message
        toserver.write(bufferOut, 0, bufferSize);
        // Wait server response
        fromserver.read(bufferIn, 0, bufferSize);
        long endTime = System.currentTimeMillis();

        System.out.println((endTime - startTime));

        // System.out.println("Time: " + (endTime - startTime) + " ms.");
        // System.out.println("Server response: " + new String(bufferIn));
        // System.out.println("Bytes write: " + bufferSize);

        fromserver.close();
        toserver.close();
        socketwithserver.close();
    }
}
