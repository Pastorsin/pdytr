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

    private String containerDestino = "Container-1";
    private String containerOrigen;

    private void moverAlContainer(String nombreContainer) {
        ContainerID destino = new ContainerID(nombreContainer, null);
        doMove(destino);
    }

    public void setup() {
        try {
            containerOrigen = here().getName();

            SequentialBehaviour tareas = new SequentialBehaviour(this) {
                public int onEnd() {
                    System.out.println("Transferencias finalizadas.");
                    doDelete();
                    return super.onEnd();
                }
            };

            TransferenciaBehaviour transferenciaCliente = new TransferenciaBehaviour(
                containerOrigen,
                containerDestino,
                pathOrigen,
                pathDestino
            );

            TransferenciaBehaviour transferenciaCopia = new TransferenciaBehaviour(
                containerDestino,
                containerOrigen,
                pathCopiaDestino,
                pathOrigen
            );

            tareas.addSubBehaviour(transferenciaCliente);
            tareas.addSubBehaviour(transferenciaCopia);

            addBehaviour(tareas);

            moverAlContainer(containerDestino);

        } catch (Exception e) {
            System.out.println("No fue posible migrar el agente");
            e.printStackTrace();
        }
    }

    private class TransferenciaBehaviour extends Behaviour {
        /*
         * Transfiere un archivo que reside en el container destino hacia el
         * container origen
        */

        private String origen;
        private String destino;

        private String pathOrigen;
        private String pathDestino;

        Transferencia transferencia = new Transferencia();

        private boolean finalizada = false;

        public TransferenciaBehaviour(String origen, String destino,
                                      String pathOrigen, String pathDestino) {
            super();

            this.origen = origen;
            this.destino = destino;

            this.pathOrigen = pathOrigen;
            this.pathDestino = pathDestino;
        }

        public void action() {
            try {
                String containerActual = here().getName();

                if (containerActual.equals(origen)) {
                    System.out.println("Escritura en " + containerActual);

                    transferencia.escribir(pathOrigen);

                    finalizada = transferencia.finalizada();

                    if (!finalizada)
                        moverAlContainer(destino);

                } else {
                    System.out.println("Lectura en " + containerActual);

                    transferencia.leer(pathDestino);
                    moverAlContainer(origen);
                }

            } catch (Exception e) {
                System.err.println("ERROR - Escribir/Leer archivo");
                e.printStackTrace();
            }
        }

        public boolean done() {
            return finalizada;
        }

        public int onEnd() {
            System.out.println("Transferencia finalizada en " + pathOrigen);
            return super.onEnd();
        }
    }

}
