package com.herthrone.configuration;

import com.herthrone.exception.HeroNotFoundException;
import com.herthrone.exception.MinionNotFoundException;
import com.herthrone.exception.SpellNotFoundException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by yifeng on 4/9/16.
 */
public class ConfigLoader {

  private static volatile Map<String, SpellConfig> SPELL_CONFIGS;
  private static volatile Map<String, MinionConfig> CARD_CONFIGS;
  private static volatile Map<String, HeroConfig> HERO_CONFIGS;
  private static volatile Map<String, SpellConfig> HERO_POWER_CONFIGS;
  private static volatile ResourceBundle RESOURCE;

  private static final String pathTemplate = "src/main/resources/%s.yaml";

  public static void main(String[] args) throws FileNotFoundException {
    for (Map.Entry<String, SpellConfig> entry : loadSpellConfiguration().entrySet()) {
      System.out.println(entry.getKey());
      System.out.println(entry.getValue().getEffects().size());
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

  public static Map<String, MinionConfig> getMinionConfigurations() throws FileNotFoundException {
    Map<String, MinionConfig> noneVolatileMinionConfigs = ConfigLoader.CARD_CONFIGS;
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
      MinionConfig config = loadMinionConfiguration().get(minionName);
      if (config == null) {
        throw new MinionNotFoundException(String.format("Minion %s not found", minionName));
      } else {
        return config;
      }
  }

  public static Map<String, HeroConfig> getHeroConfiguration() throws FileNotFoundException {
    Map<String, HeroConfig> noneVolatileHeroConfigs = ConfigLoader.HERO_CONFIGS;
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
    HeroConfig config = loadHeroConfiguration().get(heroName);
    if (config == null) {
      throw new HeroNotFoundException(String.format("Hero %s not found", heroName));
    } else {
      return config;
    }
  }

  public static Map<String, SpellConfig> getSpellConfiguration() throws FileNotFoundException {
    Map<String, SpellConfig>  noneVolatileSpellConfigs = ConfigLoader.SPELL_CONFIGS;
    if (noneVolatileSpellConfigs == null) {
      synchronized (ConfigLoader.class) {
        if (noneVolatileSpellConfigs == null) {
          noneVolatileSpellConfigs = ConfigLoader.SPELL_CONFIGS = ConfigLoader.loadSpellConfiguration();
        }
      }
    }
    return noneVolatileSpellConfigs;
  }

  public static SpellConfig getSpellConfigByName(final String spellName) throws FileNotFoundException, SpellNotFoundException {
    SpellConfig config = loadSpellConfiguration().get(spellName);
    if (config == null) {
      throw new SpellNotFoundException(String.format("Spell %s not found", spellName));
    } else {
      return config;
    }
  }

  public static Map<String, SpellConfig> getHeroPowerConfiguration() throws FileNotFoundException {
    Map<String, SpellConfig>  noneVolatileHeroPowerConfigs = ConfigLoader.HERO_POWER_CONFIGS;
    if (noneVolatileHeroPowerConfigs == null) {
      synchronized (ConfigLoader.class) {
        if (noneVolatileHeroPowerConfigs == null) {
          noneVolatileHeroPowerConfigs = ConfigLoader.HERO_POWER_CONFIGS = ConfigLoader.loadHeroPowerConfiguration();
        }
      }
    }
    return noneVolatileHeroPowerConfigs;
  }

  public static SpellConfig getHeroPowerConfigByName(final String heroPowerName) throws FileNotFoundException, SpellNotFoundException {
    SpellConfig config = loadHeroPowerConfiguration().get(heroPowerName);
    if (config == null) {
      throw new SpellNotFoundException(String.format("Hero power %s not found", heroPowerName));
    } else {
      return config;
    }
  }

  private static ResourceBundle loadResource() {
    return ResourceBundle.getBundle("configuration");
  }

  private static Map<String, MinionConfig> loadMinionConfiguration() throws FileNotFoundException {
    List<Object> minions = loadYaml("minion");
    Map<String, MinionConfig> cardConfigs = new HashMap<>();
    for(Object object : minions) {
      Map map = (Map) object;
      MinionConfig config = new MinionConfig(map);
      cardConfigs.put(config.getName(), config);
    }
    return cardConfigs;
  }

  private static Map<String, HeroConfig> loadHeroConfiguration() throws FileNotFoundException {
    List<Object> heroes = loadYaml("hero");
    Map<String, HeroConfig> heroConfigs = new HashMap<>();
    for(Object object : heroes) {
      Map map = (Map) object;
      HeroConfig config = new HeroConfig(map);
      heroConfigs.put(config.getName(), config);
    }
    return heroConfigs;
  }

  private static Map<String, SpellConfig> loadHeroPowerConfiguration() throws FileNotFoundException {
    List<Object> heroPowers = loadYaml("hero_power");
    Map<String, SpellConfig> heroPowerConfigs = new HashMap<>();
    for(Object object : heroPowers) {
      Map map = (Map) object;
      SpellConfig config = new SpellConfig(map);
      heroPowerConfigs.put(config.getName(), config);
    }
    return heroPowerConfigs;
  }

  private static Map<String, SpellConfig> loadSpellConfiguration() throws FileNotFoundException {
    List<Object> heroPowers = loadYaml("spell");
    Map<String, SpellConfig> heroPowerConfigs = new HashMap<>();
    for (Object object : heroPowers) {
      Map map = (Map) object;
      SpellConfig config = new SpellConfig(map);
      heroPowerConfigs.put(config.getName(), config);
    }
    return heroPowerConfigs;
  }

  private static List<Object> loadYaml(final String configSignature) throws FileNotFoundException {
    Yaml yaml = new Yaml();
    final String configPath = String.format(ConfigLoader.pathTemplate, configSignature);
    InputStream input = new FileInputStream(new File(configPath));
    Iterator<Object> iterator = yaml.loadAll(input).iterator();
    return (List) iterator.next();
  }
}
