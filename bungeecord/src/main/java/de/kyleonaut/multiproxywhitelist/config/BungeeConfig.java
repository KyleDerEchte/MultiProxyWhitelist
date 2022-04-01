package de.kyleonaut.multiproxywhitelist.config;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author kyleonaut
 */
public class BungeeConfig {
    private final Plugin plugin;
    @Getter
    private ConfigData configData;

    public BungeeConfig(Plugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        final File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        final File config = new File(dataFolder.getPath() + "/config.json");
        if (!config.exists()) {
            try {
                config.createNewFile();
                final URL url = getClass().getClassLoader().getResource("config.json");
                if (url == null) {
                    return;
                }
                final ObjectMapper objectMapper = new ObjectMapper();
                final ConfigData configData = objectMapper.readValue(url, ConfigData.class);
                objectMapper.writer(new DefaultPrettyPrinter()).writeValue(config, configData);
                this.configData = configData;
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            this.configData = objectMapper.readValue(config, ConfigData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
