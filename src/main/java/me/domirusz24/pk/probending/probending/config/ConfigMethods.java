package me.domirusz24.pk.probending.probending.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import static me.domirusz24.pk.probending.probending.ProBending.plugin;

public class ConfigMethods {

    public static void saveLocation(String path, Location location) {
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
        if (!ArenaLocationsConfig.get().contains(path + ".world")) {
            System.out.println(path + ".world does not exist!");
            return null;
        }
        double x = ArenaLocationsConfig.get().getInt(path + ".x");
        double y = ArenaLocationsConfig.get().getInt(path + ".y");
        double z = ArenaLocationsConfig.get().getInt(path + ".z");
        double yaw = ArenaLocationsConfig.get().getDouble(path + ".yaw");
        double pitch = ArenaLocationsConfig.get().getDouble(path + ".pitch");
        String w = ArenaLocationsConfig.get().getString(path + ".world");
        World world = plugin.getServer().getWorld(w);
        if (world == null) {
            System.out.println("World \"" + w + "\" doesn't exist!");
            return null;
        }
        return new Location(world, x + 0.5, y, z + 0.5, (float) yaw, (float) pitch);
    }

    public static void saveWESelection(String path, Location min, Location max) {
        if (min == null || max == null) {
            return;
        }
        ArenaLocationsConfig.reload();
        ArenaLocationsConfig.get().set(path + ".world", min.getWorld().getName());
        for (int i = 0; i <= 1; i++) {
            Location location = i == 0 ? min : max;
            String minmax = i == 0 ? ".min" : ".max";
            ArenaLocationsConfig.get().set(path + minmax + ".x", Math.floor(location.getX()));
            ArenaLocationsConfig.get().set(path + minmax +".y", Math.floor(location.getY()));
            ArenaLocationsConfig.get().set(path + minmax +".z",Math.floor(location.getZ()));
        }
        ArenaLocationsConfig.save();
        ArenaLocationsConfig.reload();
    }

    public static Location[] getWESelection(String path) {
        ArenaLocationsConfig.reload();
        if (!ArenaLocationsConfig.get().contains(path + ".world")) {
            return null;
        }
        Location min = new Location(plugin.getServer().getWorlds().get(0), 0, 0, 0);
        Location max = new Location(plugin.getServer().getWorlds().get(0), 0, 0, 0);
        String w = ArenaLocationsConfig.get().getString(path + ".world");
        World world = Bukkit.getWorld(w);
        for (int i = 0; i <= 1; i++) {
            String minmax = i == 0 ? ".min" : ".max";
            double x = ArenaLocationsConfig.get().getInt(path + minmax + ".x");
            double y = ArenaLocationsConfig.get().getInt(path + minmax + ".y");
            double z = ArenaLocationsConfig.get().getInt(path + minmax + ".z");
            if (i == 0) {
                min = new Location(world, x, y, z);
            } else {
                max = new Location(world, x, y, z);
            }
        }
        return new Location[]{min, max};
    }

    public static Location getTBLocation(String arenaID, String TBstage) {
        if (plugin == null) {
            System.out.println("Plugin is null!");
        }
        ArenaTBStagesConfig.reload();
        double x = ArenaTBStagesConfig.get().getInt("nr" + arenaID + "." + TBstage + ".x");
        double y = ArenaTBStagesConfig.get().getInt("nr" + arenaID + "." + TBstage + ".y");
        double z = ArenaTBStagesConfig.get().getInt("nr" + arenaID + "." + TBstage + ".z");
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
