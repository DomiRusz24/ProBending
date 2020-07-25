package me.domirusz24.pk.probending.probending.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class WinLoseCommands {
    private static File file;
    private static FileConfiguration config;

    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("ProBending").getDataFolder(), "WinLoseCommands.yml");

        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (IOException ignored) {}
        }

        config = YamlConfiguration.loadConfiguration(file);
        for (ConfigEvents e : ConfigEvents.values()) {
            e.setDefault();
        }
    }

    public static FileConfiguration get() { return config; }

    public static void save() {
        if (file == null) {
            file = new File(Bukkit.getServer().getPluginManager().getPlugin("ProBending").getDataFolder(), "WinLoseCommands.yml");
        }
        try {
            config.save(file);
        }catch (IOException ignored) {}
    }

    public static void reload() {
        if (file == null) {
            file = new File(Bukkit.getServer().getPluginManager().getPlugin("ProBending").getDataFolder(), "WinLoseCommands.yml");
        }
        config = YamlConfiguration.loadConfiguration(file); }
}
