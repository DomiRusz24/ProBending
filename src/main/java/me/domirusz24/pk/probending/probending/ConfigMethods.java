package me.domirusz24.pk.probending.probending;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;

public class ConfigMethods {

    public static void saveLocation(String path, Location location) throws IOException {
        CustomConfig.reload();
        CustomConfig.get().set(path + ".x", Math.floor(location.getX()));
        CustomConfig.get().set(path + ".y", Math.floor(location.getY()));
        CustomConfig.get().set(path + ".z",Math.floor(location.getZ()));
        CustomConfig.get().set(path + ".yaw", location.getYaw());
        CustomConfig.get().set(path + ".pitch", location.getPitch());
        CustomConfig.get().set(path + ".world", location.getWorld().getName());
        CustomConfig.save();
        CustomConfig.reload();
    }

    public static Location getLocation(String path) {
        CustomConfig.reload();
        double x = CustomConfig.get().getInt(path + ".x");
        double y = CustomConfig.get().getInt(path + ".y");
        double z = CustomConfig.get().getInt(path + ".z");
        double yaw = CustomConfig.get().getDouble(path + ".yaw");
        double pitch = CustomConfig.get().getDouble(path + ".pitch");
        String w = CustomConfig.get().getString(path + ".world");
        if (w == null) {
            return null;
        }
        World world = ProBending.plugin.getServer().getWorld(w);
        return new Location(world, x + 0.5, y, z + 0.5, (float) yaw, (float) pitch);
    }

    public static void saveArenaLocations() {

    }

    public static void loadArenaLocations() {

    }



}
