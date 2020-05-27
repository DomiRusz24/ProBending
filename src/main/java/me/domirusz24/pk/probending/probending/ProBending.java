package me.domirusz24.pk.probending.probending;

import me.domirusz24.pk.probending.probending.arena.commands.AreanaCreateCommand;
import me.domirusz24.pk.probending.probending.arena.commands.ArenaCreateCompleter;
import me.domirusz24.pk.probending.probending.arena.ArenaListener;
import me.domirusz24.pk.probending.probending.arena.commands.ProBendingControlCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ProBending extends JavaPlugin {

    public static JavaPlugin plugin;

    @Override
    public void onEnable() {
        System.out.println("ProBending zostal wlaczony!");
        plugin = this;
        plugin.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
        this.getCommand("arena").setExecutor(new AreanaCreateCommand());
        this.getCommand("pbc").setExecutor(new ProBendingControlCommand());
        this.getCommand("arena").setTabCompleter(new ArenaCreateCompleter());
    }
}
