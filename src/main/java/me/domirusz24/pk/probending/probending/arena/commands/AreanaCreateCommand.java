package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.Stage;
import me.domirusz24.pk.probending.probending.arena.StageEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AreanaCreateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (command.getLabel().equalsIgnoreCase("arena")) {
                Player player = (Player) sender;
                if (!player.hasPermission("probending.arena.config")) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji.");
                    return false;
                }

                if (args.length == 0) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj ID areny lub create / setspawn!");
                    return false;
                }
                if (args[0].equalsIgnoreCase("create")) {
                    if (!player.hasPermission("probending.arena.create")) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji.");
                        return false;
                    }
                    new Arena(player.getLocation());
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Stworzono arene o ID: " + Arena.Arenas.size());
                    return false;
                } else if (args[0].equalsIgnoreCase("setspawn")) {
                    Arena.spawn = player.getLocation();
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono spawn!");
                    return false;
                }

                if (args.length == 1) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj ID strefy!");
                    return false;
                }
                if (args.length == 2) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj typ teleporta. (player1, player2, player3, center)");
                    return false;
                }
                if (args.length > 3) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Za duzo argumentow!");
                    return false;
                }
                Arena arena;
                try {
                    arena = Arena.Arenas.get(Integer.valueOf(args[0]));
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "ID areny musi byc liczba!");
                    return false;
                }
                if (arena == null) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Takie ID areny nie istnieje!");
                    return false;
                }
                StageEnum stageEnum = StageEnum.valueOf(args[1]);
                Stage stage = arena.getStage(stageEnum.getID());
                if (arena == null) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Taka nazwa strefy nie istnieje!");
                    return false;
                }
                switch (args[2]) {
                    case "player1":
                        stage.setPlayer1Teleport(player.getLocation());
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Playera 1!");
                        return false;
                    case "player2":
                        stage.setPlayer2Teleport(player.getLocation());
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Playera 2!");
                        return false;
                    case "player3":
                        stage.setPlayer3Teleport(player.getLocation());
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Playera 3!");
                        return false;
                    case "center":
                        stage.setCenter(player.getLocation());
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Ustawiono teleport Srodka!");
                        return false;
                    default:
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprwany typ teleporta.");
                        return false;


                }

            }



        } else {
            System.out.println("Musisz byc playerem aby wykonac ta komende!");
        }



        return false;
    }
}
