import jade.core.*;
import jade.core.behaviours.*;
import jade.wrapper.*;


/* TODO:
- Establecer los parametros
- Poner como opcion la escritura destructiva
- Testear:
--> Escribir desde origen hacia origen
*/
public class AgenteMovil extends Agent {
    private String pathOrigen = "database/cliente.mp4";
    private String pathDestino = "database/server.mp4";
    private String pathCopiaDestino = "database/server-copia.mp4";

    private String containerDestino = "Main-Container";
    private String containerOrigen;

    @Override
    public void setup() {
        try {
            /* Control de argumentos */

            /* Inicializacion de Comportamientos */
            containerOrigen = here().getName();

            SequentialBehaviour tareas = new SequentialBehaviour(this) {
                public int onEnd() {
                    System.out.println("Transferencias finalizadas.");
                    doDelete();
                    return super.onEnd();
                }
            };

            TransferenciaBehaviour transferenciaCliente = new TransferenciaBehaviour(
                containerOrigen, containerDestino, pathOrigen, pathDestino
            );

            TransferenciaBehaviour transferenciaCopia = new TransferenciaBehaviour(
                containerDestino, containerOrigen, pathCopiaDestino, pathOrigen
            );

            tareas.addSubBehaviour(transferenciaCliente);
            tareas.addSubBehaviour(transferenciaCopia);

            addBehaviour(tareas);

            /* Se mueve al container destino para comenzar la lectura */
            ContainerID destino = new ContainerID(containerDestino, null);
            doMove(destino);

        } catch (Exception e) {
            System.out.println("No fue posible migrar el agente");
            e.printStackTrace();
            doDelete();            
        }
    }
}
