package me.domirusz24.pk.probending.probending.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DataConfig {
    private static File file;
    private static FileConfiguration config;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("ProBending").getDataFolder(), "Data/PlayerData.yml");

        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (IOException ignored) {}
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() { return config; }

    public static void save() {
        try {
            config.save(file);
        }catch (IOException ignored) {}
    }

    public static void reload() { config = YamlConfiguration.loadConfiguration(file); }
}
