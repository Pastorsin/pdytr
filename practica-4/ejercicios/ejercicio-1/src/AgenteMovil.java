import jade.core.*;
import jade.wrapper.*;
import java.util.Arrays;

public class AgenteMovil extends Agent {
    private int i = 0;
    private final String[] containers = {
        "PC-0",
        "PC-1",
        "PC-2",
        "PC-3"
    };
    private long[] tiempos = new long[containers.length + 1];

    private void createContainers() {
        //Get the JADE runtime interface (singleton)
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        //Create a Profile, where the launch arguments are stored
        for (String containerName : containers) {
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, containerName);
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            //create a non-main agent container
            ContainerController container = runtime.createAgentContainer(profile);
        }
    }

    // Ejecutado por unica vez en la creacion
    public void setup() {
        createContainers();

        Location origen = here();
        System.out.println("\n\nHola, agente con nombre local " + getLocalName());
        System.out.println("Y nombre completo... " + getName());
        System.out.println("Y en location " + origen.getID() + "\n\n");
        // Para migrar el agente
        try {
            ContainerID destino = new ContainerID(containers[0], null);
            System.out.println("Migrando el agente a " + destino.getID());


            doMove(destino);
        } catch (Exception e) {
            System.out.println("\n\n\nNo fue posible migrar el agente\n\n\n");
        }
    }

    // Ejecutado al llegar a un contenedor como resultado de una migracion
    protected void afterMove() {
        try {
            long inicio = System.currentTimeMillis();

            Thread.sleep(2000);

            tiempos[i] = System.currentTimeMillis() - inicio;

            String nombreOrigen = here().getName();

            System.out.println(nombreOrigen + " - " + tiempos[i] + "ms.");

            // Para migrar el agente
            if (!nombreOrigen.equals("Main-Container")) {
                i++;
                String containerName = (i == containers.length) ? "Main-Container" : containers[i];
                ContainerID destino = new ContainerID(containerName, null);
                doMove(destino);
            } else {                
                long total = Arrays.stream(tiempos).sum();                
                System.out.println("Tiempo del recorrido " + total + " ms.");
            }
        } catch (Exception e) {
            System.err.println("\n\n\nNo fue posible migrar el agente\n\n\n");
            e.printStackTrace();
        }
    }
}
