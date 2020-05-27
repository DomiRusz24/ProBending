package me.domirusz24.pk.probending.probending.arena;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class ArenaListener implements Listener {

    public static ArrayList<Player> freezePlayers = new ArrayList<>();

    public void onMove(PlayerMoveEvent event) {
        if (freezePlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    public void onLeave(PlayerQuitEvent event) {
        if (Arena.playersPlaying.contains(event.getPlayer())) {
            for (Arena arena : Arena.Arenas) {
                if (arena.isInGame()) {
                    if (arena.getAllPlayers().contains(event.getPlayer())) {
                        arena.killPlayer(arena.getPBPlayer(event.getPlayer()));
                        return;
                    }
                }
            }
        }
    }

}
