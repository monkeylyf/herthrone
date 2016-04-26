package com.herthrone.configuration;

import com.google.common.collect.ImmutableMap;
import com.herthrone.exception.HeroNotFoundException;
import com.herthrone.exception.MinionNotFoundException;
import com.herthrone.exception.SpellNotFoundException;
import com.herthrone.exception.WeaponNotFoundException;
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

  private static volatile ImmutableMap<String, SpellConfig> SPELL_CONFIGS;
  private static volatile ImmutableMap<String, MinionConfig> CARD_CONFIGS;
  private static volatile ImmutableMap<String, HeroConfig> HERO_CONFIGS;
  private static volatile ImmutableMap<String, SpellConfig> HERO_POWER_CONFIGS;
  private static volatile ImmutableMap<String, WeaponConfig> WEAPON_CONFIGS;
  private static volatile ResourceBundle RESOURCE;

  private static final String pathTemplate = "src/main/resources/%s.yaml";

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

  public static ImmutableMap<String, MinionConfig> getMinionConfigurations() throws FileNotFoundException {
    ImmutableMap<String, MinionConfig> noneVolatileMinionConfigs = ConfigLoader.CARD_CONFIGS;
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
      MinionConfig config = getMinionConfigurations().get(minionName);
      if (config == null) {
        throw new MinionNotFoundException(String.format("Minion %s not found", minionName));
      } else {
        return config;
      }
  }

  public static ImmutableMap<String, HeroConfig> getHeroConfiguration() throws FileNotFoundException {
    ImmutableMap<String, HeroConfig> noneVolatileHeroConfigs = ConfigLoader.HERO_CONFIGS;
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
    HeroConfig config = getHeroConfiguration().get(heroName);
    if (config == null) {
      throw new HeroNotFoundException(String.format("Hero %s not found", heroName));
    } else {
      return config;
    }
  }

  public static ImmutableMap<String, SpellConfig> getSpellConfiguration() throws FileNotFoundException {
    ImmutableMap<String, SpellConfig>  noneVolatileSpellConfigs = ConfigLoader.SPELL_CONFIGS;
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
    SpellConfig config = getSpellConfiguration().get(spellName);
    if (config == null) {
      throw new SpellNotFoundException(String.format("Spell %s not found", spellName));
    } else {
      return config;
    }
  }

  public static ImmutableMap<String, SpellConfig> getHeroPowerConfiguration() throws FileNotFoundException {
    ImmutableMap<String, SpellConfig> noneVolatileHeroPowerConfigs = ConfigLoader.HERO_POWER_CONFIGS;
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
    SpellConfig config = getHeroPowerConfiguration().get(heroPowerName);
    if (config == null) {
      throw new SpellNotFoundException(String.format("Hero power %s not found", heroPowerName));
    } else {
      return config;
    }
  }

  public static ImmutableMap<String, WeaponConfig> getWeaponConfiguration() throws FileNotFoundException {
    ImmutableMap<String, WeaponConfig> nonVolatileWeaponConfigs = ConfigLoader.WEAPON_CONFIGS;
    if (nonVolatileWeaponConfigs == null) {
      synchronized (ConfigLoader.class) {
        if (nonVolatileWeaponConfigs == null) {
          nonVolatileWeaponConfigs = ConfigLoader.WEAPON_CONFIGS = ConfigLoader.loadWeaponConfiguration();
        }
      }
    }
    return nonVolatileWeaponConfigs;
  }

  public static WeaponConfig getWeaponConfigByName(final String weaponName) throws FileNotFoundException, WeaponNotFoundException {
    WeaponConfig config = getWeaponConfiguration().get(weaponName);
    if (config == null) {
      throw new WeaponNotFoundException(String.format("Weapon %s not found", weaponName));
    } else {
      return config;
    }
  }

  private static ResourceBundle loadResource() {
    return ResourceBundle.getBundle("configuration");
  }

  private static ImmutableMap<String, MinionConfig> loadMinionConfiguration() throws FileNotFoundException {
    List<Object> minions = loadYaml("minion");
    ImmutableMap.Builder<String, MinionConfig> builder = ImmutableMap.builder();
    for(Object object : minions) {
      Map map = (Map) object;
      MinionConfig config = new MinionConfig(map);
      builder.put(config.getName(), config);
    }
    return builder.build();
  }

  private static ImmutableMap<String, HeroConfig> loadHeroConfiguration() throws FileNotFoundException {
    List<Object> heroes = loadYaml("hero");
    ImmutableMap.Builder<String, HeroConfig> builder = ImmutableMap.builder();
    for(Object object : heroes) {
      Map map = (Map) object;
      HeroConfig config = new HeroConfig(map);
      builder.put(config.getName(), config);
    }
    return builder.build();
  }

  private static ImmutableMap<String, SpellConfig> loadHeroPowerConfiguration() throws FileNotFoundException {
    List<Object> heroPowers = loadYaml("hero_power");
    ImmutableMap.Builder<String, SpellConfig> builder = ImmutableMap.builder();
    for(Object object : heroPowers) {
      Map map = (Map) object;
      SpellConfig config = new SpellConfig(map);
      builder.put(config.getName(), config);
    }
    return builder.build();
  }

  private static ImmutableMap<String, SpellConfig> loadSpellConfiguration() throws FileNotFoundException {
    List<Object> heroPowers = loadYaml("spell");
    ImmutableMap.Builder<String, SpellConfig> builder = ImmutableMap.builder();
    for (Object object : heroPowers) {
      Map map = (Map) object;
      SpellConfig config = new SpellConfig(map);
      builder.put(config.getName(), config);
    }
    return builder.build();
  }

  private static ImmutableMap<String, WeaponConfig> loadWeaponConfiguration() throws FileNotFoundException {
    List<Object> weapons = loadYaml("weapon");
    ImmutableMap.Builder<String, WeaponConfig> builder = ImmutableMap.builder();
    for (Object object : weapons) {
      Map map = (Map) object;
      WeaponConfig config = new WeaponConfig(map);
      builder.put(config.getName(), config);
    }
    return builder.build();
  }

  private static List<Object> loadYaml(final String configSignature) throws FileNotFoundException {
    Yaml yaml = new Yaml();
    final String configPath = String.format(ConfigLoader.pathTemplate, configSignature);
    InputStream input = new FileInputStream(new File(configPath));
    Iterator<Object> iterator = yaml.loadAll(input).iterator();
    return (List) iterator.next();
  }

}
