package me.domirusz24.pk.probending.probending.misc;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.misc.Elements;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
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
                if (player.getLocation().toVector().isInAABB(new Vector(x1, y1, z1), new Vector(x2, y2, z2))) p.add(player);
            }
        }
          return p;
    }

    public static List<String> getPossibleCompletions(String[] args, List<String> possibilitiesOfCompletion) {
        String argumentToFindCompletionFor = args[args.length - 1];
        ArrayList<String> listOfPossibleCompletions = new ArrayList<>();

        for (String foundString : possibilitiesOfCompletion) {
            if (foundString.regionMatches(true, 0, argumentToFindCompletionFor, 0, argumentToFindCompletionFor.length())) {
                listOfPossibleCompletions.add(foundString);
            }
        }
        return listOfPossibleCompletions;
    }

    public static Vector rotateVectorAroundY(Vector vector, double degrees) {
        double rad = Math.toRadians(degrees);

        double currentX = vector.getX();
        double currentZ = vector.getZ();

        double cosine = Math.cos(rad);
        double sine = Math.sin(rad);

        return new Vector((cosine * currentX - sine * currentZ), vector.getY(), (sine * currentX + cosine * currentZ));
    }

    public static Elements getPlayerElement(BendingPlayer player) {
        Element e = null;
        if (player.getAbilities() == null) {
            return null;
        }
        for (int i = 0; i <= 9; i++) {
            Ability ab = CoreAbility.getAbility(player.getAbilities().get(i));
            if (ab != null) {
                Element eleab = ab.getElement() instanceof Element.SubElement ? ((Element.SubElement) ab.getElement()).getParentElement() : ab.getElement();
                if (!Elements.getValidElements().contains(eleab)) {
                    return Elements.Illegal;
                }
                if (e == null) {
                    e = eleab;
                } else if (e != eleab) {
                    return null;
                }
            }
        }
        if (e == null) {
            return Elements.NonBender;
        }
        return Elements.getElement(e);
    }


    public static ItemStack addGlow(ItemStack item, boolean glow) {
        ItemMeta e = item.getItemMeta();
        if (glow) {
            e.addEnchant(Enchantment.WATER_WORKER, 70, true);
            e.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            e.getEnchants().keySet().forEach(e::removeEnchant);
            e.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(e);
        return item;
    }
}
