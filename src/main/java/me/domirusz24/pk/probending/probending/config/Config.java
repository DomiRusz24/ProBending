package me.domirusz24.pk.probending.probending.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class Config {
    private FileConfiguration config;
    private File file;

    public abstract File getFile();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void setupConfig() {
        this.file = getFile();
        if (!file.exists()){
            try {
                file.createNewFile();
            }catch (IOException ignored) {}
        }

        config = YamlConfiguration.loadConfiguration(getFile());
    }

    public FileConfiguration getConfig() { return config; }

    public void saveConfig() {
        try {
            config.save(file);
        }catch (IOException ignored) {}
    }

    public void reloadConfig() { config = YamlConfiguration.loadConfiguration(file); }
}
