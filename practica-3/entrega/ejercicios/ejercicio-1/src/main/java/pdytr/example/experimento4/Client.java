package pdytr.example.experimento4;

import io.grpc.*;
import pdytr.example.experimentos.*;

import io.grpc.stub.StreamObserver;

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
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
        GreetingServiceOuterClass.HelloRequest request =
            GreetingServiceOuterClass.HelloRequest.newBuilder()
            .setName("Rami")
            .build();

        StreamObserver<GreetingServiceOuterClass.HelloResponse> responseObserver = new StreamObserver<GreetingServiceOuterClass.HelloResponse>() {
            @Override
            public void onNext(GreetingServiceOuterClass.HelloResponse response) {
                System.out.println(response);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError");
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }
        };

        // Finally, make the call using the stub
        stub.greeting(request, responseObserver);

        System.out.println("En 2 segundos se apaga el cliente");
        Thread.sleep(2000);

        // A Channel should be shutdown before stopping the process.
        channel.shutdownNow();
        System.exit(0);
    }
}