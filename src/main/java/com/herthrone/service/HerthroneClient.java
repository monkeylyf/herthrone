package com.herthrone.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class HerthroneClient {
  private static final Logger logger = Logger.getLogger(HerthroneClient.class.getName());

  private final ManagedChannel channel;
  private final HerthroneGrpc.HerthroneBlockingStub blockingStub;

  public HerthroneClient(final String host, final int port) {
    this.channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext(true)
        .build();
    this.blockingStub = HerthroneGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public void listHeroes() {
    final ListHeroesRequest request = ListHeroesRequest.newBuilder().setName("foo").build();
    final ListHeroesResponse response = blockingStub.listHeroes(request);
    logger.info("response: " + response.getHeroesList());
  }

  public static void main(String[] args) {
    final HerthroneClient client = new HerthroneClient("localhost", 50051);

    client.listHeroes();
  }
}
