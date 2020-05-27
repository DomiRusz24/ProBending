package me.domirusz24.pk.probending.probending;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.*;
import me.domirusz24.pk.probending.probending.arena.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import me.domirusz24.pk.probending.probending.arena.commands.*;

import java.io.File;
import java.io.IOException;

public final class ProBending extends JavaPlugin
{
    public static JavaPlugin plugin;
    private FileConfiguration customConfig;

    public void onEnable() {
        System.out.println("ProBending zostal wlaczony!");
        ProBending.plugin = this;
        ProBending.plugin.getServer().getPluginManager().registerEvents((Listener)new ArenaListener(), (Plugin)this);
        createCustomConfig();
        this.getCommand("arena").setExecutor((CommandExecutor)new AreanaCreateCommand());
        this.getCommand("pbc").setExecutor((CommandExecutor)new ProBendingControlCommand());
        this.getCommand("arena").setTabCompleter((TabCompleter)new ArenaCreateCompleter());
        this.getCommand("pbc").setTabCompleter((TabCompleter)new PBCCompleter());
    }
    
    public void onDisable() {
        System.out.println("Zapisano areny do configu!");
    }

    public FileConfiguration getLocationConfig() {
        return this.customConfig;
    }

    private void createCustomConfig() {
        File customConfigFile = new File(getDataFolder(), "locations.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("locations.yml", false);
        }
        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
