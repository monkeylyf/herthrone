package com.herthrone.configuration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstHeroPower;
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

  public ConstType getCardTypeByName(final String cardName) {
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

  public static MinionConfig getMinionConfigByName(final ConstMinion minion) {
    return minionConfigLoader.getConfigurations().get(minion.toString());
  }

  public static HeroConfig getHeroConfigByName(final ConstHero hero) {
    return heroConfigLoader.getConfigurations().get(hero.toString());
  }

  public static SpellConfig getSpellConfigByName(final ConstSpell spell) {
    return spellConfigLoader.getConfigurations().get(spell.toString());
  }

  public static SpellConfig getHeroPowerConfigByName(final ConstHeroPower heroPower) {
    return heroPowerConfigLoader.getConfigurations().get(heroPower.toString());
  }

  public static WeaponConfig getWeaponConfigByName(final ConstWeapon weapon) {
    return weaponConfigLoader.getConfigurations().get(weapon.toString());
  }

  private static ResourceBundle loadResource() {
    return ResourceBundle.getBundle("configuration");
  }

  private abstract static class AbstractConfigLoader<T extends BaseConfig> {
    private volatile ImmutableMap<String, T> configs;
    private String configName;

    public AbstractConfigLoader(String configName) {
      this.configName = configName;
    }

    abstract protected T createInstance(Map map);

    private ImmutableMap<String, T> loadConfiguration() {
      List<Object> minions = loadYaml();
      ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();
      for (Object object : minions) {
        Map map = (Map) object;
        T config = createInstance(map);
        builder.put(config.getName().toString(), config);
      }
      return builder.build();
    }

    public synchronized ImmutableMap<String, T> getConfigurations() {
      if (configs == null) {
        configs = loadConfiguration();
      }
      return configs;
    }

    public T getConfigByName(final String name) {
      T config = getConfigurations().get(name);
      Preconditions.checkNotNull(config, String.format("% % not found", this.configName, name));
      return config;
    }

    private List<Object> loadYaml() {
      Yaml yaml = new Yaml();
      final String configPath = String.format(ConfigLoader.pathTemplate, this.configName);
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

  }
}
