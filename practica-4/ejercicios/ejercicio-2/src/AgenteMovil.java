import jade.core.*;
import java.util.*;
import jade.wrapper.*;

import java.io.File;
import java.io.FileNotFoundException; 

import java.util.Scanner;


public class AgenteMovil extends Agent {

    private List<String> COMPUTADORAS_ADICIONALES = new ArrayList<String> () {{
        add("PC-0");
        add("PC-1");
        add("PC-2");
    }};

    private int actual = 0;
    private Integer sumaTotal = 0;
    private Integer[] suma = new Integer[4];

    private String idOrigen, contenedorOrigen, path;

    private void crearComputadoras() {
        jade.core.Runtime runtime = jade.core.Runtime.instance();

        for (String pc : COMPUTADORAS_ADICIONALES) {
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.CONTAINER_NAME, pc);
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            ContainerController container = runtime.createAgentContainer(profile);
        }
    }

    public void migrarAgente(){
        // Migra el agente al container cuyo nombre llega por parametro
        try {
            String container = COMPUTADORAS_ADICIONALES.get(actual);
            ContainerID destino = new ContainerID(container, null);
            System.out.println("Migrando el agente a " + destino.getID());
            doMove(destino);
        } catch (Exception e) {
            System.out.println("\n\n\nNo fue posible migrar el agente\n\n\n");
        }

    }

    public void setup() {
        crearComputadoras();
        Location origen = here();
        
        //Se guarda el nombre del container origen
        idOrigen = origen.getID() ;
        contenedorOrigen = (idOrigen).split("@")[0];
        System.out.println("\n\nContenedor origen: " + contenedorOrigen  + "\n");
        COMPUTADORAS_ADICIONALES.add("Main-Container");
        COMPUTADORAS_ADICIONALES.add(contenedorOrigen);
        migrarAgente();
    }

    public void realizarSuma(){
        try{
            //Se abre y se lee el archivo.
            String path = "database/" + COMPUTADORAS_ADICIONALES.get(actual) + ".csv"; 
            File archivo = new File(path);
            Scanner lector = new Scanner(archivo);
            suma[actual] = 0;
            System.out.println("Contenido del archivo: ");
            while (lector.hasNextLine()) {
                String datos = lector.nextLine();
                
                //Realiza la suma de numeros y no tiene en cuenta las letras.
                try{
                    suma[actual] += Integer.parseInt(datos);
                } catch(NumberFormatException e){
                    continue;
                }
                System.out.println(datos);
            }
            lector.close();

        } catch (FileNotFoundException e){
            System.out.println("Ocurrio un error");
            e.printStackTrace();
        }
    }

    private void loguearInformacion() {
        // Limpiar output
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        // Contenido
        for (int i = 0; i< COMPUTADORAS_ADICIONALES.size() -1; i++){
            System.out.println("Conteiner " +COMPUTADORAS_ADICIONALES.get(i) + " sumo " + suma[i]  );
            sumaTotal += suma[i];
        }
        System.out.println("Resultado de la suma total:" + sumaTotal + "\n");
    }

    protected void afterMove() {
        Location origen = here();
        System.out.println("\n\nHola, agente migrado con nombre local " + getLocalName());
        System.out.println("Y nombre completo... " + getName());
        System.out.println("Y en location " + origen.getID() + "\n");

        if(idOrigen.equals(origen.getID())){
            //El container origen imprime el resultado de la suma
            loguearInformacion();
        }else{
            realizarSuma();
            actual += 1;
            migrarAgente();
        }
    }
}
//Tener en cuenta si me mandan como parametro el container origen.