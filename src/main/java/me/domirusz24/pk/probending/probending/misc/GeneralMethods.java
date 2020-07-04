package me.domirusz24.pk.probending.probending.misc;

import me.domirusz24.pk.probending.probending.ProBending;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeneralMethods {


    public static ArrayList<Player> getPlayersBetween(Location min, Location max) {
        if (min == null || max == null) {
            return null;
        }
        ArrayList<Player> p = new ArrayList<>();
        double x1, x2, y1, y2, z1, z2;
        if (min.getX() < max.getX()) {
            x1 = min.getX();
            x2 = max.getX();
        } else {
            x1 = max.getX();
            x2 = min.getX();
        }
        if (min.getY() < max.getY()) {
            y1 = min.getY();
            y2 = max.getY();
        } else {
            y1 = max.getY();
            y2 = min.getY();
        }
        if (min.getX() < max.getX()) {
            z1 = min.getZ();
            z2 = max.getZ();
        } else {
            z1 = max.getZ();
            z2 = min.getZ();
        }
        for (Player player : ProBending.plugin.getServer().getOnlinePlayers()) {
            if (player.getLocation().getWorld().equals(min.getWorld())) {
                Location loc = player.getLocation();
                double x = loc.getBlockX();
                double y = loc.getBlockY();
                double z = loc.getBlockZ();
                if (x >= x1 && x2 >= x && y >= y1 && y2 >= y && z >= z1 && z2 >= z) {
                    p.add(player);
                }
            }
        }
          return p;
    }

    public static List<String> getPossibleCompletions(String[] args, List<String> possibilitiesOfCompletion) {
        String argumentToFindCompletionFor = args[args.length - 1];
        ArrayList<String> listOfPossibleCompletions = new ArrayList<>();
        Iterator<String> var4 = possibilitiesOfCompletion.iterator();

        while(var4.hasNext()) {
            String foundString = var4.next();
            if (foundString.regionMatches(true, 0, argumentToFindCompletionFor, 0, argumentToFindCompletionFor.length())) {
                listOfPossibleCompletions.add(foundString);
            }
        }

        return listOfPossibleCompletions;
    }
}
