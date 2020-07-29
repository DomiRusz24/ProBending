package me.domirusz24.pk.probending.probending.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public abstract class CustomGUI {
    public final static HashMap<Player, CustomGUI> PLAYER_ACTIVE_GUI = new HashMap<>();

    public final Player player;
    public final Inventory inventory;

    public CustomGUI(Player player) {
        this.player = player;
        this.inventory = Bukkit.getServer().createInventory(null, getSize(), getInventoryName());
        CustomGUI.PLAYER_ACTIVE_GUI.put(player, this);
    }

    public void drag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    public void click(InventoryClickEvent event) {
        event.setCancelled(true);
        onClick(event);
    }

    public void close(InventoryCloseEvent event) {
        PLAYER_ACTIVE_GUI.remove(player);
        onClose(event);
    }

    public abstract String getInventoryName();

    public abstract int getSize();

    protected abstract void onClick(InventoryClickEvent event);

    protected abstract void onClose(InventoryCloseEvent event);
}
