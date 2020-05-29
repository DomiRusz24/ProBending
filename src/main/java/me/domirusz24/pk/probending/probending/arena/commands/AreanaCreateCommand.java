package me.domirusz24.pk.probending.probending.arena.commands;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import me.domirusz24.pk.probending.probending.arena.*;

import java.io.IOException;

public class AreanaCreateCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            if (command.getLabel().equalsIgnoreCase("arena")) {
                final Player player = (Player) sender;
                if (!player.hasPermission("probending.arena.config")) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji.");
                    return true;
                }
                if (args.length == 0) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj ID areny lub create / setspawn!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("create")) {
                    if (!player.hasPermission("probending.arena.create")) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji.");
                        return true;
                    }
                    try {
                        new Arena(player.getLocation(), String.valueOf(Arena.Arenas.size() + 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Stworzono arene o ID: " + Arena.Arenas.size());
                    return true;
                } else {
                    if (args[0].equalsIgnoreCase("setspawn")) {
                        try {
                            Arena.setSpawn(player.getLocation());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono spawn!");
                        return true;
                    } else if (args[0].equalsIgnoreCase("teleportspawn")) {
                        if (Arena.spawn() == null) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Spawn nie istnieje!");
                            return true;
                        } else {
                            player.teleport(Arena.spawn());
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Zostales przeteleportowany na spawn!");
                            return true;
                        }
                    }
                    if (args.length == 1) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj ID strefy!");
                        return true;
                    }
                    if (args.length == 2) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj typ teleporta. (player1, player2, player3, center)");
                        return true;
                    }
                    if (args.length > 4) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Za duzo argumentow!");
                        return true;
                    }
                    Arena arena;
                    try {
                        arena = Arena.getArenaFromID(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "ID areny musi byc liczba!");
                        return true;
                    }
                    if (arena == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Takie ID areny nie istnieje!");
                        return true;
                    }
                    final StageEnum stageEnum = StageEnum.getFromConfigName(args[1]);
                    if (stageEnum == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Taka nazwa strefy nie istnieje!");
                        return true;
                    }
                    final Stage stage = arena.getStage(stageEnum.getID());
                    if (stage == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Arena nie wytworzyla jeszcze tej areny!");
                        return true;
                    }
                    System.out.println(stageEnum.toString());
                    final String s = args[2];
                    if (args.length == 3) {
                        switch (s) {
                            case "player1": {
                                try {
                                    stage.setPlayer1Teleport(player.getLocation());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Playera 1 w " + stage.getStage().toString() + "!");
                                return true;
                            }
                            case "player2": {
                                try {
                                    stage.setPlayer2Teleport(player.getLocation());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Playera 2 w " + stage.getStage().toString() + "!");
                                return true;
                            }
                            case "player3": {
                                try {
                                    stage.setPlayer3Teleport(player.getLocation());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Playera 3 w " + stage.getStage().toString() + "!");
                                return true;
                            }
                            case "center": {
                                try {
                                    stage.setCenter(player.getLocation());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Srodka w " + stage.getStage().toString() + "!");
                                return true;
                            }
                            default: {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprwany typ teleporta.");
                                return true;
                            }
                        }
                    } else {
                        if (!args[3].equalsIgnoreCase("teleport")) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Miales na mysli teleport?");
                            return true;
                        }
                        switch (s) {
                            case "player1": {
                                if (stage.getPlayer1Teleport() == null) {
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Teleport player1 w " + stage.getStage().toString() + " nie istnieje!");
                                }
                                player.teleport(stage.getPlayer1Teleport());
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Przeteleportowano cie do player1 w " + stage.getStage().toString() + "!");
                                return true;
                            }
                            case "player2": {
                                if (stage.getPlayer2Teleport() == null) {
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Teleport player2 w " + stage.getStage().toString() + " nie istnieje!");
                                }
                                player.teleport(stage.getPlayer2Teleport());
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Przeteleportowano cie do player2 w " + stage.getStage().toString() + "!");
                                return true;
                            }
                            case "player3": {
                                if (stage.getPlayer3Teleport() == null) {
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Teleport player3 w " + stage.getStage().toString() + " nie istnieje!");
                                }
                                player.teleport(stage.getPlayer3Teleport());
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Przeteleportowano cie do player3 w " + stage.getStage().toString() + "!");
                                return true;
                            }
                            case "center": {
                                if (stage.getCenter() == null) {
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Teleport center w " + stage.getStage().toString() + " nie istnieje!");
                                }
                                player.teleport(stage.getCenter());
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Przeteleportowano cie do center w " + stage.getStage().toString() + "!");
                                return true;
                            }
                            default: {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprwany typ teleporta.");
                                return true;
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("Musisz byc playerem aby wykonac ta komende!");
        }
        return true;
    }
}
