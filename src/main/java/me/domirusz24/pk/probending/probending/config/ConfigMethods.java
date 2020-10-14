package me.domirusz24.pk.probending.probending.config;

import me.domirusz24.pk.probending.probending.misc.customsigns.TeamStartSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import static me.domirusz24.pk.probending.probending.ProBending.plugin;

public class ConfigMethods {

    public static void saveLocation(String path, Location location) {
        ConfigManager.getArenaLocationsConfig().reloadConfig();
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".x", Math.floor(location.getX()));
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".y", Math.floor(location.getY()));
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".z",Math.floor(location.getZ()));
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".yaw", location.getYaw());
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".pitch", location.getPitch());
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".world", location.getWorld().getName());
        ConfigManager.getArenaLocationsConfig().saveConfig();
        ConfigManager.getArenaLocationsConfig().reloadConfig();
    }

    public static Location getLocation(String path) {
        ConfigManager.getArenaLocationsConfig().reloadConfig();
        if (!ConfigManager.getArenaLocationsConfig().getConfig().contains(path + ".world")) {
            System.out.println(path + ".world does not exist!");
            return null;
        }
        double x = ConfigManager.getArenaLocationsConfig().getConfig().getInt(path + ".x");
        double y = ConfigManager.getArenaLocationsConfig().getConfig().getInt(path + ".y");
        double z = ConfigManager.getArenaLocationsConfig().getConfig().getInt(path + ".z");
        double yaw = ConfigManager.getArenaLocationsConfig().getConfig().getDouble(path + ".yaw");
        double pitch = ConfigManager.getArenaLocationsConfig().getConfig().getDouble(path + ".pitch");
        String w = ConfigManager.getArenaLocationsConfig().getConfig().getString(path + ".world");
        World world = plugin.getServer().getWorld(w);
        if (world == null) {
            System.out.println("World \"" + w + "\" doesn't exist!");
            return null;
        }
        return new Location(world, x + 0.5, y, z + 0.5, (float) yaw, (float) pitch);
    }

    public static void saveSign(TeamStartSign teamStartSign) {
        if (!teamStartSign.isSet()) return;
        String path = teamStartSign.getPath();
        ConfigManager.getArenaLocationsConfig().reloadConfig();
        Location location = teamStartSign.getSign().getLocation();
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".x", Math.floor(location.getX()));
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".y", Math.floor(location.getY()));
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".z",Math.floor(location.getZ()));
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".world", location.getWorld().getName());
        ConfigManager.getArenaLocationsConfig().saveConfig();
        ConfigManager.getArenaLocationsConfig().reloadConfig();
    }

    public static Sign getTeamSign(TeamStartSign teamStartSign) {
        ConfigManager.getArenaTBStagesConfig().reloadConfig();
        String path = teamStartSign.getPath();
        double x = ConfigManager.getArenaTBStagesConfig().getConfig().getInt(path + ".x");
        double y = ConfigManager.getArenaTBStagesConfig().getConfig().getInt(path + ".y");
        double z = ConfigManager.getArenaTBStagesConfig().getConfig().getInt(path + ".z");
        String worldName = ConfigManager.getArenaTBStagesConfig().getConfig().getString(path + ".world");
        if (worldName == null) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);

        Block block = new Location(world, x, y, z).getBlock();
        if (block instanceof Sign) {
            return (Sign) block;
        }
        return null;
    }

    public static void saveWESelection(String path, Location min, Location max) {
        if (min == null || max == null) {
            return;
        }
        ConfigManager.getArenaLocationsConfig().reloadConfig();
        ConfigManager.getArenaLocationsConfig().getConfig().set(path + ".world", min.getWorld().getName());
        for (int i = 0; i <= 1; i++) {
            Location location = i == 0 ? min : max;
            String minmax = i == 0 ? ".min" : ".max";
            ConfigManager.getArenaLocationsConfig().getConfig().set(path + minmax + ".x", Math.floor(location.getX()));
            ConfigManager.getArenaLocationsConfig().getConfig().set(path + minmax +".y", Math.floor(location.getY()));
            ConfigManager.getArenaLocationsConfig().getConfig().set(path + minmax +".z",Math.floor(location.getZ()));
        }
        ConfigManager.getArenaLocationsConfig().saveConfig();
        ConfigManager.getArenaLocationsConfig().reloadConfig();
    }

    public static Location[] getWESelection(String path) {
        ConfigManager.getArenaLocationsConfig().reloadConfig();
        if (!ConfigManager.getArenaLocationsConfig().getConfig().contains(path + ".world")) {
            return null;
        }
        Location min = new Location(plugin.getServer().getWorlds().get(0), 0, 0, 0);
        Location max = new Location(plugin.getServer().getWorlds().get(0), 0, 0, 0);
        String w = ConfigManager.getArenaLocationsConfig().getConfig().getString(path + ".world");
        World world = Bukkit.getWorld(w);
        for (int i = 0; i <= 1; i++) {
            String minmax = i == 0 ? ".min" : ".max";
            double x = ConfigManager.getArenaLocationsConfig().getConfig().getInt(path + minmax + ".x");
            double y = ConfigManager.getArenaLocationsConfig().getConfig().getInt(path + minmax + ".y");
            double z = ConfigManager.getArenaLocationsConfig().getConfig().getInt(path + minmax + ".z");
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
        ConfigManager.getArenaTBStagesConfig().reloadConfig();
        double x = ConfigManager.getArenaTBStagesConfig().getConfig().getInt("nr" + arenaID + "." + TBstage + ".x");
        double y = ConfigManager.getArenaTBStagesConfig().getConfig().getInt("nr" + arenaID + "." + TBstage + ".y");
        double z = ConfigManager.getArenaTBStagesConfig().getConfig().getInt("nr" + arenaID + "." + TBstage + ".z");
        String worldName = ConfigManager.getArenaTBStagesConfig().getConfig().getString("nr" + arenaID + "." + TBstage + ".world");
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
        ConfigManager.getArenaTBStagesConfig().reloadConfig();
        ConfigManager.getArenaTBStagesConfig().getConfig().set("nr" + arenaID + "." + TBstage + ".x", x);
        ConfigManager.getArenaTBStagesConfig().getConfig().set("nr" + arenaID + "." + TBstage + ".y", y);
        ConfigManager.getArenaTBStagesConfig().getConfig().set("nr" + arenaID + "." + TBstage + ".z", z);
        ConfigManager.getArenaTBStagesConfig().getConfig().set("nr" + arenaID + "." + TBstage + ".world", world.getName());
        ConfigManager.getArenaTBStagesConfig().saveConfig();
    }



}
