package me.domirusz24.pk.probending.probending.misc.guis;

import me.domirusz24.pk.probending.probending.arena.kit.PlayerKit;
import me.domirusz24.pk.probending.probending.config.ConfigManager;
import me.domirusz24.pk.probending.probending.misc.CustomGUI;
import me.domirusz24.pk.probending.probending.misc.GeneralMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LoadoutGUI extends CustomGUI {

    public LoadoutGUI(Player player) {
        super(player);
        ConfigManager.getDataConfig().reloadConfig();
        for (PlayerKit k : PlayerKit.getAvailableKits().values()) {
            if (player.hasPermission(k.getPermission())) {
                tempBool.put(k, k.isEnabled(player));
            } else {
                tempBool.put(k, false);
            }
        }
        setUpInventory();
        player.openInventory(inventory);
    }

    private final HashMap<PlayerKit, Boolean> tempBool = new HashMap<>();

    public void setUpInventory() {
        inventory.clear();
        int slot = 0;
        for (PlayerKit k : PlayerKit.getAvailableKits().values()) {
            ItemStack i = k.getItemStack();
            ItemMeta m = i.getItemMeta();
            String status;
            if (player.hasPermission(k.getPermission())) {
                status = tempBool.get(k) ? ChatColor.GREEN + "Wlaczony!" : ChatColor.GRAY + "Wylaczony!";
            } else {
                status = ChatColor.RED + "Nie masz permisji!";
            }
            if (m.getLore() == null || m.getLore().isEmpty()) {
                m.setLore(Collections.singletonList(status));
            } else {
                List<String> temp = new ArrayList<>(m.getLore());
                temp.add(status);
                m.setLore(temp);
            }
            i.setItemMeta(m);
            if (tempBool.get(k)) {
                i = GeneralMethods.addGlow(i, true);
            }
            inventory.setItem(slot, i);
            slot++;
        }
    }

    @Override
    public String getInventoryName() {
        return ChatColor.GOLD + "" + ChatColor.BOLD + "Kity";
    }

    @Override
    public int getSize() {
        int slots = PlayerKit.getAvailableKits().size();
        slots = (int) Math.ceil((float) slots / 9);
        return slots * 9;
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        ItemStack i = event.getCurrentItem();
        if (i == null || i.getItemMeta() == null || i.getItemMeta().getLore() == null) {
            return;
        }
        for (ItemStack item : PlayerKit.getAvailableKits().keySet()) {
            PlayerKit kit = PlayerKit.getAvailableKits().get(item);
            if (player.hasPermission(kit.getPermission())) {
                if (item.getType().equals(i.getType()) && item.getItemMeta().getDisplayName().equals(i.getItemMeta().getDisplayName())) {
                    boolean e = tempBool.get(kit);
                    tempBool.put(kit, !e);
                    setUpInventory();
                    break;
                }
            }
        }
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        for (PlayerKit k : PlayerKit.getAvailableKits().values()) {
            k.togglePlayer(player, tempBool.get(k));
        }
        ConfigManager.getDataConfig().saveConfig();
    }
}
