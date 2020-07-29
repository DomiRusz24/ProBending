package me.domirusz24.pk.probending.probending;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.ArenaListener;
import me.domirusz24.pk.probending.probending.arena.commands.*;
import me.domirusz24.pk.probending.probending.arena.kit.PlayerKit;
import me.domirusz24.pk.probending.probending.config.ConfigManager;
import me.domirusz24.pk.probending.probending.config.ConfigMethods;
import me.domirusz24.pk.probending.probending.misc.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

public final class ProBending extends JavaPlugin
{
    public static ProBending plugin;
    public static ScoreboardManager scoreboardManager;
    public static final String prefix = ChatColor.BOLD + "" + ChatColor.BLUE + "[ProBending]" + ChatColor.RESET + " ";
    public static final String errorPrefix = prefix + ChatColor.BOLD + "" + ChatColor.RED + "[ERROR]" + ChatColor.RESET + "" + ChatColor.RED + " ";
    public static final String successPrefix = prefix + ChatColor.BOLD + "" + ChatColor.GREEN + "[SUCCESS]" + ChatColor.RESET + "" + ChatColor.GREEN + " ";
    public static final String arrow = ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RESET + "";

    public void onEnable() {
        System.out.println("ProBending zostal wlaczony!");
        ProBending.plugin = this;
        scoreboardManager = this.getServer().getScoreboardManager();
        ProBending.plugin.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
        this.getCommand("arena").setExecutor(new ArenaCreateCommand());
        this.getCommand("pbc").setExecutor(new ProBendingControlCommand());
        this.getCommand("arena").setTabCompleter(new ArenaCreateCompleter());
        this.getCommand("pbc").setTabCompleter(new PBCCompleter());
        this.getCommand("leave").setExecutor(new LeaveCommand());
        this.getCommand("statystyki").setExecutor(new Statistics());
        this.getCommand("kity").setExecutor(new KitsMenu());
        int i = 1;
        ConfigManager.register();
        while (ConfigManager.getWinLoseCommands().getConfig().isSet("Arena.nr" + i)) {
                new Arena(ConfigMethods.getLocation("Arena.nr" + i + ".center"), String.valueOf(i), false);
                System.out.println("Utworzono arene nr. " + i);
            i++;
        }
        getConfig().addDefault("stage.y", 30);
        getConfig().addDefault("TB.raisingStages", 4);
        getConfig().addDefault("arena.tickUpdate", 5);
        getConfig().addDefault("arena.winningRound", 2);
        getConfig().addDefault("arena.tieBreakerRound", 4800);
        getConfig().addDefault("arena.roundTime", 12000);
        getConfig().addDefault("arena.hpToTirednessRatio", 3);
        getConfig().addDefault("debug", true);
        getConfig().options().copyDefaults(true);
        PlayerKit.readKits();
        saveConfig();
        ArenaListener.HpRatio = getConfig().getInt("arena.hpToTirednessRatio", 3);
        CustomItem.createItems();
    }
    
    public void onDisable() {
        for (Arena arena : Arena.Arenas) {
            arena.stopGame();
        }
    }
}
