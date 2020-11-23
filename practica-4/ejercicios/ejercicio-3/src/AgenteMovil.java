import jade.core.*;
import jade.core.behaviours.*;
import jade.wrapper.*;


public class AgenteMovil extends Agent {
    private String pathOrigen;
    private String pathDestino;
    private String pathCopiaDestino;

    private String containerDestino;
    private String containerOrigen;

    public void onDestroy() {
        doDelete();
        System.exit(0);
    }

    private void setupComportamientos() {
        SequentialBehaviour tareas = new SequentialBehaviour(this) {
            public int onEnd() {
                System.out.println("Transferencias finalizadas.");
                onDestroy();
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
    }

    @Override
    public void setup() {
        /* Control de argumentos */
        Object[] args = getArguments();

        if ((args == null) || (args.length != 4)) {
            System.err.println("Argumentos invalidos, se necesitan 4 argumentos:");
            System.err.println("<containerDestino> <pathOrigen>" +
                               "<pathDestino> <pathCopiaDestino>");
            System.exit(1);
        }

        containerOrigen = here().getName();
        containerDestino = args[0].toString();

        pathOrigen = args[1].toString();
        pathDestino = args[2].toString();
        pathCopiaDestino = args[3].toString();

        /* Inicializacion de Comportamientos */
        setupComportamientos();
    }
}
