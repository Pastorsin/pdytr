package pdytr.example.grpc;

import static java.lang.Math.min;

import io.grpc.*;
import io.grpc.stub.StreamObserver;
import com.google.protobuf.ByteString;

import java.util.Iterator;

import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;

import pdytr.example.grpc.GreetingServiceOuterClass.*;


public class Client {

    private static final int VENTANA = 1024;

    private static void leer(GreetingServiceGrpc.GreetingServiceBlockingStub stub, String path_servidor, String path_cliente) {
        try {
            int totalBytesLeidos = 0;
            int bytesLeidos = 0;

            FileOutputStream fo = new FileOutputStream(path_cliente);

            do {
                LecturaRequest request = LecturaRequest.newBuilder()
                                         .setNombre(path_servidor)
                                         .setPosicion(totalBytesLeidos)
                                         .setOffset(VENTANA)
                                         .build();

                LecturaResponse response = stub.leer(request);

                bytesLeidos = response.getBytesLeidos();
                System.out.println("Bytes leidos: " + bytesLeidos);

                if (bytesLeidos > 0) {

                    byte[] contenido = response.getContenido().toByteArray();
                    fo.write(contenido, 0, bytesLeidos);

                    totalBytesLeidos += bytesLeidos;
                }

            } while (bytesLeidos > 0);

            fo.close();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void escribir(GreetingServiceGrpc.GreetingServiceBlockingStub stub, String path_servidor, String path_cliente) {
        try {
            File archivo = new File(path_cliente);
            FileInputStream fi = new FileInputStream(archivo);

            long totalBytesEscritos = 0;

            do {

                byte[] contenido = new byte[VENTANA];

                int bytesFaltantes = fi.available();

                fi.read(contenido);

                EscrituraRequest request = EscrituraRequest.newBuilder()
                                           .setNombre(path_servidor)
                                           .setContenido(ByteString.copyFrom(contenido))
                                           .setOffset(min(VENTANA, bytesFaltantes))
                                           .setDestructiva(totalBytesEscritos == 0)
                                           .build();

                EscrituraResponse response = stub.escribir(request);

                totalBytesEscritos = response.getBytesEscritos();

                System.out.printf("Faltan %d bytes de %d\n", fi.available(), archivo.length());
                System.out.printf("Bytes escritos: %d\n", totalBytesEscritos);
                System.out.printf("Bytes totales: %d\n", archivo.length());
                System.out.println("---");


            } while(totalBytesEscritos < archivo.length());

        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args ) throws Exception {
        if (args.length != 3) {
            System.err.println("Se necesitan 2 argumentos:");
            System.err.println("[-leer|-escribir] path_servidor path_cliente");

            System.exit(1);
        }


        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
                                       .usePlaintext(true)
                                       .build();

        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);

        String operacion = args[0];
        String path_servidor = args[1];
        String path_cliente = args[2];

        switch (operacion) {

        case "-leer":
            leer(stub, path_servidor, path_cliente);
            break;
        case "-escribir":
            escribir(stub, path_servidor, path_cliente);
            break;
        default:
            System.err.println("Operacion invalida");
            System.err.println("[-leer|-escribir] path_servidor path_cliente");
        }

        System.out.println("Transferencia finalizada");
        System.out.printf("diff %s %s\n", path_servidor, path_cliente);

        channel.shutdownNow();

        System.exit(0);
    }
}