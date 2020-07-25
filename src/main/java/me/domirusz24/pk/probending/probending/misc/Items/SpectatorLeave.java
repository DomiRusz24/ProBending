package me.domirusz24.pk.probending.probending.misc.Items;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.config.ConfigEvents;
import me.domirusz24.pk.probending.probending.misc.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class SpectatorLeave extends CustomItem {
    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Wyjdz");
        itemMeta.setUnbreakable(true);
        itemMeta.setLore(Collections.singletonList(ChatColor.BOLD + "" + ChatColor.GOLD + "Kliknij aby wyjsc z gry!"));
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public String getName() {
        return "arena_leave";
    }

    @Override
    public void onClick(Player player, Action action) {
        if (Arena.getPlayersSpectating().containsKey(player)) {
            Arena.getPlayersSpectating().get(player).removeSpectator(player);
            ConfigEvents.PlayerClickLeave.run(Arena.getPlayersSpectating().get(player), player);
        } else if (Arena.playersPlaying.contains(player)) {
            for (Arena a : Arena.Arenas) {
                if (a.isInGame()) {
                    if (a.getAllPlayers().contains(player)) {
                        a.killPlayer(a.getPBPlayer(player));
                        a.removeSpectator(player);
                        ConfigEvents.PlayerClickLeave.run(a, player);
                    }
                }
            }
        } else {
            player.sendMessage(ProBending.errorPrefix + "Nie ogladasz, ani nie grasz!");
        }
    }
}
