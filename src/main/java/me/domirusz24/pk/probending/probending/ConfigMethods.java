package me.domirusz24.pk.probending.probending;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static me.domirusz24.pk.probending.probending.ProBending.plugin;

public class ConfigMethods {

    public static void saveLocation(String path, Location location) throws IOException {
        ArenaLocationsConfig.reload();
        ArenaLocationsConfig.get().set(path + ".x", Math.floor(location.getX()));
        ArenaLocationsConfig.get().set(path + ".y", Math.floor(location.getY()));
        ArenaLocationsConfig.get().set(path + ".z",Math.floor(location.getZ()));
        ArenaLocationsConfig.get().set(path + ".yaw", location.getYaw());
        ArenaLocationsConfig.get().set(path + ".pitch", location.getPitch());
        ArenaLocationsConfig.get().set(path + ".world", location.getWorld().getName());
        ArenaLocationsConfig.save();
        ArenaLocationsConfig.reload();
    }

    public static Location getLocation(String path) {
        ArenaLocationsConfig.reload();
        double x = ArenaLocationsConfig.get().getInt(path + ".x");
        double y = ArenaLocationsConfig.get().getInt(path + ".y");
        double z = ArenaLocationsConfig.get().getInt(path + ".z");
        double yaw = ArenaLocationsConfig.get().getDouble(path + ".yaw");
        double pitch = ArenaLocationsConfig.get().getDouble(path + ".pitch");
        String w = ArenaLocationsConfig.get().getString(path + ".world");
        if (w == null) {
            return null;
        }
        World world = plugin.getServer().getWorld(w);
        return new Location(world, x + 0.5, y, z + 0.5, (float) yaw, (float) pitch);
    }

    public static Location getTBLocation(String arenaID, String TBstage) {
        if (plugin == null) {
            System.out.println("Plugin is null!");
        }
        ArenaTBStagesConfig.reload();
        double x = (double)ArenaTBStagesConfig.get().getInt("nr" + arenaID + "." + TBstage + ".x");
        double y = (double)ArenaTBStagesConfig.get().getInt("nr" + arenaID + "." + TBstage + ".y");
        double z = (double)ArenaTBStagesConfig.get().getInt("nr" + arenaID + "." + TBstage + ".z");
        String worldName = ArenaTBStagesConfig.get().getString("nr" + arenaID + "." + TBstage + ".world");
        if (worldName == null) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        return new Location(world, x, y, z);
    }

    public static void setTBLocation(String arenaID, String TBstage, int x, int y, int z, World world) {
        if (plugin == null) {
            System.out.println("Plugin is null!");
        }
        ArenaTBStagesConfig.reload();
        ArenaTBStagesConfig.get().set("nr" + arenaID + "." + TBstage + ".x", x);
        ArenaTBStagesConfig.get().set("nr" + arenaID + "." + TBstage + ".y", y);
        ArenaTBStagesConfig.get().set("nr" + arenaID + "." + TBstage + ".z", z);
        ArenaTBStagesConfig.get().set("nr" + arenaID + "." + TBstage + ".world", world.getName());
        ArenaTBStagesConfig.save();
        ArenaTBStagesConfig.reload();
    }



}
