import jade.core.*;
import jade.wrapper.*;

import java.util.*;


public class AgenteMovil extends Agent {
    private final String[] COMPUTADORAS_ADICIONALES = {
        "PC-0",
        "PC-1",
        "PC-2",
        "PC-3"
    };

    private int actual = 0;
    private List<Informacion> info = new ArrayList<>();

    private long tiempoInicial;
    private long tiempoDeRecoleccion;

    private void crearComputadoras() {
        jade.core.Runtime runtime = jade.core.Runtime.instance();

        for (String pc : COMPUTADORAS_ADICIONALES) {
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, pc);
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            ContainerController container = runtime.createAgentContainer(profile);
        }
    }

    private void inicializarInformacion() {
        String origen = here().getName();

        info.add(new Informacion(origen));

        info.add(new Informacion("Main-Container"));

        for (String pc : COMPUTADORAS_ADICIONALES)
            info.add(new Informacion(pc));
    }


    public void setup() {
        try {
            crearComputadoras();

            inicializarInformacion();

            tiempoInicial = System.currentTimeMillis();
            moverseAlContainerSiguiente();

        } catch (Exception e) {
            System.out.println("\n\n\nNo fue posible migrar el agente\n\n\n");
            e.printStackTrace();
        }
    }

    private void logearInformacion() {
        // Limpiar output
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        // Encabezados
        System.out.println("-----------------");
        System.out.println("ORIGEN: " + here().getName());
        System.out.printf("TIEMPO DE RECOLECCION: %d ms.\n", tiempoDeRecoleccion);
        System.out.println("-----------------");

        System.out.printf("%-20s %-10s %-10s\n",
                          "NOMBRE", "CPU USADO",
                          "MEMORIA DISPONIBLE");

        // Contenido
        for (Informacion i : info)
            System.out.println(i.toString());

    }

    private boolean esPcOrigen() {
        return actual == 0;
    }

    private void moverseAlContainerSiguiente() {
        int siguiente = siguiente();

        actual = siguiente;

        String nombreContainer = info.get(siguiente).getContainer();
        ContainerID destino = new ContainerID(nombreContainer, null);

        doMove(destino);
    }

    private int siguiente() {
        return (actual + 1) % info.size();
    }

    protected void afterMove() {
        try {

            info.get(actual).actualizar();

            if (esPcOrigen()) {
                tiempoDeRecoleccion = System.currentTimeMillis() - tiempoInicial;
                logearInformacion();

                Thread.sleep(1000);
                tiempoInicial = System.currentTimeMillis();
            }

            moverseAlContainerSiguiente();

        } catch (Exception e) {
            System.err.println("\n\n\nNo fue posible migrar el agente\n\n\n");
            e.printStackTrace();
        }
    }
}
