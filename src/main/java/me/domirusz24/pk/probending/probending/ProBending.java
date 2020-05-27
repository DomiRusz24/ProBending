package me.domirusz24.pk.probending.probending;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.ArenaListener;
import me.domirusz24.pk.probending.probending.arena.commands.AreanaCreateCommand;
import me.domirusz24.pk.probending.probending.arena.commands.ArenaCreateCompleter;
import me.domirusz24.pk.probending.probending.arena.commands.PBCCompleter;
import me.domirusz24.pk.probending.probending.arena.commands.ProBendingControlCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class ProBending extends JavaPlugin
{
    public static ProBending plugin;
    private FileConfiguration customConfig;
    private File customConfigFile;

    public void onEnable() {
        System.out.println("ProBending zostal wlaczony!");
        ProBending.plugin = this;
        ProBending.plugin.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
        this.getCommand("arena").setExecutor(new AreanaCreateCommand());
        this.getCommand("pbc").setExecutor(new ProBendingControlCommand());
        this.getCommand("arena").setTabCompleter(new ArenaCreateCompleter());
        this.getCommand("pbc").setTabCompleter(new PBCCompleter());
        Config.setup();
        int i = 1;
        while (Config.get().isSet("Arena.nr" + String.valueOf(i))) {
            try {
                new Arena(ConfigMethods.getLocation("Arena.nr" + String.valueOf(i) + ".center"), String.valueOf(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
    
    public void onDisable() {
    }


}
