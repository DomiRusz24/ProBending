package me.domirusz24.pk.probending.probending.misc.guis;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.misc.CustomGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class SpectatorGUI extends CustomGUI {

    public SpectatorGUI(Player player) {
        super(player);
        setUpInv();
    }

    public ItemStack createItem(int ID) {
        ItemStack i = null;
        ItemMeta m;
        if (ID == 0) {
            i = new ItemStack(Material.MAGMA_CREAM,1);
            m = i.getItemMeta();
            m.setDisplayName(ChatColor.RED + "Wyjdz");
            m.setLore(Collections.singletonList(ChatColor.BOLD + "" + ChatColor.GOLD + "Kliknij aby wyjsc z gry!"));
            m.setUnbreakable(true);
            i.setItemMeta(m);
        }
        return i;
    }

    public void setUpInv() {
        inventory.setItem(4, createItem(0));
        player.openInventory(inventory);
    }


    @Override
    public String getInventoryName() {
        return ChatColor.GOLD + "Menu spektatora";
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem().getItemMeta().equals(createItem(0).getItemMeta())) {
            Arena a = Arena.getPlayersSpectating().get(player);
            if (a != null) {
                a.removeSpectator(player);
            }
            player.closeInventory();
        }

    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
    }
}
