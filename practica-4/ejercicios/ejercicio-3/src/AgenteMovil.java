import jade.core.*;
import jade.wrapper.*;

import java.io.*;

import static java.lang.Math.min;

/* TODO:
- Establecer los parametros
- Poner como opcion la escritura destructiva
- Testear:
--> Leer desde el origen al destino
--> Escribir desde el origen en el destino
*/
public class AgenteMovil extends Agent {

    private String pathCliente = "database/cliente.mp4";
    private String pathServidor = "database/server.mp4";

    private String containerDestino = "Main-Container";
    private String containerOrigen;

    private String operacion = "leer";

    // Refactor en nueva clase
    // Clase base Operacion
    // Subclases Lectura-Escribir que reusen el leer y escribir
    private static final int VENTANA = 1000000;
    private int bytesLeidos = 0;
    private byte[] contenido;
    private int bytesFaltantes = 0;


    private void leer(String path) throws IOException, FileNotFoundException {
        File archivo = new File(path);
        FileInputStream fi = new FileInputStream(archivo);

        fi.skip(bytesLeidos);
        bytesFaltantes = (int) min(archivo.length() - bytesLeidos, VENTANA);

        System.out.println("Bytes faltantes: " + bytesFaltantes);

        contenido = new byte[bytesFaltantes];
        bytesLeidos += fi.read(contenido, 0, bytesFaltantes);

        fi.close();
    }

    private void escribir(String path) throws IOException, FileNotFoundException {
        FileOutputStream fo = new FileOutputStream(path, true);
        fo.write(contenido, 0, contenido.length);
        fo.close();

        System.out.println("Bytes escritos: " + contenido.length);
    }

    private void moverAlContainer(String nombreContainer) {
        ContainerID destino = new ContainerID(nombreContainer, null);
        doMove(destino);
    }

    public void setup() {
        try {
            containerOrigen = here().getName();
            moverAlContainer(containerDestino);
        } catch (Exception e) {
            System.out.println("\n\n\nNo fue posible migrar el agente\n\n\n");
            e.printStackTrace();
        }
    }

    protected void afterMove() {
        try {
            String containerActual = here().getName();
            System.out.println("Estoy en el container " + containerActual);

            if (containerActual.equals(containerDestino)) {
                leer();
                moverAlContainer(containerOrigen);
            } else {
                escribir();

                if (bytesFaltantes > 0)
                    moverAlContainer(containerDestino);
                else
                    System.out.println("Termine!");
            }

        } catch (Exception e) {
            System.err.println("\n\n\nNo fue posible migrar el agente\n\n\n");
            e.printStackTrace();
        }
    }

}
