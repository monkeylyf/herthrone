package com.herthrone.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import com.herthrone.exception.ConfigNotFoundException;
import com.herthrone.exception.HeroNotFoundException;
import com.herthrone.exception.MinionNotFoundException;
import org.yaml.snakeyaml.Yaml;

/**
 * Created by yifeng on 4/9/16.
 */
public class ConfigLoader {

  private static volatile List<MinionConfig> CARD_CONFIGS;
  private static volatile List<HeroConfig> HERO_CONFIGS;
  private static volatile ResourceBundle RESOURCE;

  private static final String pathTemplate = "src/main/resources/%s.yml";

  public static void main(String[] args) throws FileNotFoundException {
    List<MinionConfig> configs = loadMinionConfiguration();
    for (MinionConfig card : configs) {
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

  public static List<MinionConfig> getMinionConfigurations() throws FileNotFoundException {
    List<MinionConfig> noneVolatileMinionConfigs = ConfigLoader.CARD_CONFIGS;
    if (noneVolatileMinionConfigs == null) {
      synchronized (ConfigLoader.class) {
        noneVolatileMinionConfigs = ConfigLoader.CARD_CONFIGS;
        if (noneVolatileMinionConfigs == null) {
          noneVolatileMinionConfigs = ConfigLoader.CARD_CONFIGS = ConfigLoader.loadMinionConfiguration();
        }
      }
    }
    return noneVolatileMinionConfigs;
  }

  public static MinionConfig getMinionConfigByName(final String minionName) throws FileNotFoundException, MinionNotFoundException {
    for (MinionConfig config : getMinionConfigurations()) {
      if (config.getName().equals(minionName)) {
        return config;
      }
    }
    throw new MinionNotFoundException(String.format("Minion %s not found", minionName));
  }

  public static List<HeroConfig> getHeroConfiguration() throws FileNotFoundException {
    List<HeroConfig> noneVolatileHeroConfigs = ConfigLoader.HERO_CONFIGS;
    if (noneVolatileHeroConfigs == null) {
      synchronized (ConfigLoader.class) {
        noneVolatileHeroConfigs = ConfigLoader.HERO_CONFIGS;
        if (noneVolatileHeroConfigs == null) {
          noneVolatileHeroConfigs = ConfigLoader.HERO_CONFIGS = ConfigLoader.loadHeroConfiguration();
        }
      }
    }
    return noneVolatileHeroConfigs;
  }

  public static HeroConfig getHeroConfigByName(final String heroName) throws FileNotFoundException, HeroNotFoundException {
    for (HeroConfig heroConfig : getHeroConfiguration()) {
      if (heroConfig.getName().equals(heroName)) {
        return heroConfig;
      }
    }

    throw new HeroNotFoundException(String.format("Hero %s not found", heroName));
  }

  private static ResourceBundle loadResource() {
    return ResourceBundle.getBundle("configuration");
  }

  private static List<MinionConfig> loadMinionConfiguration() throws FileNotFoundException {
    Yaml yaml = new Yaml();
    final String configPath = String.format(ConfigLoader.pathTemplate, "minion");
    InputStream input = new FileInputStream(new File(configPath));
    List<MinionConfig> cardConfigs = new ArrayList<>();
    for(Object object : yaml.loadAll(input)) {
      Map map = (Map) object;
      cardConfigs.add(new MinionConfig(map));
    }
    return cardConfigs;
  }

  private static List<HeroConfig> loadHeroConfiguration() throws FileNotFoundException {
    Yaml yaml = new Yaml();
    final String configPath = String.format(ConfigLoader.pathTemplate, "hero");
    InputStream input = new FileInputStream(new File(configPath));
    List<HeroConfig> heroConfigs = new ArrayList<>();
    for(Object object : yaml.loadAll(input)) {
      Map map = (Map) object;
      heroConfigs.add(new HeroConfig(map));
    }
    return heroConfigs;
  }

}
