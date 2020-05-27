package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.PBTeamPlayer;
import me.domirusz24.pk.probending.probending.arena.TeamTag;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ProBendingControlCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("pbc")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj ID areny!");
                    return false;
                }
                if (args.length == 1) {
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

                    ArrayList<String> info = new ArrayList<>();
                    ChatColor inGame = arena.isInGame() ? ChatColor.RED : ChatColor.GREEN;
                    info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");

                    // ARENA ID INFO
                    info.add(ChatColor.BOLD + "" + inGame + "Arena " + arena.getID());
                    info.add(ChatColor.BOLD + "Srodek areny: " + arena.getCenter().getBlock().getLocation().getX() + " " + arena.getCenter().getBlock().getLocation().getY() + " " + arena.getCenter().getBlock().getLocation().getZ());

                    // ID INFO
                    info.add(ChatColor.BOLD + "ID:" + arena.getID());

                    // INFO IF IN GAME
                    String inGameInfo = arena.isInGame() ? "TAK" : "NIE";
                    info.add(ChatColor.BOLD + "" + inGame + "W grze: " + inGameInfo);

                    if (arena.isInGame() || arena.inProgressOfCreating) {

                        // TEAMS
                        info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "DRUZYNY: ");
                        info.add("");

                        // RED TEAM
                        info.add(ChatColor.BOLD + "" + ChatColor.RED + "CZERWONA: ");
                        int i = 0;
                        for (PBTeamPlayer teamPlayer : arena.getTeamRed().getPBPlayers()) {
                            i++;
                            String playerstatus = teamPlayer == null ? "NIE DODANY" : teamPlayer.getPlayer().getName();
                            info.add("Gracz " + i + ": " + playerstatus);
                        }
                        info.add("Punkty: " + arena.getTeamRed().getPoints());

                        // BLUE TEAM
                        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "NIEBIESKA: ");
                        int y = 0;
                        for (PBTeamPlayer teamPlayer : arena.getTeamBlue().getPBPlayers()) {
                            y++;
                            String playerstatus = teamPlayer == null ? "NIE DODANY" : teamPlayer.getPlayer().getName();
                            info.add("Gracz " + i + ": " + playerstatus);
                        }
                        info.add("Punkty: " + arena.getTeamBlue().getPoints());

                    }
                    info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");

                    info.forEach(player::sendMessage);


                    return false;
                }
                if (!player.hasPermission("probending.arena.modify")) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                    return false;
                }
                if (args.length == 2) {
                    switch (args[1]) {
                        case "stop":

                            return false;
                        case "add":
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj jakiego gracza dodac!");
                            return false;
                        case "remove":
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj jakiego gracza usunac!");
                            return false;
                    }
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprawny argument!");
                    return false;
                }
                if (args.length == 3) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj do jakiej druzyny dodac gracza!");
                }
                if (args.length == 4) {

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

                    Player target = ProBending.plugin.getServer().getPlayer(args[2]);
                    if (target == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Player " + args[2] + " nie jest online!");
                        return false;
                    }

                    TeamTag joinTeam;

                    switch (args[2]) {
                        case "red":
                            joinTeam = TeamTag.RED;
                            break;
                        case "blue":
                            joinTeam = TeamTag.BLUE;
                            break;
                        default:
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprawna druzyna!");
                            return false;
                    }

                    switch (args[1]) {
                        case "add":
                            if (!arena.getTeamByTag(joinTeam).isFull()) {

                            } else {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta druzyna jest pelna!");
                            }
                            return false;
                        case "remove":

                            return false;
                    }
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprawny argument! (add, remove)");
                    return false;
                }
            }

        }
        return false;
    }
}
