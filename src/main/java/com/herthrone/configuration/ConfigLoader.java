package com.herthrone.configuration;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.herthrone.constant.ConstClass;
import com.herthrone.constant.ConstHero;
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

  static <T> T getByDefault(final Map map, final String key, final T defaultValue) {
    @SuppressWarnings("unchecked") final T value =  (map.containsKey(key)) ?
        (T) map.get(key) : defaultValue;
    return value;
  }

  static String getUpperCaseStringValue(final Map map, final String key) {
    final String value = (String) map.get(key);
    Preconditions.checkNotNull(value, "Map %s does not contain key %s", map, key);
    return value.toUpperCase();
  }

  static Objects.ToStringHelper addIfConditionIsTrue(final boolean condition,
                                                     final Objects.ToStringHelper toStringHelper,
                                                     final String key, final Object object) {
    if (condition) {
      toStringHelper.add(key, object.toString());
    }
    return toStringHelper;
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

  private abstract static class AbstractConfigLoader<T extends AbstractConfig> {
    private volatile ImmutableMap<Enum, T> configs;
    private String configName;

    public AbstractConfigLoader(final String configName) {
      this.configName = configName;
    }

    public synchronized ImmutableMap<Enum, T> getConfigurations() {
      if (configs == null) {
        configs = loadConfiguration();
      }
      return configs;
    }

    private ImmutableMap<Enum, T> loadConfiguration() {
      final List<Object> configSection = loadYaml();
      final ImmutableMap.Builder<Enum, T> builder = ImmutableMap.builder();
      for (final Object object : configSection) {
        final Map map = (Map) object;
        final T config = createInstance(map);
        builder.put(config.name, config);
      }
      return builder.build();
    }

    private List<Object> loadYaml() {
      final Yaml yaml = new Yaml();
      final String configPath = String.format(ConfigLoader.pathTemplate, configName);
      final InputStream input;
      try {
        input = new FileInputStream(new File(configPath));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        throw new RuntimeException("Configuration file not found: " + configPath);
      }
      final Iterator<Object> iterator = yaml.loadAll(input).iterator();
      @SuppressWarnings("unchecked") List<Object> configurationSections = (List) iterator.next();
      return configurationSections;
    }

    abstract protected T createInstance(Map map);

  }

  abstract static class AbstractConfig<E extends Enum<E>> {

    private static final String NAME = "name";
    private static final String CLASS = "class";
    private static final String DESCRIPTION = "description";
    private static final String DISPLAY = "display";
    private static final String CRYSTAL = "crystal";
    private static final String COLLECTIBLE = "collectible";

    public final E name;
    public final String displayName;
    public final ConstClass className;
    public final String description;
    public final int crystal;
    public final boolean isCollectible;

    protected AbstractConfig(final Map map) {
      this.name = loadName((String) map.get(NAME));
      this.displayName = getByDefault(map, DISPLAY, lowerUnderscoreToUpperWhitespace(name));
      this.className = ConstClass.valueOf(getUpperCaseStringValue(map, CLASS));
      this.description = (String) map.get(DESCRIPTION);
      this.crystal = getByDefault(map, CRYSTAL, 0);
      this.isCollectible = getByDefault(map, COLLECTIBLE, true);
    }

    protected abstract E loadName(final String name);

    private String lowerUnderscoreToUpperWhitespace(final Enum name) {
      return CaseFormat.UPPER_CAMEL
          .to(CaseFormat.UPPER_UNDERSCORE, name.toString())
          .replaceAll(" ", "_");
    }
  }
}
