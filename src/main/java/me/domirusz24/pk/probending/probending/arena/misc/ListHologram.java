package me.domirusz24.pk.probending.probending.arena.misc;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.config.ConfigMethods;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ListHologram {

    private static Hologram hologram;
    private static Location location;

    public static void setLocation(Location location) {
        ConfigMethods.saveLocation("ArenaListHologram", location);
        replace();
    }

    public static Location getLocation() {
        return ConfigMethods.getLocation("ArenaListHologram");
    }

    public static List<String> getArenaList() {
        List<String> info = new ArrayList<>();
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "Dostepne areny:");
        for (Arena arena : Arena.Arenas) {
            String s = arena.isInGame() ? "Zajeta!" : "Wolna!";
            ChatColor c = arena.isInGame() ? ChatColor.RED : ChatColor.GREEN;
            info.add(c + "Arena " + arena.getID() + ": " + s);
        }
        return info;
    }

    public static void update() {
        if (getLocation() == null) {
            return;
        }
        if (hologram == null) {
            replace();
        }
        hologram.clearLines();
        getArenaList().forEach(hologram::appendTextLine);
    }

    public static void replace() {
        if (getLocation() == null) {
            return;
        }
        if (hologram == null) {
            hologram = HologramsAPI.createHologram(ProBending.plugin, getLocation());
        } else {
            hologram.teleport(getLocation());
        }
    }

    public static List<String> getInfo() {
        List<String> info = new ArrayList<>();
        info.add(ChatColor.BLUE + "Info for ArenaList hologram:");
        info.add(ChatColor.GRAY + "Location: " + (getLocation() == null ? "Not set!" : " X: " + getLocation().getX() + " Y: " + getLocation().getY() + " Z: " + getLocation().getZ() + " World: " + getLocation().getWorld()));
        return info;
    }
}
