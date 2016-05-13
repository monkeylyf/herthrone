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
            
  private abstract static class AbstractConfigLoader<T extends BaseConfig> {
      private volatile ImmutableMap<String, T> configs;
      private String configName;
      
      abstract protected T createInstance(Map map);
      
      public AbstractConfigLoader(String configName) {
          this.configName = configName;
      }
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

  public static MinionConfig getMinionConfigByName(final String minionName) throws FileNotFoundException, MinionNotFoundException {
    MinionConfig config = cardConfigLoader.getConfigurations().get(minionName);
    if (config == null) {
      throw new MinionNotFoundException(String.format("Minion %s not found", minionName));
    } else {
      return config;
    }
  }

  public static HeroConfig getHeroConfigByName(final String heroName) throws FileNotFoundException, HeroNotFoundException {
    HeroConfig config = heroConfigLoader.getConfigurations().get(heroName);
    if (config == null) {
      throw new HeroNotFoundException(String.format("Hero %s not found", heroName));
    } else {
      return config;
    }
  }

  public static SpellConfig getSpellConfigByName(final String spellName) throws FileNotFoundException, SpellNotFoundException {
    SpellConfig config = spellConfigLoader.getConfigurations().get(spellName);
    if (config == null) {
      throw new SpellNotFoundException(String.format("Spell %s not found", spellName));
    } else {
      return config;
    }
  }

  public static SpellConfig getHeroPowerConfigByName(final String heroPowerName) throws FileNotFoundException, SpellNotFoundException {
    SpellConfig config = heroPowerConfigLoader.getConfigurations().get(heroPowerName);
    if (config == null) {
      throw new SpellNotFoundException(String.format("Hero power %s not found", heroPowerName));
    } else {
      return config;
    }
  }

  public static WeaponConfig getWeaponConfigByName(final String weaponName) throws FileNotFoundException, WeaponNotFoundException {
    WeaponConfig config = weaponConfigLoader.getConfigurations().get(weaponName);
    if (config == null) {
      throw new WeaponNotFoundException(String.format("Weapon %s not found", weaponName));
    } else {
      return config;
    }
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

}
