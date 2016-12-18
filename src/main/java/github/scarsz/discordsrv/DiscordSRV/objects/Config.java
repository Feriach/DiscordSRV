package github.scarsz.discordsrv.DiscordSRV.objects;

import github.scarsz.discordsrv.DiscordSRV.Manager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @at 11/7/2016
 */
@SuppressWarnings({"unused", "unchecked"})
public class Config {

    private final Map<String, Object> config = new HashMap<>();
    private final Map<String, Object> defaultConfig = new HashMap<>();
    public File configFile = null;

    public void initialize() {
        try {
            // load default config values
            ((Map<String, Object>) Manager.getInstance().getYaml().load(Manager.getInstance().getPlatform().getResourceAsString("config.yml"))).entrySet().forEach(entry -> defaultConfig.put(entry.getKey(), entry.getValue()));
            System.out.print("Default config: " + defaultConfig);

            // load actual config files
            ((Map<String, Object>) Manager.getInstance().getYaml().load(FileUtils.readFileToString(configFile, Charset.defaultCharset()))).entrySet().forEach(entry -> defaultConfig.put(entry.getKey(), entry.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void save() {
        if (configFile == null) throw new NullPointerException("Config file is null. Can't save.");
        try {
            //TODO properly(?) save
            FileUtils.writeStringToFile(configFile, Manager.getInstance().getYaml().dump(config), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, Object value) {
        config.put(key, value);
    }

    public boolean getBoolean(String key) {
        return (boolean) config.getOrDefault(key, defaultConfig.get(key));
    }
    public Set<Map.Entry<String, Object>> getEntrySet() {
        return config.entrySet();
    }
    public int getInt(String key) {
        return (int) config.getOrDefault(key, defaultConfig.get(key));
    }
    public String getString(String key) {
        return (String) config.getOrDefault(key, defaultConfig.get(key));
    }
    public List<String> getStringList(String key) {
        return (List<String>) config.getOrDefault(key, defaultConfig.get(key));
    }

}
