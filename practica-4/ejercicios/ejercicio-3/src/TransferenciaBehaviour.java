import jade.core.*;
import jade.core.behaviours.*;

public class TransferenciaBehaviour extends Behaviour {
    /*
     * Transfiere un archivo que reside en el container destino hacia el
     * container origen
    */
    private String origen;
    private String destino;
    private String pathOrigen;
    private String pathDestino;

    Transferencia transferencia = new Transferencia();

    private boolean escrituraFinalizada = false;
    private boolean modoAppend = false;

    public TransferenciaBehaviour(String origen, String destino,
                                  String pathOrigen, String pathDestino) {
        super();

        this.origen = origen;
        this.destino = destino;

        this.pathOrigen = pathOrigen;
        this.pathDestino = pathDestino;
    }

    private void moverAlContainer(String nombreContainer) {
        ContainerID destino = new ContainerID(nombreContainer, null);
        myAgent.doMove(destino);
    }

    private String containerActual() {
        return myAgent.here().getName();
    }

    @Override
    public void action() {
        try {
            
            if (containerActual().equals(origen)) {
                System.out.println("Escritura en " + containerActual());

                transferencia.escribir(pathOrigen, modoAppend);
                moverAlContainer(destino);

                modoAppend = true;
                escrituraFinalizada = transferencia.finalizada();
            } else {
                System.out.println("Lectura en " + containerActual());

                transferencia.leer(pathDestino);
                moverAlContainer(origen);
            }
        } catch (Exception e) {
            System.err.println("ERROR - Escribir/Leer archivo");
            e.printStackTrace();
            myAgent.doDelete();
        }
    }

    @Override
    public boolean done() {
        return escrituraFinalizada;
    }

    @Override
    public int onEnd() {
        System.out.println("Transferencia finalizada en " + pathOrigen);
        return super.onEnd();
    }
}