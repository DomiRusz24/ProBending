package me.domirusz24.pk.probending.probending.misc;

import me.domirusz24.pk.probending.probending.misc.Items.SpectatorLeave;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class CustomItem {
    public static void createItems() {
        new SpectatorLeave();
    }


    public static CustomItem getCustomItem(String name) {
        for (CustomItem e : customItems) {
            if (e.getName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }


    public static final ArrayList<CustomItem> customItems = new ArrayList<>();

    public CustomItem() {
        customItems.add(this);
    }

    public void givePlayer(Player player) {
        player.getInventory().addItem(getItem());
    }

    public void givePlayer(Player player, int slot) {
        player.getInventory().setItem(slot, getItem());
    }

    public void removePlayer(Player player) {
        player.getInventory().remove(getItem());
    }

    abstract public ItemStack getItem();
    abstract public String getName();
    abstract public void onClick(Player player, Action action);
}
