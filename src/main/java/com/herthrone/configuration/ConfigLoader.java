package com.herthrone.configuration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
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

  public static MinionConfig getMinionConfigByName(final String minionName) throws FileNotFoundException {
    return cardConfigLoader.getConfigurations().get(minionName);
  }

  public static HeroConfig getHeroConfigByName(final String heroName) throws FileNotFoundException {
    return heroConfigLoader.getConfigurations().get(heroName);
  }

  public static SpellConfig getSpellConfigByName(final String spellName) throws FileNotFoundException {
    return spellConfigLoader.getConfigurations().get(spellName);
  }

  public static SpellConfig getHeroPowerConfigByName(final String heroPowerName) throws FileNotFoundException {
    return heroPowerConfigLoader.getConfigurations().get(heroPowerName);
  }

  public static WeaponConfig getWeaponConfigByName(final String weaponName) throws FileNotFoundException {
    return weaponConfigLoader.getConfigurations().get(weaponName);
  }

  private static ResourceBundle loadResource() {
    return ResourceBundle.getBundle("configuration");
  }

  private static List<Object> loadYaml(final String configSignature) throws FileNotFoundException {
    Yaml yaml = new Yaml();
    final String configPath = String.format(ConfigLoader.pathTemplate, configSignature);
    InputStream input = new FileInputStream(new File(configPath));
    Iterator<Object> iterator = yaml.loadAll(input).iterator();
    return (List) iterator.next();
  }

  private abstract static class AbstractConfigLoader<T extends BaseConfig> {
    private volatile ImmutableMap<String, T> configs;
    private String configName;

    public AbstractConfigLoader(String configName) {
      this.configName = configName;
    }

    abstract protected T createInstance(Map map);

    private ImmutableMap<String, T> loadConfiguration() throws FileNotFoundException {
      List<Object> minions = loadYaml(configName);
      ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();
      for (Object object : minions) {
        Map map = (Map) object;
        T config = createInstance(map);
        builder.put(config.getName(), config);
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

  }
}
