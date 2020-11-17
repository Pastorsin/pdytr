import jade.core.*;

import java.io.File;
import java.io.FileNotFoundException; 

import java.util.Scanner;


public class AgenteMovil extends Agent {
    // Ejecutado por unica vez en la creacion
    private Integer suma = 0;
    private String idOrigen, contenedorOrigen;
    private static final String path = "database/numeros.csv";
    
    public void migrarAgente(String container){
        // Migra el agente al container cuyo nombre llega por parametro
        try {
            ContainerID destino = new ContainerID(container, null);
            System.out.println("Migrando el agente a " + destino.getID());
            doMove(destino);
        } catch (Exception e) {
            System.out.println("\n\n\nNo fue posible migrar el agente\n\n\n");
        }

    }

    public void setup() {
        Location origen = here();

        //Se guarda el nombre del container origen
        idOrigen = origen.getID() ;
        contenedorOrigen = (idOrigen).split("@")[0];
        System.out.println("\n\nContenedor origen: " + contenedorOrigen  + "\n");
        migrarAgente("Main-Container");
    }

    // Ejecutado al llegar a un contenedor como resultado de una migracion
    protected void afterMove() {
        Location origen = here();
        System.out.println("\n\nHola, agente migrado con nombre local " + getLocalName());
        System.out.println("Y nombre completo... " + getName());
        System.out.println("Y en location " + origen.getID() + "\n");

        if(idOrigen.equals(origen.getID())){
            //El container origen imprime el resultado de la suma
            System.out.println("Resultado de la suma:" + suma + "\n");
        }else{
            //Se abre y se lee el archivo.
            try{
                File archivo = new File(path);
                Scanner lector = new Scanner(archivo);
                
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
                lector.close();
                
                System.out.println("\nEnviando suma al container origen");
                migrarAgente(contenedorOrigen);

            } catch (FileNotFoundException e){
                System.out.println("Ocurrio un error");
                e.printStackTrace();
            }
        }    
    }
}
//Tener en cuenta si me mandan como parametro el container origen.