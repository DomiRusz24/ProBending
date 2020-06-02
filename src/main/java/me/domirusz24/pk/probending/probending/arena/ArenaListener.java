package me.domirusz24.pk.probending.probending.arena;

import me.domirusz24.pk.probending.probending.arena.temp.TempTeam;
import org.bukkit.GameMode;
import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import java.util.*;

public class ArenaListener implements Listener {
    public static ArrayList<Player> freezePlayers = new ArrayList<>();
    public static HashMap<Player, Boolean> playerDeathStatus = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (ArenaListener.freezePlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeave(final PlayerQuitEvent event) {
        playerDeathStatus.remove(event.getPlayer());
        if (Arena.playersPlaying.contains(event.getPlayer())) {
            for (final Arena arena : Arena.Arenas) {
                if (arena.isInGame() && arena.getAllPlayers().contains(event.getPlayer())) {
                    if (!arena.getPBPlayer(event.getPlayer()).isKilled()) {
                        arena.killPlayer(arena.getPBPlayer(event.getPlayer()));
                    }
                    arena.removePlayer(arena.getPBPlayer(event.getPlayer()));
                }
            }
        }
        if (TempTeam.playersWaiting.containsKey(event.getPlayer())) {
            TempTeam.playersWaiting.get(event.getPlayer()).removePlayer(event.getPlayer());
        }
        if (Arena.getPlayersSpectating().containsKey(event.getPlayer())) {
            Arena.getPlayersSpectating().get(event.getPlayer()).removeSpectator(event.getPlayer());
        }
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getFinalDamage() > player.getHealth()) {
                if (Arena.playersPlaying.contains(player)) {
                    for (final Arena arena : Arena.Arenas) {
                        if (arena.isInGame() && arena.getAllPlayers().contains(player)) {
                            player.setHealth(20);
                            event.setCancelled(true);
                            arena.killPlayer(arena.getPBPlayer(player));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (Arena.playersPlaying.contains(player)) {
                for (final Arena arena : Arena.Arenas) {
                    if (arena.isInGame() && arena.getAllPlayers().contains(player)) {
                        arena.killPlayer(arena.getPBPlayer(player));
                        ArenaListener.playerDeathStatus.put(player, true);
                    }
                }
            }
            if (Arena.getPlayersSpectating().containsKey(player)) {
                Arena.getPlayersSpectating().get(player).removeSpectator(player);
                ArenaListener.playerDeathStatus.put(player, false);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (ArenaListener.playerDeathStatus.containsKey(player)) {
            if (playerDeathStatus.get(player)) {
                if (Arena.playersPlaying.contains(player)) {
                    for (final Arena arena : Arena.Arenas) {
                        if (arena.isInGame() && arena.getAllPlayers().contains(player)) {
                            arena.addSpectator(player.getPlayer(), true);
                            playerDeathStatus.remove(event.getPlayer());
                            return;
                        }
                    }
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(Arena.spawn());
                }
            } else {
                if (Arena.getPlayersSpectating().containsKey(player)) {
                    Arena.getPlayersSpectating().get(player).removeSpectator(player);
                } else {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(Arena.spawn());
                }
            }
        }
    }

}
