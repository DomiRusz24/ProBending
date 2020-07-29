package me.domirusz24.pk.probending.probending.config.winlosecommandsconfig;

import me.domirusz24.pk.probending.probending.config.Config;
import org.bukkit.Bukkit;

import java.io.File;

public class WinLoseCommands extends Config {

    @Override
    public File getFile() {
        return new File(Bukkit.getServer().getPluginManager().getPlugin("ProBending").getDataFolder(), "WinLoseCommands.yml");
    }
}
