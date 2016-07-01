package com.herthrone.configuration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.herthrone.base.Config;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
import com.herthrone.constant.ConstType;
import com.herthrone.constant.ConstWeapon;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by yifeng on 4/9/16.
 */
public class ConfigLoader {

  private static final String pathTemplate = "src/main/resources/%s.yaml";
  private static volatile ResourceBundle RESOURCE;

  private static AbstractConfigLoader<SpellConfig> spellConfigLoader = new AbstractConfigLoader<SpellConfig>("spell") {
    @Override
    protected SpellConfig createInstance(Map map) {
      return new SpellConfig(map);
    }
  };

  private static AbstractConfigLoader<MinionConfig> minionConfigLoader = new AbstractConfigLoader<MinionConfig>("minion") {
    @Override
    protected MinionConfig createInstance(Map map) {
      return new MinionConfig(map);
    }
  };

  private static AbstractConfigLoader<HeroConfig> heroConfigLoader = new AbstractConfigLoader<HeroConfig>("hero") {
    @Override
    protected HeroConfig createInstance(Map map) {
      return new HeroConfig(map);
    }
  };

  private static AbstractConfigLoader<SpellConfig> heroPowerConfigLoader = new AbstractConfigLoader<SpellConfig>("hero_power") {
    @Override
    protected SpellConfig createInstance(Map map) {
      return new SpellConfig(map);
    }
  };

  private static AbstractConfigLoader<WeaponConfig> weaponConfigLoader = new AbstractConfigLoader<WeaponConfig>("weapon") {
    @Override
    protected WeaponConfig createInstance(Map map) {
      return new WeaponConfig(map);
    }
  };

  public static ConstType getCardTypeByName(final Enum cardName) {
    if (spellConfigLoader.getConfigurations().containsKey(cardName)) {
      return ConstType.SPELL;
    } else if (minionConfigLoader.getConfigurations().containsKey(cardName)) {
      return ConstType.MINION;
    } else if (heroConfigLoader.getConfigurations().containsKey(cardName)) {
      return ConstType.HERO;
    } else if (heroPowerConfigLoader.getConfigurations().containsKey(cardName)) {
      return ConstType.HERO_POWER;
    } else if (weaponConfigLoader.getConfigurations().containsKey(cardName)) {
      return ConstType.WEAPON;
    } else {
      throw new IllegalArgumentException("Unknown card name: " + cardName);
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

  private static ResourceBundle loadResource() {
    return ResourceBundle.getBundle("configuration");
  }

  public static MinionConfig getMinionConfigByName(final ConstMinion minion) {
    return minionConfigLoader.getConfigurations().get(minion);
  }

  public static HeroConfig getHeroConfigByName(final ConstHero hero) {
    return heroConfigLoader.getConfigurations().get(hero);
  }

  public static SpellConfig getSpellConfigByName(final ConstSpell spell) {
    return spellConfigLoader.getConfigurations().get(spell);
  }

  public static SpellConfig getHeroPowerConfigByName(final ConstSpell heroPower) {
    return heroPowerConfigLoader.getConfigurations().get(heroPower);
  }

  public static WeaponConfig getWeaponConfigByName(final ConstWeapon weapon) {
    return weaponConfigLoader.getConfigurations().get(weapon);
  }

  private abstract static class AbstractConfigLoader<T extends Config> {
    private volatile ImmutableMap<Enum, T> configs;
    private String configName;

    public AbstractConfigLoader(final String configName) {
      this.configName = configName;
    }

    public T getConfigByName(final String name) {
      T config = getConfigurations().get(name);
      Preconditions.checkNotNull(config, String.format("% % not found", configName, name));
      return config;
    }

    public synchronized ImmutableMap<Enum, T> getConfigurations() {
      if (configs == null) {
        configs = loadConfiguration();
      }
      return configs;
    }

    private ImmutableMap<Enum, T> loadConfiguration() {
      List<Object> configSection = loadYaml();
      ImmutableMap.Builder<Enum, T> builder = ImmutableMap.builder();
      for (Object object : configSection) {
        final Map map = (Map) object;
        final T config = createInstance(map);
        builder.put(config.name(), config);
      }
      return builder.build();
    }

    private List<Object> loadYaml() {
      Yaml yaml = new Yaml();
      final String configPath = String.format(ConfigLoader.pathTemplate, configName);
      InputStream input = null;
      try {
        input = new FileInputStream(new File(configPath));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        throw new RuntimeException("Configuration file not found: " + configPath);
      }
      Iterator<Object> iterator = yaml.loadAll(input).iterator();
      return (List) iterator.next();
    }

    abstract protected T createInstance(Map map);

  }
}
