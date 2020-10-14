package me.domirusz24.pk.probending.probending.arena;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.misc.ListHologram;
import me.domirusz24.pk.probending.probending.arena.team.TempTeam;
import me.domirusz24.pk.probending.probending.misc.CustomGUI;
import me.domirusz24.pk.probending.probending.misc.CustomItem;
import me.domirusz24.pk.probending.probending.misc.CustomSign;
import me.domirusz24.pk.probending.probending.misc.customguis.SpectatorGUI;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;

public class ArenaListener implements Listener {
    private static final HashSet<String> freezePlayers = new HashSet<>();

    public static void freezePlayer(Player player) {
        freezePlayers.add(player.getUniqueId().toString());
    }

    public static void unFreezePlayer(Player player) {
        freezePlayers.remove(player.getUniqueId().toString());
    }



    public static final HashMap<Player, Boolean> playerDeathStatus = new HashMap<>();
    public static final HashMap<Player, Player> lastDamage = new HashMap<>();
    public static int HpRatio;

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] split = event.getMessage().split(" ");
        if (split[0].equalsIgnoreCase("/wyjdz") || split[0].equalsIgnoreCase("/leave")) return;
        if (Arena.playersPlaying.contains(event.getPlayer())) {
            event.getPlayer().sendMessage(ProBending.errorPrefix + "Jestes w grze! Nie mozesz uzyc tej komendy, prosze wyjdz za pomoca komendy /wyjdz");
            event.setCancelled(true);
        } else if (Arena.getPlayersSpectating().containsKey(event.getPlayer())) {
            event.getPlayer().sendMessage(ProBending.errorPrefix + "Ogladasz gre! Nie mozesz uzyc tej komendy, prosze wyjdz za pomoca komendy /wyjdz");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (ArenaListener.freezePlayers.contains(event.getPlayer().getUniqueId().toString())) {
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
                        if (!arena.getPBPlayer(player).isKilled()) {
                            if (player.getHealth() - event.getFinalDamage() <= 0) {
                                player.setHealth(20);
                                event.setCancelled(true);
                                arena.killPlayer(arena.getPBPlayer(player));
                            }
                        }
                    }
                }
            }
            if (Arena.getPlayersSpectating().containsKey(player)) {
                event.setCancelled(true);
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
            } else {
                return;
            }
            if (Arena.playersPlaying.contains(target) && Arena.playersPlaying.contains(damager)) {
                for (Arena arena : Arena.Arenas) {
                    if (arena.isInGame()) {
                        if (arena.getAllPlayers().contains(target) && arena.getAllPlayers().contains(damager)) {
                            if (arena.isInPeace()) {
                                event.setCancelled(true);
                            } else if (arena.getPBPlayer(target).getTeam().getTeamTag() == arena.getPBPlayer(damager).getTeam().getTeamTag()) {
                                event.setCancelled(true);
                            } else {
                                Player player = (Player) event.getEntity();
                                lastDamage.put(player, damager);
                                arena.getPBPlayer(player).increaseTiredMeter(Math.round((long) event.getDamage() * HpRatio));
                                new BukkitRunnable() {
                                    final double vel = 1 + ((double) arena.getPBPlayer(player).getTiredMeter() / 100);
                                    @Override
                                    public void run() {
                                        Vector v = player.getVelocity();
                                        v.setX(v.getX() * vel);
                                        v.setZ(v.getZ() * vel);
                                        player.setVelocity(v);
                                    }
                                }.runTaskLater(ProBending.plugin, 1);
                            }
                        }
                    }
                }
            }
            if (Arena.getPlayersSpectating().containsKey(target)) {
                event.setCancelled(true);
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
            event.setRespawnLocation(Arena.getSpawn());
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand() != null && event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
            ItemStack main = event.getPlayer().getInventory().getItemInMainHand();
            if (main.getItemMeta() == null) {
                return;
            }
            for (CustomItem e : CustomItem.customItems) {
                if (main.getItemMeta().equals(e.getItem().getItemMeta())) {
                    e.onClick(event.getPlayer(), event.getAction());
                    return;
                }
            }
        }
        if (event.getClickedBlock() != null) {
            Block b = event.getClickedBlock();
            if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
                Sign s = (Sign) b.getState();
                for (CustomSign sign : CustomSign.CUSTOM_SIGNS) {
                    if (sign.isSet()) {
                        if (sign.getSign().equals(s)) {
                            sign.onRightClick(event.getPlayer());
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getType().equals(EntityType.PLAYER)) {
            CustomGUI g = CustomGUI.PLAYER_ACTIVE_GUI.get((Player) event.getWhoClicked());
            if (g != null) {
                g.click(event);
            }
        }
    }

    @EventHandler
    public void onInvDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().getType().equals(EntityType.PLAYER)) {
            CustomGUI g = CustomGUI.PLAYER_ACTIVE_GUI.get((Player) event.getWhoClicked());
            if (g != null) {
                g.drag(event);
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (event.getPlayer().getType().equals(EntityType.PLAYER)) {
            CustomGUI g = CustomGUI.PLAYER_ACTIVE_GUI.get((Player) event.getPlayer());
            if (g != null) {
                g.close(event);
            }
        }
    }

    @EventHandler
    public void onAni(PlayerAnimationEvent event) {
        if (Arena.getPlayersSpectating().get(event.getPlayer()) != null) {
            new SpectatorGUI(event.getPlayer());
        }
    }

    @EventHandler
    public void pluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("HolographicDisplays")) {
            System.out.println("Hooking into HolographicDisplays...");
            for (Arena arena : Arena.Arenas) {
                arena.hookUpHologram();
            }
            ListHologram.update();
            System.out.println("Hooked in!");
        }
    }
}
