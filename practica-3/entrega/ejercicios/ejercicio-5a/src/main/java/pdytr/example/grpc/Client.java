package pdytr.example.grpc;

import io.grpc.*;
import java.util.concurrent.TimeUnit;

public class Client
{
    public static void main( String[] args ) throws Exception
    {
      if (args.length != 1) {
        System.err.println("Se necesitan 1 argumentos:");
        System.err.println("Seleccionar accion: [ -time | -ejercicio5a | -ejercicio5b ]");
        System.exit(1);
      }
      
      Integer deadline = new Integer(args[0]);

      // Channel is the abstraction to connect to a service endpoint
      // Let's use plaintext communication because we don't have certs
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
        .usePlaintext(true)
        .build();

      // It is up to the client to determine whether to block the call
      // Here we create a blocking stub, but an async stub,
      // or an async stub with Future are always possible.
      GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
      GreetingServiceOuterClass.HelloRequest request =
        GreetingServiceOuterClass.HelloRequest.newBuilder()
          .setName("Ray")
          .build();

      // Finally, make the call using the stub
      //Tomando tiempo en nanosegundos
      long startTime = System.currentTimeMillis();

      GreetingServiceOuterClass.HelloResponse response = 
        stub.withDeadlineAfter(deadline, TimeUnit.MILLISECONDS).greeting(request);
      
      System.out.println(System.currentTimeMillis() - startTime);

      // A Channel should be shutdown before stopping the process.
      channel.shutdownNow();
      System.exit(0);
    }
}