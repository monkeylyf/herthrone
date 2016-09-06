package com.herthrone.service;

import com.google.common.collect.ImmutableMap;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.HeroConfig;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.stream.Collectors;

public class HerthroneServer {

  private static final Logger logger = Logger.getLogger(HerthroneServer.class.getName());
  private int port = 50051;
  private Server server;

  private void start() throws IOException {
    this.server = ServerBuilder.forPort(port)
        .addService(new HerthroneImpl())
        .build()
        .start();
    logger.info("Herthrone server starts on port " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("Shutting down Herthrone server since JVM is shutting down");
        HerthroneServer.this.stop();
        System.err.println("Server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    final HerthroneServer herthroneServer = new HerthroneServer();
    herthroneServer.start();
    herthroneServer.blockUntilShutdown();
  }

  private class HerthroneImpl extends HerthroneGrpc.HerthroneImplBase {

    public void listHeroes(final ListHeroesRequest request,
                           final StreamObserver<ListHeroesResponse> responseObserver) {
      final ImmutableMap<Enum, HeroConfig> heroConfigs = ConfigLoader.getHeroConfigs();
      final ListHeroesResponse response = ListHeroesResponse.newBuilder()
          .addAllHeroes(heroConfigs.keySet().stream().map(Enum::toString).collect(Collectors.toList()))
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }
}
