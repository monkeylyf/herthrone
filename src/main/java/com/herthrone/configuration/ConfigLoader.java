package com.herthrone.configuration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.herthrone.constant.ConstHero;
import com.herthrone.constant.ConstHeroPower;
import com.herthrone.constant.ConstMinion;
import com.herthrone.constant.ConstSpell;
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

  private static AbstractConfigLoader<MinionConfig> cardConfigLoader = new AbstractConfigLoader<MinionConfig>("minion") {
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

  public static MinionConfig getMinionConfigByName(final ConstMinion minion) throws FileNotFoundException {
    return cardConfigLoader.getConfigurations().get(minion.toString());
  }

  public static HeroConfig getHeroConfigByName(final ConstHero hero) throws FileNotFoundException {
    return heroConfigLoader.getConfigurations().get(hero.toString());
  }

  public static SpellConfig getSpellConfigByName(final ConstSpell spell) throws FileNotFoundException {
    return spellConfigLoader.getConfigurations().get(spell.toString());
  }

  public static SpellConfig getHeroPowerConfigByName(final ConstHeroPower heroPower) throws FileNotFoundException {
    return heroPowerConfigLoader.getConfigurations().get(heroPower.toString());
  }

  public static WeaponConfig getWeaponConfigByName(final ConstWeapon weapon) throws FileNotFoundException {
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

    private ImmutableMap<String, T> loadConfiguration() throws FileNotFoundException {
      List<Object> minions = loadYaml();
      ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();
      for (Object object : minions) {
        Map map = (Map) object;
        T config = createInstance(map);
        builder.put(config.getName().toString(), config);
      }
      return builder.build();
    }

    public synchronized ImmutableMap<String, T> getConfigurations() throws FileNotFoundException {
      if (configs == null) {
        configs = loadConfiguration();
      }
      return configs;
    }

    public T getConfigByName(final String name) throws FileNotFoundException {
      T config = getConfigurations().get(name);
      Preconditions.checkNotNull(config, String.format("% % not found", this.configName, name));
      return config;
    }

    private List<Object> loadYaml() throws FileNotFoundException {
      Yaml yaml = new Yaml();
      final String configPath = String.format(ConfigLoader.pathTemplate, this.configName);
      InputStream input = new FileInputStream(new File(configPath));
      Iterator<Object> iterator = yaml.loadAll(input).iterator();
      return (List) iterator.next();
    }
  }
}
