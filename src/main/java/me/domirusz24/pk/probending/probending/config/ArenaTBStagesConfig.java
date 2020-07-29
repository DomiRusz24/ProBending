package me.domirusz24.pk.probending.probending.config;

import org.bukkit.Bukkit;

import java.io.File;

public class ArenaTBStagesConfig extends Config {
    @Override
    public File getFile() {
        return new File(Bukkit.getServer().getPluginManager().getPlugin("ProBending").getDataFolder(), "locations/TBStages.yml");
    }
}
