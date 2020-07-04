package me.domirusz24.pk.probending.probending;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.ArenaListener;
import me.domirusz24.pk.probending.probending.arena.commands.ArenaCreateCommand;
import me.domirusz24.pk.probending.probending.arena.commands.ArenaCreateCompleter;
import me.domirusz24.pk.probending.probending.arena.commands.PBCCompleter;
import me.domirusz24.pk.probending.probending.arena.commands.ProBendingControlCommand;
import me.domirusz24.pk.probending.probending.config.ArenaLocationsConfig;
import me.domirusz24.pk.probending.probending.config.ArenaTBStagesConfig;
import me.domirusz24.pk.probending.probending.config.ConfigMethods;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class ProBending extends JavaPlugin
{
    public static ProBending plugin;
    private FileConfiguration customConfig;
    private File customConfigFile;
    public static String prefix = ChatColor.BOLD + "" + ChatColor.BLUE + "[ProBending]" + ChatColor.RESET + " ";
    public static String errorPrefix = prefix + ChatColor.BOLD + "" + ChatColor.RED + "[ERROR]" + ChatColor.RESET + "" + ChatColor.RED + " ";
    public static String successPrefix = prefix + ChatColor.BOLD + "" + ChatColor.GREEN + "[SUCCESS]" + ChatColor.RESET + "" + ChatColor.GREEN + " ";

    public void onEnable() {
        System.out.println("ProBending zostal wlaczony!");
        ProBending.plugin = this;
        ProBending.plugin.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
        this.getCommand("arena").setExecutor(new ArenaCreateCommand());
        this.getCommand("pbc").setExecutor(new ProBendingControlCommand());
        this.getCommand("arena").setTabCompleter(new ArenaCreateCompleter());
        this.getCommand("pbc").setTabCompleter(new PBCCompleter());
        int i = 1;
        ArenaLocationsConfig.setup();
        ArenaTBStagesConfig.setup();
        while (ArenaLocationsConfig.get().isSet("Arena.nr" + String.valueOf(i))) {
            try {
                new Arena(ConfigMethods.getLocation("Arena.nr" + String.valueOf(i) + ".center"), String.valueOf(i), false);
                System.out.println("Utworzono arene nr. " + String.valueOf(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
        getConfig().addDefault("stage.y", 30);
        getConfig().addDefault("TB.raisingStages", 4);
        getConfig().addDefault("arena.tickUpdate", 5);
        getConfig().addDefault("arena.winningRound", 2);
        getConfig().addDefault("arena.tieBreakerRound", 4800);
        getConfig().addDefault("arena.roundTime", 12000);
        getConfig().addDefault("debug", true);
        getConfig().options().copyDefaults(true);
        saveConfig();

    }
    
    public void onDisable() {
    }


}
