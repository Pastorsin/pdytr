package pdytr.example.experimento3;

import io.grpc.*;
import java.util.concurrent.TimeUnit;
import pdytr.example.experimentos.*;


public class Client {
    public static void main( String[] args ) throws Exception {
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
            .setName("Rami")
            .build();

        Runnable myRunnable = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);

                    channel.shutdownNow();

                    System.out.println("Cliente cerrado");
                    System.exit(1);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(myRunnable).start();

        // Finally, make the call using the stub
        GreetingServiceOuterClass.HelloResponse response = stub.greeting(request);

        System.out.println(response);

        // A Channel should be shutdown before stopping the process.
        channel.shutdownNow();
    }
}