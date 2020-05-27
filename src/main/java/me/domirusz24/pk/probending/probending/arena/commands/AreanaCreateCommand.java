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
                final Player player = (Player)sender;
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
                }
                else {
                    if (args[0].equalsIgnoreCase("setspawn")) {
                        try {
                            Arena.setSpawn(player.getLocation());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono spawn!");
                        return true;
                    }
                    if (args.length == 1) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj ID strefy!");
                        return true;
                    }
                    if (args.length == 2) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj typ teleporta. (player1, player2, player3, center)");
                        return true;
                    }
                    if (args.length > 3) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Za duzo argumentow!");
                        return true;
                    }
                    Arena arena;
                    try {
                        arena = Arena.getArenaFromID(args[0]);
                    }
                    catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "ID areny musi byc liczba!");
                        return true;
                    }
                    if (arena == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Takie ID areny nie istnieje!");
                        return true;
                    }
                    final StageEnum stageEnum = StageEnum.valueOf(args[1]);
                    final Stage stage = arena.getStage(stageEnum.getID());
                    if (arena == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Taka nazwa strefy nie istnieje!");
                        return true;
                    }
                    final String s = args[2];
                    switch (s) {
                        case "player1": {
                            try {
                                stage.setPlayer1Teleport(player.getLocation());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Playera 1 w " + stage.toString() + "!");
                            return true;
                        }
                        case "player2": {
                            try {
                                stage.setPlayer2Teleport(player.getLocation());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Playera 2 w " + stage.toString() + "!");
                            return true;
                        }
                        case "player3": {
                            try {
                                stage.setPlayer3Teleport(player.getLocation());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Playera 3 w " + stage.toString() + "!");
                            return true;
                        }
                        case "center": {
                            try {
                                stage.setCenter(player.getLocation());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Srodka w " + stage.toString() + "!");
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
        else {
            System.out.println("Musisz byc playerem aby wykonac ta komende!");
        }
        return true;
    }
}
