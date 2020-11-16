import jade.core.*;

import java.io.File;
import java.io.FileNotFoundException; 

import java.util.Scanner;


public class AgenteMovil extends Agent {
    // Ejecutado por unica vez en la creacion
    private File archivo;
    private Scanner lector;
    private Integer suma = 0;
    private static final String path = "database/numeros.csv";
    private static final String containerName = "Main-Container";
    
    public void setup() {
        Location origen = here();
        System.out.println("\n\nHola, agente con nombre local " + getLocalName());
        System.out.println("Y nombre completo... " + getName());
        System.out.println("Y en location " + origen.getID() + "\n\n");
        
        // Para migrar el agente
        try {
            ContainerID destino = new ContainerID(containerName, null);
            System.out.println("Migrando el agente a " + destino.getID());
            doMove(destino);
        } catch (Exception e) {
            System.out.println("\n\n\nNo fue posible migrar el agente\n\n\n");
        }
    }

    // Ejecutado al llegar a un contenedor como resultado de una migracion
    protected void afterMove() {
        Location origen = here();
        System.out.println("\n\nHola, agente migrado con nombre local " + getLocalName());
        System.out.println("Y nombre completo... " + getName());
        System.out.println("Y en location " + origen.getID() + "\n\n");

        try{
            archivo = new File(path);
            lector = new Scanner(archivo);
            
            System.out.println("Contenido del archivo: ");
            while (lector.hasNextLine()) {
                String datos = lector.nextLine();
                
                //Realiza la suma de numeros y no tiene en cuenta las letras.
                try{
                    suma += Integer.parseInt(datos);
                } catch(NumberFormatException e){
                    continue;
                }
                System.out.println(datos);

            }
            System.out.println("\nResultado de la suma: " + suma);
            lector.close();
        } catch (FileNotFoundException e){
            System.out.println("Ocurrio un error");
            e.printStackTrace();
        }
        
    }
}
//Tener en cuenta si me mandan como parametro el container origen.