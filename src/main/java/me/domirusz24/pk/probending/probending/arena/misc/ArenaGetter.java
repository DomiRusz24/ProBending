package me.domirusz24.pk.probending.probending.arena.misc;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.config.ConfigMethods;
import me.domirusz24.pk.probending.probending.misc.GeneralMethods;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ArenaGetter {
    private final Arena arena;

    public ArenaGetter(Arena arena) {
        this.arena = arena;
    }

    public void setGetter(ArenaGetters getter, Location min, Location max) {
        if (getter == null || min == null || max == null) {
            return;
        }
        ConfigMethods.saveWESelection("Arena.nr" + arena.getID() + "." + getter.getName(), min, max);
    }

    public Location[] getGetter(ArenaGetters getter) {
        Location[] loc = ConfigMethods.getWESelection("Arena.nr" + arena.getID() + "." + getter.getName());
        if (loc == null) {
            System.out.println(getter.getName() + " in arena " + arena.getID() + " location is null");
            return null;
        }
        if (loc.length != 2) {
            System.out.println(getter.getName() + " in arena " + arena.getID() + " location length is not 2");
            return null;
        }
        if (loc[0] == null || loc[1] == null) {
            System.out.println(getter.getName() + " in arena " + arena.getID() + " one of the locations is null");
            return null;
        }
        return loc;
    }

    public ArrayList<Player> getPlayersInside(ArenaGetters getter) {
        Location[] loc = getGetter(getter);
        if (loc == null) {
            return null;
        }
        return GeneralMethods.getPlayersBetween(loc[0], loc[1]);
    }

    public ArrayList<String> getInfo(ArenaGetters getter) {
        Location[] loc = getGetter(getter);
        if (loc == null) {
            return null;
        }
        ArrayList<String> info = new ArrayList<>();
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "GETTER INFO FOR " + getter.getName() + " IN ARENA " + arena.getID());
        info.add(ChatColor.ITALIC + "" + ChatColor.RED + "MIN: ");
        info.add(ChatColor.AQUA + "X: " + loc[0].getX());
        info.add(ChatColor.AQUA + "Y: " + loc[0].getY());
        info.add(ChatColor.AQUA + "Z: " + loc[0].getZ());
        info.add(ChatColor.ITALIC + "" + ChatColor.RED + "MAX: ");
        info.add(ChatColor.AQUA + "X: " + loc[1].getX());
        info.add(ChatColor.AQUA + "Y: " + loc[1].getY());
        info.add(ChatColor.AQUA + "Z: " + loc[1].getZ());
        return info;
    }

}
