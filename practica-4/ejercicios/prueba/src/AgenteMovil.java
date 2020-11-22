import jade.core.Agent;
import jade.core.behaviours.*;
import jade.core.ContainerID;


public class AgenteMovil extends Agent {

    protected void setup() {
        System.out.println("Empece");

        SequentialBehaviour secuencial = new SequentialBehaviour(this) {
            public void onStart() {

            }
            
            public int onEnd() {
                System.out.println("Termine de Leer y Escribir");
                doDelete();
                return super.onEnd();
            }

        };
        secuencial.addSubBehaviour(new LecturaBehaviour());
        secuencial.addSubBehaviour(new EscrituraBehaviour());
        addBehaviour(secuencial);
    }

    private class LecturaBehaviour extends Behaviour {
        private int step = 1;

        public void onStart() {
            System.out.println("OnStart");

            String nombreContainer = "Main-Container";
            ContainerID destino = new ContainerID(nombreContainer, null);
            doMove(destino);

            root().block();
        }

        public void action() {                                
            System.out.println("Leer en " + myAgent.here().getName() + " " + step);
            step++;
        }

        public boolean done() {
            return step == 5;
        }

        public int onEnd() {
            System.out.println("Termine de leer :)");
            return super.onEnd();
        }
    }

    private class EscrituraBehaviour extends Behaviour {
        private int step = 1;

        public void action() {
            if (step == 1) {
                String nombreContainer = "Main-Container";
                ContainerID destino = new ContainerID(nombreContainer, null);
                // doMove(destino);
            }
            System.out.println("Escribir en " + myAgent.here().getName());
            step++;
        }

        public boolean done() {
            return step == 5;
        }

        public int onEnd() {
            System.out.println("Termine de escribir :)");
            return super.onEnd();
        }
    }
}
