package com.herthrone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.yaml.snakeyaml.Yaml;

/**
 * Created by yifeng on 4/9/16.
 */
public class ConfigLoader {

  private static volatile List<CardConfig> CARD_CONFIGS;
  private static volatile ResourceBundle RESOURCE;

  public static void main(String[] args) throws FileNotFoundException {
    List<CardConfig> configs = loadCards();
    for (CardConfig card : configs) {
      System.out.println(card.getId() + " " + card);
      System.out.println(card.getMechanics());
    }
  }

  public static ResourceBundle getResource() {
    ResourceBundle noneVolatileResource = ConfigLoader.RESOURCE;
    if (noneVolatileResource == null) {
      synchronized (ConfigLoader.class) {
        noneVolatileResource = ConfigLoader.RESOURCE;
        if (noneVolatileResource == null) {
          noneVolatileResource = ConfigLoader.RESOURCE = loadResource();
        }
      }
    }
    return noneVolatileResource;
  }

  public static List<CardConfig> getCardConfigs() throws FileNotFoundException {
    List<CardConfig> noneVolatileCardConfigs = ConfigLoader.CARD_CONFIGS;
    if (noneVolatileCardConfigs == null) {
      synchronized (ConfigLoader.class) {
        noneVolatileCardConfigs = ConfigLoader.CARD_CONFIGS;
        if (noneVolatileCardConfigs == null) {
          noneVolatileCardConfigs = ConfigLoader.CARD_CONFIGS = ConfigLoader.loadCards();
        }
      }
    }
    return noneVolatileCardConfigs;
  }

  public static class CardConfig {

    private final int id;
    private final String name;
    private final int attack;
    private final int health;
    private final int crystal;
    private final List<String> mechanics;

    public CardConfig(final Map map) {
      this.id = (int) map.get("id");
      this.name = (String) map.get("name");
      this.attack = (int) map.get("attack");
      this.health = (int) map.get("health");
      this.crystal = (int) map.get("crystal");
      this.mechanics = (List<String>) map.get("mechanics");
    }

    public String toString() { return this.name; }

    public int getId() { return this.id; }
    public String getName() { return this.name; }
    public int getAttack() { return this.attack; }
    public int getHealth() { return this.health; }
    public int getCrystal() { return this.crystal; }
    public List<String> getMechanics() { return this.mechanics; }

  }

  private static ResourceBundle loadResource() {
    return ResourceBundle.getBundle("configuration");
  }

  private static List<CardConfig> loadCards() throws FileNotFoundException {
    Yaml yaml = new Yaml();
    InputStream input = new FileInputStream(new File("src/main/resources/card.yaml"));
    List<CardConfig> cardConfigs = new ArrayList<>();
    for(Object object : yaml.loadAll(input)) {
      Map map = (Map) object;
      cardConfigs.add(new CardConfig(map));
    }
    return cardConfigs;
  }
}
