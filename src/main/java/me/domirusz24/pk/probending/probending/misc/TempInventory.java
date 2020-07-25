package me.domirusz24.pk.probending.probending.misc;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TempInventory {

    private ItemStack[] invContents;
    private ItemStack[] armorContents;
    private ItemStack[] extraContents;
    private final Player player;

    public TempInventory(Player player) {
        invContents = player.getPlayer().getInventory().getContents();
        armorContents = player.getPlayer().getInventory().getArmorContents();
        extraContents = player.getPlayer().getInventory().getExtraContents();
        this.player = player;
    }

    public void remove() {
        invContents = player.getPlayer().getInventory().getContents();
        armorContents = player.getPlayer().getInventory().getArmorContents();
        extraContents = player.getPlayer().getInventory().getExtraContents();
        player.getInventory().clear();
    }

    public void revert() {
        player.getInventory().setContents(invContents);
        player.getInventory().setArmorContents(armorContents);
        player.getInventory().setExtraContents(extraContents);
    }
}
