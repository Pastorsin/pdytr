import jade.core.*;
import jade.core.behaviours.*;
import jade.wrapper.*;


public class AgenteMovil extends Agent {
    private String pathOrigen;
    private String pathDestino;
    private String pathCopiaDestino;

    private String containerDestino;
    private String containerOrigen;

    private final SequentialBehaviour tareas = new SequentialBehaviour(this) {
        public int onEnd() {
            System.out.println("Transferencias finalizadas.");
            onDestroy();
            return super.onEnd();
        }
    };

    public void onDestroy() {
        doDelete();
        System.exit(0);
    }

    @Override
    public void setup() {        
        /* Control de argumentos */
        Object[] args = getArguments();

        if (args == null) {
            System.err.println("Especifique alguna operacion: -r -w -rw");
            System.exit(1);
        }

        String operacion = args[0].toString();

        TransferenciaBehaviour leer = null;
        TransferenciaBehaviour escribir = null;

        containerOrigen = here().getName();

        /* Identificacion de Operacion */
        switch(operacion) {
        case "-r":
            if (args.length != 4) {
                System.err.println("Argumentos invalidos, se necesitan 3 argumentos:");
                System.err.println("-r <containerDestino> <pathOrigen> <pathDestino>");
                System.exit(1);
            }

            containerDestino = args[1].toString();
            pathOrigen = args[2].toString();
            pathDestino = args[3].toString();

            leer = new TransferenciaBehaviour(
                containerOrigen, containerDestino, pathOrigen, pathDestino
            );

            tareas.addSubBehaviour(leer);

            break;
        case "-w":
            if (args.length != 4) {
                System.err.println("Argumentos invalidos, se necesitan 3 argumentos:");
                System.err.println("-w <containerDestino> <pathOrigen> <pathDestino>");
                System.exit(1);
            }

            containerDestino = args[1].toString();
            pathOrigen = args[2].toString();
            pathDestino = args[3].toString();

            escribir = new TransferenciaBehaviour(
                containerDestino, containerOrigen, pathDestino, pathOrigen
            );

            tareas.addSubBehaviour(escribir);

            break;
        case "-rw":
            if ((args.length != 5)) {
                System.err.println("Argumentos invalidos, se necesitan 4 argumentos:");
                System.err.println("-rw <containerDestino> <pathOrigen>" +
                                   "<pathDestino> <pathCopiaDestino>");
                System.exit(1);
            }

            containerDestino = args[1].toString();
            pathOrigen = args[2].toString();
            pathDestino = args[3].toString();
            pathCopiaDestino = args[4].toString();

            leer = new TransferenciaBehaviour(
                containerOrigen, containerDestino, pathOrigen, pathDestino
            );

            escribir = new TransferenciaBehaviour(
                containerDestino, containerOrigen, pathCopiaDestino, pathOrigen
            );

            tareas.addSubBehaviour(leer);
            tareas.addSubBehaviour(escribir);

            break;
        default:
            System.err.println("Operacion invalida");
            System.err.println("Operaciones disponibles: -r -w -rw");
            System.exit(1);
        }

        /* Adicion de Comportamiento */
        addBehaviour(tareas);

    }
}
