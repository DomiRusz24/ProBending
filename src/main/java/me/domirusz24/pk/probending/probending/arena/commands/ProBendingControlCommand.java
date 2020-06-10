package me.domirusz24.pk.probending.probending.arena.commands;

import org.bukkit.attribute.Attribute;
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
                    return true;
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("stopspectate")) {
                        if (Arena.getPlayersSpectating().containsKey(player)) {
                            Arena.getPlayersSpectating().get(player).removeSpectator(player);
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Przestales spektatowac gre!");
                        } else {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Musisz byc spektatorem aby wyjsc!");
                        }
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
                    arena.displayArenaInfo(player);
                    return true;
                }
                if (args.length == 2) {
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
                    final String s = args[1];
                    switch (s.toLowerCase()) {
                        case "start": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra juz sie rozpoczela!");
                                return true;
                            }
                            arena.startGameWithFeedBack(player);
                            return true;
                        }
                        case "spectate": {
                            if (Arena.playersPlaying.contains(player)) {

                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie mozesz spektatowac w trakcie gry!");
                                return true;

                            } else if (!arena.isInGame()) {

                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra jeszcze sie nie zaczela!");
                                return true;

                            } else {
                                arena.addSpectator(player, false);
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Spektetujesz gre w arenie " + arena.getID() + "!");
                                return true;

                            }
                        }
                        case "teleport": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.getCenter() == null) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Jakims cudem srodek nie istnieje...");

                            } else {
                                Location t = arena.getCenter();
                                t.setY(t.getY() + 6);
                                player.teleport(t);
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Zostales przeteleportowany na srodek areny " + arena.getID() + "!");
                            }
                            return true;
                        }
                        case "forcestart": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra juz sie rozpoczela!");
                                return true;
                            }
                            arena.forceStart(player);
                            return true;
                        }
                        case "stop": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (!arena.isInGame()) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra jeszcze sie nie rozpoczela!");
                                return true;
                            }
                            arena.stopGame();
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Zakonczono gre!");
                            return true;
                        }
                        case "resettempteams": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra juz sie rozpoczela!");
                            } else {
                                arena.getTempTeamByTag(TeamTag.BLUE).removeAllPlayers();
                                arena.getTempTeamByTag(TeamTag.RED).removeAllPlayers();
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Usunieto wszystkich playerow z oczekujacych druzyn!");
                            }
                            return true;
                        }
                        case "forcenextround": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                arena.nextMidRound();
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Zaczeto runde " + arena.getRoundNumber() + "!");
                            } else {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra jeszcze sie nie rozpoczela!");
                            }
                            return true;
                        }
                        case "forcetiebreaker": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                arena.startTieBreaker();
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Zaczeto TieBreaker!");
                            } else {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra jeszcze sie nie rozpoczela!");
                            }
                            return true;
                        }
                        case "add": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj jakiego gracza dodac!");
                            return true;
                        }
                        case "remove": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj jakiego gracza usunac!");
                            return true;
                        }
                        default: {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprawny argument!");
                            return true;
                        }
                    }
                }
                else {
                    if (args.length == 3) {
                        if (!player.hasPermission("probending.arena.modify")) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
                            return true;
                        }
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Prosze podaj do jakiej druzyny dodac gracza!");
                    }
                    if (args.length == 4) {
                        if (!player.hasPermission("probending.arena.modify")) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji");
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
                        if (arena.isInGame()) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta gra juz sie rozpoczela!");
                            return true;
                        }
                        final Player target = ProBending.plugin.getServer().getPlayer(args[2]);
                        if (target == null) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Player " + args[2] + " nie jest online!");
                            return true;
                        }
                        if (Arena.playersPlaying.contains(target)) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Gracz " + target.getName() + " juz jest w grze!");
                            return true;
                        }
                        final String s2 = args[3];
                        TeamTag joinTeam;
                        switch (s2.toLowerCase()) {
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
                                return true;
                            }
                        }
                        final String s3 = args[1];
                        switch (s3.toLowerCase()) {
                            case "add": {
                                if (!arena.getTempTeamByTag(joinTeam).readyToPlay()) {
                                    arena.getTempTeamByTag(joinTeam).addPlayer(target);
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Dodano gracza " + target.getName() + " do druzyny!");
                                    return true;
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta druzyna jest pelna!");
                                return true;
                            }
                            case "remove": {
                                if (arena.getTempTeamByTag(joinTeam).getAllPlayers().contains(target)) {
                                    arena.getTempTeamByTag(joinTeam).removePlayer(target);
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Usunieto gracza " + target.getName() + " z druzyny!");
                                    return true;
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Ta osoba nie jest w tej druzynie!");
                                return true;
                            }
                            default: {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprawny argument! (add, remove)");
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }




}
