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
        DatagramSocket socket = null;

        InetAddress ip = InetAddress.getByName(args[0]);
        int port = Integer.valueOf(args[1]);

        try /* Connection with the server */
        {
            socket = new DatagramSocket(port, ip);
        }
        catch (Exception e)
        {
            System.out.println("ERROR connecting");
            System.exit(1);
        }

        /* Send data as datagram */
        byte[] buffer;

        for (int i = 3; i <= 6; i++)
        {
            int bufferSize = (int)Math.pow(10, i);

            buffer = new byte[bufferSize];

            // Fill buffer with 'a' chars
            for (int j = 0; j < bufferSize; j++)
                buffer[j] = 'a';

            System.out.println("In process to write " + buffer.length + " bytes.");

            DatagramPacket sendPkt = new DatagramPacket(
                buffer,
                buffer.length,
                ip,
                port
            );

            socket.send(sendPkt);

            System.out.println("Writed " + buffer.length + " bytes.");
        }

        socket.close();
    }
}
