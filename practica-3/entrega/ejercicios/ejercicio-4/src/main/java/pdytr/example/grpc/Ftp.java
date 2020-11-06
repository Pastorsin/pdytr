package pdytr.example.grpc;

import io.grpc.stub.StreamObserver;
import com.google.protobuf.ByteString;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

import pdytr.example.grpc.FtpOuterClass.*;


public class Ftp extends FtpGrpc.FtpImplBase {
    @Override
    public void leer(LecturaRequest request, StreamObserver<LecturaResponse> responseObserver) {
        System.out.println(request);

        try {
            FileInputStream fi = new FileInputStream(request.getNombre());

            int posicion = request.getPosicion();
            int offset = request.getOffset();

            byte[] contenido = new byte[offset];

            fi.skip(posicion);
            int bytesLeidos = fi.read(contenido, 0, offset);

            LecturaResponse response = LecturaResponse.newBuilder()
                                       .setContenido(ByteString.copyFrom(contenido))
                                       .setBytesLeidos(bytesLeidos)
                                       .build();

            responseObserver.onNext(response);

            fi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        responseObserver.onCompleted();
    }

    @Override
    public void escribir(EscrituraRequest request, StreamObserver<EscrituraResponse> responseObserver) {

        try {
            File archivo = new File(request.getNombre());
            FileOutputStream fo = new FileOutputStream(
                archivo,
                !request.getDestructiva()
            );

            byte[] contenido = request.getContenido().toByteArray();

            fo.write(contenido, 0, request.getOffset());

            long bytesEscritos = archivo.length();

            System.out.printf("Bytes escritos: %d\n",bytesEscritos);
            System.out.printf("Offset: %d\n",request.getOffset());
            System.out.println("---");

            EscrituraResponse response = EscrituraResponse.newBuilder()
                                         .setBytesEscritos(bytesEscritos)
                                         .build();

            responseObserver.onNext(response);

            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseObserver.onCompleted();

    }
}