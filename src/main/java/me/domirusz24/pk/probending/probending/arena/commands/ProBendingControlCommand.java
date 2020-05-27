package me.domirusz24.pk.probending.probending.arena.commands;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import java.util.function.*;
import me.domirusz24.pk.probending.probending.arena.*;
import me.domirusz24.pk.probending.probending.*;
import java.util.*;

public class ProBendingControlCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player)sender;
            if (command.getName().equalsIgnoreCase("pbc")) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj ID areny!");
                    return false;
                }
                if (args.length == 1) {
                    Arena arena;
                    try {
                        arena = Arena.getArenaFromID(args[0]);
                    }
                    catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "ID areny musi byc liczba!");
                        return false;
                    }
                    if (arena == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Takie ID areny nie istnieje!");
                        return false;
                    }
                    final ArrayList<String> info = new ArrayList<String>();
                    final ChatColor inGame = arena.isInGame() ? ChatColor.RED : ChatColor.GREEN;
                    info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
                    info.add(ChatColor.BOLD + "" + inGame + "Arena " + arena.getID());
                    info.add(ChatColor.BOLD + "Srodek areny: " + arena.getCenter().getBlock().getLocation().getX() + " " + arena.getCenter().getBlock().getLocation().getY() + " " + arena.getCenter().getBlock().getLocation().getZ());
                    info.add(ChatColor.BOLD + "ID:" + arena.getID());
                    final String inGameInfo = arena.isInGame() ? "TAK" : "NIE";
                    info.add(ChatColor.BOLD + "" + inGame + "W grze: " + inGameInfo);
                    if (!arena.isInGame()) {
                        info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "DRUZYNY CZEKAJACE NA START: ");
                        info.add("");
                        info.add(ChatColor.BOLD + "" + ChatColor.RED + "CZERWONA: ");
                        if (arena.getTempTeamByTag(TeamTag.RED).getAllPlayers(true) != null) {
                            int i = 0;
                            for (final Player player2 : arena.getTempTeamByTag(TeamTag.RED).getAllPlayers(true)) {
                                ++i;
                                final String playerstatus = (player2 == null) ? "NIE DODANY" : player2.getName();
                                info.add("Gracz " + i + ": " + playerstatus);
                            }
                        }
                        else {
                            info.add("Gracz 1: NIE DODANY");
                            info.add("Gracz 2: NIE DODANY");
                            info.add("Gracz 3: NIE DODANY");
                        }
                        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "NIEBIESKA: ");
                        if (arena.getTempTeamByTag(TeamTag.BLUE).getAllPlayers(true) != null) {
                            int i = 0;
                            for (final Player player2 : arena.getTempTeamByTag(TeamTag.BLUE).getAllPlayers(true)) {
                                ++i;
                                final String playerstatus = (player2 == null) ? "NIE DODANY" : player2.getName();
                                info.add("Gracz " + i + ": " + playerstatus);
                            }
                        }
                        else {
                            info.add("Gracz 1: NIE DODANY");
                            info.add("Gracz 2: NIE DODANY");
                            info.add("Gracz 3: NIE DODANY");
                        }
                        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
                        final ArrayList<String> list = info;
                        final Player player3 = player;
                        Objects.requireNonNull(player3);
                        list.forEach(player3::sendMessage);
                        return false;
                    }
                    info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "DRUZYNY: ");
                    info.add("");
                    info.add(ChatColor.BOLD + "" + ChatColor.RED + "CZERWONA: ");
                    int i = 0;
                    for (final PBTeamPlayer teamPlayer : arena.getTeamRed().getPBPlayers(true)) {
                        ++i;
                        final String playerstatus = (teamPlayer == null) ? "NIE DODANY" : teamPlayer.getPlayer().getName();
                        info.add("Gracz " + i + ": " + playerstatus);
                    }
                    info.add("Punkty: " + arena.getTeamRed().getPoints());
                    info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "NIEBIESKA: ");
                    int y = 0;
                    for (final PBTeamPlayer teamPlayer2 : arena.getTeamBlue().getPBPlayers(true)) {
                        ++y;
                        final String playerstatus2 = (teamPlayer2 == null) ? "NIE DODANY" : teamPlayer2.getPlayer().getName();
                        info.add("Gracz " + y + ": " + playerstatus2);
                    }
                    info.add("Punkty: " + arena.getTeamBlue().getPoints());
                }
                if (!player.hasPermission("probending.arena.modify")) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                    return false;
                }
                if (args.length == 2) {
                    Arena arena;
                    try {
                        arena = Arena.getArenaFromID(args[0]);
                    }
                    catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "ID areny musi byc liczba!");
                        return false;
                    }
                    if (arena == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Takie ID areny nie istnieje!");
                        return false;
                    }
                    final String s = args[1];
                    switch (s) {
                        case "start": {
                            if (arena.isInGame()) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra juz sie rozpoczela!");
                                return false;
                            }
                            arena.startGameWithFeedBack(player);
                            return false;
                        }
                        case "stop": {
                            if (!arena.isInGame()) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra jeszcze sie nie rozpoczela!");
                                return false;
                            }
                            return false;
                        }
                        case "resetTempTeams": {
                            if (arena.isInGame()) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra juz sie rozpoczela!");
                                return false;
                            } else {
                                arena.getTempTeamByTag(TeamTag.BLUE).removeAllPlayers();
                                arena.getTempTeamByTag(TeamTag.RED).removeAllPlayers();
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Usunieto wszystkich playerow z oczekujacych druzyn!");
                            }
                        }
                        case "add": {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj jakiego gracza dodac!");
                            return false;
                        }
                        case "remove": {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj jakiego gracza usunac!");
                            return false;
                        }
                        default: {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprawny argument!");
                            return false;
                        }
                    }
                }
                else {
                    if (args.length == 3) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj do jakiej druzyny dodac gracza!");
                    }
                    if (args.length == 4) {
                        Arena arena;
                        try {
                            arena = Arena.getArenaFromID(args[0]);
                        }
                        catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "ID areny musi byc liczba!");
                            return false;
                        }
                        if (arena == null) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Takie ID areny nie istnieje!");
                            return false;
                        }
                        if (arena.isInGame()) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra juz sie rozpoczela!");
                            return false;
                        }
                        final Player target = ProBending.plugin.getServer().getPlayer(args[2]);
                        if (target == null) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Player " + args[2] + " nie jest online!");
                            return false;
                        }
                        if (Arena.playersPlaying.contains(target)) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Gracz " + target.getName() + " juz jest w grze!");
                            return false;
                        }
                        final String s2 = args[3];
                        TeamTag joinTeam = null;
                        switch (s2) {
                            case "red": {
                                joinTeam = TeamTag.RED;
                                break;
                            }
                            case "blue": {
                                joinTeam = TeamTag.BLUE;
                                break;
                            }
                            default: {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprawna druzyna!");
                                return false;
                            }
                        }
                        final String s3 = args[1];
                        switch (s3) {
                            case "add": {
                                if (!arena.getTempTeamByTag(joinTeam).readyToPlay()) {
                                    arena.getTempTeamByTag(joinTeam).addPlayer(target);
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Dodano gracza " + target.getName() + " do druzyny!");
                                    return false;
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta druzyna jest pelna!");
                                return false;
                            }
                            case "remove": {
                                if (arena.getTempTeamByTag(joinTeam).getAllPlayers().contains(target)) {
                                    arena.getTempTeamByTag(joinTeam).removePlayer(player);
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Usunieto gracza " + target.getName() + " z druzyny!");
                                    return false;
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta osoba nie jest w tej druzynie!");
                                return false;
                            }
                            default: {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprawny argument! (add, remove)");
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
