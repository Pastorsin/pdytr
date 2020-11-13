import jade.core.*;
import jade.wrapper.*;

import java.util.*;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.net.*;
import java.io.Serializable;


public class AgenteMovil extends Agent {
    private String[] computadorasAdicionales = {
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

        for (String pc : computadorasAdicionales) {
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, pc);
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            ContainerController container = runtime.createAgentContainer(profile);
        }
    }

    private void inicializarInformacion() {
        for (String pc : computadorasAdicionales)
            info.add(new Informacion(pc));

        info.add(new Informacion("Main-Container"));

        info.add(new Informacion(here().getName()));
    }

    private void logearInformacion() {
        // Limpiar output
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // Encabezados
        System.out.printf("%-20s %-10s %-10s\n",
                          "NOMBRE", "CPU USADO",
                          "MEMORIA DISPONIBLE");

        // Contenido
        for (Informacion i : info)
            System.out.println(i.toString());

        System.out.println("-----------------");
        System.out.printf("TIEMPO DE RECOLECCION: %d ms.\n", tiempoDeRecoleccion);
        System.out.println("-----------------");
    }

    private boolean esPcOrigen(int indice) {
        return indice == (info.size() - 1);
    }

    public void setup() {
        crearComputadoras();
        inicializarInformacion();

        try {
            ContainerID destino = new ContainerID(info.get(0).getContainer(), null);
            tiempoInicial = System.currentTimeMillis();
            doMove(destino);
        } catch (Exception e) {
            System.out.println("\n\n\nNo fue posible migrar el agente\n\n\n");
        }
    }

    protected void afterMove() {
        try {

            info.get(actual).actualizar();

            if (esPcOrigen(actual)) {
                tiempoDeRecoleccion = System.currentTimeMillis() - tiempoInicial;
                logearInformacion();
                Thread.sleep(1000);
                tiempoInicial = System.currentTimeMillis();
            }

            actual = (actual + 1) % info.size();

            String nombreContainer = info.get(actual).getContainer();
            ContainerID destino = new ContainerID(nombreContainer, null);

            doMove(destino);

        } catch (Exception e) {
            System.err.println("\n\n\nNo fue posible migrar el agente\n\n\n");
            e.printStackTrace();
        }
    }

    public static class Informacion implements Serializable {
        private String container;
        private double cpuUsado;
        private long memoriaDisponible;

        public Informacion(String container) {
            this.container = container;
        }

        public void actualizar() throws Exception {
            OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory
                                         .getOperatingSystemMXBean();

            this.cpuUsado = bean.getSystemCpuLoad() * 100;
            this.memoriaDisponible = bean.getFreePhysicalMemorySize();
        }

        public String getContainer() {
            return container;
        }

        @Override
        public String toString() {
            return String.format("%-20s %-10.2f %d Bytes",
                                 container, cpuUsado, memoriaDisponible);
        }

    }
}
