/*
 * Server.java
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


public class Server {

    public static void main(String[] args) throws IOException {
        String response = "I got your message";

        byte[] bufferOut = response.getBytes();
        byte[] bufferIn;
        byte[] checksum = new byte[16];

        int bufferSize;
        int totalBytesRead = 0;
        int bytesRead;
        
        /* Check the number of command line parameters */
        if ((args.length != 1) || (Integer.valueOf(args[0]) <= 0) ) {
            System.out.println("1 arguments needed: port");
            System.exit(1);
        }

        /* The server socket */
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Integer.valueOf(args[0]));
        } catch (Exception e) {
            System.out.println("Error on server socket");
            System.exit(1);
        }

        /* The socket to be created on the connection with the client */
        Socket connected_socket = null;

        try { /* To wait for a connection with a client */
            connected_socket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Error on Accept");
            System.exit(1);
        }

        /* Streams from/to client */
        DataInputStream fromclient;
        DataOutputStream toclient;

        /* Get the I/O streams from the connected socket */
        fromclient = new DataInputStream(connected_socket.getInputStream());
        toclient   = new DataOutputStream(connected_socket.getOutputStream());

        // Read the first 4 bytes -> size of message
        bufferSize = fromclient.readInt();

        // Read 16 bytes -> MD5 checksum
        fromclient.read(checksum);

        // Read message until complet the bufferIn
        bufferIn = new byte[bufferSize];

        while (totalBytesRead < bufferSize) {
            bytesRead = fromclient.read(
                            bufferIn,
                            totalBytesRead,
                            bufferSize - totalBytesRead
                        );

            if ( bytesRead < 0 ) {
                System.err.println("Error to read bufferIn");
                System.exit(1);
            }

            totalBytesRead += bytesRead;
        }

        /* Send the bytes back */
        toclient.write(bufferOut, 0, bufferOut.length);

        /* Close everything related to the client connection */
        fromclient.close();
        toclient.close();
        connected_socket.close();
        serverSocket.close();
    }
}
