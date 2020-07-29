package net.leloubil.channitguilds;

import com.google.common.base.CaseFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;

@RequiredArgsConstructor
public class ConfigManager {

    private final ChannitGuildsPlugin channitGuildsPlugin;

    @Getter
    private ChannitGuildsConfig guildsConfig;


    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ChannitGuildsConfig {
        String databaseAddress = "127.0.0.1";
        int databasePort = 3306;
        String databaseName = "database";
        String databaseUserName = "root";
        String databasePassword = "root";
    }

    public void loadConfiguration() {
        if (!channitGuildsPlugin.getDataFolder().exists()) {
            channitGuildsPlugin.getDataFolder().mkdir();
        }

        ChannitGuildsConfig config = new ChannitGuildsConfig();
        FileConfiguration configuration = channitGuildsPlugin.getConfig();
        try {

            for (Field declaredField : config.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                declaredField.set(config, configuration.get(CamelToSnake(declaredField.getName()), declaredField.get(config)));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.guildsConfig = config;
        saveConfiguration();
    }

    private void saveConfiguration() {
        if (!channitGuildsPlugin.getDataFolder().exists()) {
            channitGuildsPlugin.getDataFolder().mkdir();
        }

        FileConfiguration configuration = channitGuildsPlugin.getConfig();
        try {

            for (Field declaredField : guildsConfig.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                configuration.set(CamelToSnake(declaredField.getName()),declaredField.get(guildsConfig));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        channitGuildsPlugin.saveConfig();

    }

    private String CamelToSnake(String other) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, other);
    }

    private String SnakeToCamel(String other) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, other);
    }
}
