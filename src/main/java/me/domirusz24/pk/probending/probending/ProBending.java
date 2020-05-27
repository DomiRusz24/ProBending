package me.domirusz24.pk.probending.probending;

import org.bukkit.plugin.java.*;
import me.domirusz24.pk.probending.probending.arena.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import me.domirusz24.pk.probending.probending.arena.commands.*;

public final class ProBending extends JavaPlugin
{
    public static JavaPlugin plugin;
    
    public void onEnable() {
        System.out.println("ProBending zostal wlaczony!");
        ProBending.plugin = this;
        ProBending.plugin.getServer().getPluginManager().registerEvents((Listener)new ArenaListener(), (Plugin)this);
        this.getCommand("arena").setExecutor((CommandExecutor)new AreanaCreateCommand());
        this.getCommand("pbc").setExecutor((CommandExecutor)new ProBendingControlCommand());
        this.getCommand("arena").setTabCompleter((TabCompleter)new ArenaCreateCompleter());
        this.getCommand("pbc").setTabCompleter((TabCompleter)new PBCCompleter());
    }
    
    public void onDisable() {
        System.out.println("Zapisano areny do configu!");
    }
}
