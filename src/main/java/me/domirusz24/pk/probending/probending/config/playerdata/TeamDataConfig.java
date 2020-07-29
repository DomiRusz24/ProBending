package me.domirusz24.pk.probending.probending.config.playerdata;

import me.domirusz24.pk.probending.probending.config.Config;
import org.bukkit.Bukkit;

import java.io.File;

public class TeamDataConfig extends Config {
    @Override
    public File getFile() {
        return new File(Bukkit.getServer().getPluginManager().getPlugin("ProBending").getDataFolder(), "Data/TeamData.yml");
    }
}
