package me.domirusz24.pk.probending.probending.arena;

import me.domirusz24.pk.probending.probending.arena.temp.TempTeam;
import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import java.util.*;

public class ArenaListener implements Listener
{
    public static ArrayList<Player> freezePlayers = new ArrayList<>();
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (ArenaListener.freezePlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent event) {
        if (Arena.playersPlaying.contains(event.getPlayer())) {
            for (final Arena arena : Arena.Arenas) {
                if (arena.isInGame() && arena.getAllPlayers().contains(event.getPlayer())) {
                    arena.killPlayer(arena.getPBPlayer(event.getPlayer()));
                    arena.removePlayer(arena.getPBPlayer(event.getPlayer()));
                }
            }
        }
        if (TempTeam.playersWaiting.containsKey(event.getPlayer())) {
            TempTeam.playersWaiting.get(event.getPlayer()).removePlayer(event.getPlayer());
        }
    }
}
