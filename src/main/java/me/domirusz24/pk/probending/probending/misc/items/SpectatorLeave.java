package me.domirusz24.pk.probending.probending.misc.items;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.config.winlosecommandsconfig.ConfigEvents;
import me.domirusz24.pk.probending.probending.misc.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class SpectatorLeave extends CustomItem {

    public SpectatorLeave() {
        super();
    }

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
            Arena arena = Arena.getArena(player);
            assert arena != null;
            arena.killPlayer(arena.getPBPlayer(player));
            arena.removeSpectator(player);
            ConfigEvents.PlayerClickLeave.run(arena, player);
        } else {
            player.sendMessage(ProBending.errorPrefix + "Nie ogladasz, ani nie grasz!");
        }
    }
}
