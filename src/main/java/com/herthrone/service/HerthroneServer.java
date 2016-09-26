package com.herthrone.service;

import com.google.common.collect.ImmutableMap;
import com.herthrone.configuration.ConfigLoader;
import com.herthrone.configuration.HeroConfig;
import com.herthrone.configuration.MinionConfig;
import com.herthrone.configuration.SpellConfig;
import com.herthrone.configuration.WeaponConfig;
import com.herthrone.constant.ConstClass;
import com.herthrone.game.Game;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
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

    @Override
    public void listHeroes(final ListHeroesRequest request,
                           final StreamObserver<ListHeroesResponse> responseObserver) {
      final ImmutableMap<Enum, HeroConfig> heroConfigs = ConfigLoader.getHeroConfigs();
      final Collection<Hero> heroes = heroConfigs.values().stream()
          .map(HeroConfig::toHeroProto)
          .collect(Collectors.toList());
      final ListHeroesResponse response = ListHeroesResponse.newBuilder()
          .addAllHeroes(heroes)
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void listCards(final ListRequest request,
                          final StreamObserver<ListCardsResponse> responseObserver) {
      final ConstClass requestClass = ConstClass.valueOf(request.getClassType());
      final ListCardsResponse response = ListCardsResponse.newBuilder()
          .addAllMinions(ConfigLoader.getMinionConfigsByClass(requestClass).stream()
              .map(MinionConfig::toMinionProto)
              .collect(Collectors.toList()))
          .addAllSpells(ConfigLoader.getSpellConfigsByClass(requestClass).stream()
              .map(SpellConfig::toSpellProto)
              .collect(Collectors.toList()))
          .addAllWeapons(ConfigLoader.getWeaponConfigsByClass(requestClass).stream()
              .map(WeaponConfig::toWeaponProto)
              .collect(Collectors.toList()))
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void listMinions(final ListRequest request,
                            final StreamObserver<ListMinionsResponse> responseObserver) {
      final ConstClass requestClass = ConstClass.valueOf(request.getClassType());
      final List<Minion> minionProtos = ConfigLoader.getMinionConfigsByClass(requestClass).stream()
          .map(MinionConfig::toMinionProto)
          .collect(Collectors.toList());
      final ListMinionsResponse response = ListMinionsResponse.newBuilder()
          .addAllMinions(minionProtos)
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void listSpells(final ListRequest request,
                           final StreamObserver<ListSpellsResponse> responseObserver) {
      final ConstClass requestClass = ConstClass.valueOf(request.getClassType());
      final Collection<Spell> spellProtos = ConfigLoader.getSpellConfigsByClass(requestClass).stream()
          .map(SpellConfig::toSpellProto)
          .collect(Collectors.toList());
      final ListSpellsResponse response = ListSpellsResponse.newBuilder()
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void listWeapons(final ListRequest request,
                            final StreamObserver<ListWeaponsResponse> responseObserver) {
      final ConstClass requestClass = ConstClass.valueOf(request.getClassType());
      final Collection<Weapon> weaponProtos = ConfigLoader.getWeaponConfigsByClass(requestClass).stream()
          .map(WeaponConfig::toWeaponProto)
          .collect(Collectors.toList());
      final ListWeaponsResponse response = ListWeaponsResponse.newBuilder()
          .addAllWeapons(weaponProtos)
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }

    @Override
    public void startGame(final StartGameRequest request,
                          final StreamObserver<StartGameResponse> responseObserver) {
      final String gameId = Game.StartGame(request.getGameSettingsList());
      final StartGameResponse startGameResponse = StartGameResponse.newBuilder()
          .setGameId(gameId)
          .build();
      responseObserver.onNext(startGameResponse);
      responseObserver.onCompleted();
    }
  }

}
