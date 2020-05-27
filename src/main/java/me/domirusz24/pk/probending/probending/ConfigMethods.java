package me.domirusz24.pk.probending.probending;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.UUID;

public class ConfigMethods {

    public static void saveLocation(String path, Location location) throws IOException {
        Config.reload();
        Config.get().set(path + ".x", location.getBlockX());
        Config.get().set(path + ".y", location.getBlockY());
        Config.get().set(path + ".z", location.getBlockZ());
        Config.get().set(path + ".yaw", location.getYaw());
        Config.get().set(path + ".pitch", location.getPitch());
        Config.get().set(path + ".world", location.getWorld().getName());
        Config.save();
        Config.reload();
    }

    public static Location getLocation(String path) {
        Config.reload();
        int x = Config.get().getInt(path + ".x");
        int y = Config.get().getInt(path + ".y");
        int z = Config.get().getInt(path + ".z");
        double yaw = Config.get().getDouble(path + ".yaw");
        double pitch = Config.get().getDouble(path + ".pitch");
        String w = Config.get().getString(path + ".world");
        World world = ProBending.plugin.getServer().getWorld(w);
        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    public static void saveArenaLocations() {

    }

    public static void loadArenaLocations() {

    }



}
