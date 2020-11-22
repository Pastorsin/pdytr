import java.io.Serializable;
import java.io.*;

import static java.lang.Math.min;


public class Transferencia implements Serializable {

    private static final int VENTANA = 100000;
    private long totalBytesLeidos = 0;
    private byte[] contenido;
    private Long bytesFaltantes = null;

    public void leer(String path) throws IOException, FileNotFoundException {
        File archivo = new File(path);
        Long tamanioArchivo = archivo.length();

        FileInputStream fi = new FileInputStream(archivo);

        fi.skip(totalBytesLeidos);

        int chunk = (int) min(tamanioArchivo - totalBytesLeidos, VENTANA);

        contenido = new byte[chunk];
        totalBytesLeidos += fi.read(contenido, 0, chunk);

        System.out.println("Bytes leidos: " + totalBytesLeidos);
        fi.close();

        // Se actualiza la cantidad de bytes faltantes
        // para saber si la transferencia termino.
        bytesFaltantes = tamanioArchivo - totalBytesLeidos;
    }

    public void escribir(String path) throws IOException, FileNotFoundException {
        FileOutputStream fo = new FileOutputStream(path, modo());
        fo.write(contenido, 0, contenido.length);
        fo.close();

        System.out.println("Bytes escritos: " + contenido.length);
    }

    private boolean modo() {
        // Si es la primer iteracion, entonces la escritura se hace destructiva.
        int chunk = contenido.length;
        return totalBytesLeidos != chunk;
    }

    public boolean finalizada() {
        return bytesFaltantes.equals(0L);
    }

}