package me.domirusz24.pk.probending.probending.arena;

import me.domirusz24.pk.probending.probending.arena.team.TempTeam;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.HashMap;

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (Arena.playersPlaying.contains(player)) {
                for (final Arena arena : Arena.Arenas) {
                    if (arena.isInGame() && arena.getAllPlayers().contains(player)) {
                        if (player.getHealth() - event.getFinalDamage() <= 0) {
                            player.setHealth(20);
                            event.setCancelled(true);
                            arena.killPlayer(arena.getPBPlayer(player));
                        }
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onHitEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player target = (Player) event.getEntity();
            Player damager;
            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            } else if (event.getDamager() instanceof Projectile) {
                if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    damager = (Player) ((Projectile) event.getDamager()).getShooter();
                } else {
                    return;
                }
            }  else {
                return;
            }

            if (Arena.playersPlaying.contains(target) && Arena.playersPlaying.contains(damager)) {
                for (Arena arena : Arena.Arenas) {
                    if (arena.isInGame()) {
                        if (arena.getAllPlayers().contains(target) && arena.getAllPlayers().contains(damager)) {
                            if (arena.getPBPlayer(target).getTeam().getTeamTag() == arena.getPBPlayer(damager).getTeam().getTeamTag()) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }


        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (Arena.playersPlaying.contains(player)) {
                for (final Arena arena : Arena.Arenas) {
                    if (arena.isInGame() && arena.getAllPlayers().contains(player)) {
                        arena.killPlayer(arena.getPBPlayer(player));
                        return;
                    }
                }
            } else if (Arena.getPlayersSpectating().containsKey(player)) {
                Arena.getPlayersSpectating().get(player).removeSpectator(player);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (Arena.playersPlaying.contains(player)) {
            for (final Arena arena : Arena.Arenas) {
                if (arena.isInGame() && arena.getAllPlayers().contains(player)) {
                    arena.addSpectator(player.getPlayer(), true);
                    event.setRespawnLocation(arena.getCenter().clone().add(0,6,0));
                    return;
                }
            }
        } else if (Arena.getPlayersSpectating().containsKey(player)) {
            Arena.getPlayersSpectating().get(player).removeSpectator(player);
            event.setRespawnLocation(Arena.getPlayersSpectating().get(player).getCenter().clone().add(0,6,0));
        } else {
            player.setGameMode(GameMode.SURVIVAL);
            event.setRespawnLocation(Arena.spawn());
        }
    }
}
