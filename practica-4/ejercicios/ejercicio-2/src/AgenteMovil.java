import jade.core.*;

import java.io.File;
import java.io.FileNotFoundException; 

import java.util.Scanner;


public class AgenteMovil extends Agent {
    // Ejecutado por unica vez en la creacion
    private Integer suma = 0;
    private String contenedorOrigen, path;

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

    public void realizarSuma(){
        try{
            //Se abre y se lee el archivo.
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

        } catch (FileNotFoundException e){
            System.out.println("Ocurrio un error");
            e.printStackTrace();
        }
    }

    public void setup() {
        Location origen = here();
        Object[] args = getArguments();
        if ((args == null) || (args.length != 2)) {
            System.err.println("Argumentos invalidos, se necesitan 2 argumentos:");
            System.err.println("<containerDestino> <path>");
            System.exit(1);
        }
        String containerName = args[0].toString();
        path = args[1].toString();
        
        //Se guarda el nombre del container origen
        contenedorOrigen = origen.getName();
        System.out.println("\n\nContenedor origen: " + contenedorOrigen  + "\n");
        if(containerName.equals(contenedorOrigen)){
            realizarSuma();
            System.out.println("Resultado de la suma:" + suma + "\n");
        }else{
            migrarAgente(containerName);
        }
    }

    // Ejecutado al llegar a un contenedor como resultado de una migracion
    protected void afterMove() {
        Location actual = here();
        System.out.println("\n\nHola, agente migrado con nombre local " + getLocalName());
        System.out.println("Y nombre completo... " + getName());
        System.out.println("Y en location " + actual.getID() + "\n");

        if(contenedorOrigen.equals(actual.getName())){
            //El container origen imprime el resultado de la suma
            System.out.println("Resultado de la suma:" + suma + "\n");
        }else{
            realizarSuma();
            System.out.println("\nEnviando suma al container origen");
            migrarAgente(contenedorOrigen); 
        }
    }
}
