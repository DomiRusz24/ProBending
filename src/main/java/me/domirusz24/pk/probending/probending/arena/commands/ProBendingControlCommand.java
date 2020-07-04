package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.team.TeamTag;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ProBendingControlCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player)sender;
            if (command.getName().equalsIgnoreCase("pbc")) {
                if (args.length == 0) {
                    player.sendMessage(ProBending.errorPrefix +  "Prosze podaj ID areny!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("help")) {
                        displayHelp(player, args);
                        return true;
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("stopspectate")) {
                        if (Arena.getPlayersSpectating().containsKey(player)) {
                            Arena.getPlayersSpectating().get(player).removeSpectator(player);
                            player.sendMessage(ProBending.successPrefix +  "Przestales spektatowac gre!");
                        } else {
                            player.sendMessage(ProBending.errorPrefix +  "Musisz byc spektatorem aby wyjsc!");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("rules")) {
                        Arena.getArenaRules().forEach(player::sendMessage);
                        return true;
                    } else if (args[0].equalsIgnoreCase("autostart")) {
                        if (!player.hasPermission("probending.autostart")) {
                            player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                            return false;
                        }
                        for (Arena arena : Arena.Arenas) {
                            if (!arena.isInGame()) {
                                if (arena.autoStart()) {
                                    player.sendMessage(ProBending.successPrefix + "Arena " + arena.getID() + " zostala rozpoczeta!");
                                    return false;
                                }
                            }
                            player.sendMessage(ProBending.errorPrefix + "Zadna arena sie nie rozpoczela1");
                        }
                    }
                    Arena arena;
                    try {
                        arena = Arena.getArenaFromID(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ProBending.errorPrefix +  "ID areny musi byc liczba!");
                        return true;
                    }
                    if (arena == null) {
                        player.sendMessage(ProBending.errorPrefix +  "Takie ID areny nie istnieje!");
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
                        player.sendMessage(ProBending.errorPrefix +  "ID areny musi byc liczba!");
                        return true;
                    }
                    if (arena == null) {
                        player.sendMessage(ProBending.errorPrefix +  "Takie ID areny nie istnieje!");
                        return true;
                    }
                    final String s = args[1];
                    switch (s.toLowerCase()) {
                        case "start": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                player.sendMessage(ProBending.errorPrefix +  "Ta gra juz sie rozpoczela!");
                                return true;
                            }
                            arena.startGameWithFeedBack(player);
                            return true;
                        }
                        case "spectate": {
                            if (Arena.playersPlaying.contains(player)) {

                                player.sendMessage(ProBending.errorPrefix +  "Nie mozesz spektatowac w trakcie gry!");
                                return true;

                            } else if (!arena.isInGame()) {

                                player.sendMessage(ProBending.errorPrefix +  "Ta gra jeszcze sie nie zaczela!");
                                return true;

                            } else {
                                arena.addSpectator(player, false);
                                player.setGameMode(GameMode.SPECTATOR);
                                player.sendMessage(ProBending.successPrefix +  "Spektetujesz gre w arenie " + arena.getID() + "!");
                                return true;

                            }
                        }
                        case "teleport": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.getCenter() == null) {
                                player.sendMessage(ProBending.errorPrefix +  "Jakims cudem srodek nie istnieje...");

                            } else {
                                Location t = arena.getCenter();
                                t.setY(t.getY() + 6);
                                player.teleport(t);
                                player.sendMessage(ProBending.successPrefix +  "Zostales przeteleportowany na srodek areny " + arena.getID() + "!");
                            }
                            return true;
                        }
                        case "forcestart": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                player.sendMessage(ProBending.errorPrefix +  "Ta gra juz sie rozpoczela!");
                                return true;
                            }
                            arena.forceStart(player);
                            return true;
                        }
                        case "autostart": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                player.sendMessage(ProBending.errorPrefix +  "Ta gra juz sie rozpoczela!");
                                return true;
                            }
                            if (arena.autoStart()) {
                                player.sendMessage(ProBending.successPrefix +  "Gra zostala rozpoczeta!");
                            } else {
                                player.sendMessage(ProBending.errorPrefix +  "Gra sie nie rozpoczela z powodu braku graczy!");
                            }
                            return true;
                        }
                        case "stop": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (!arena.isInGame()) {
                                player.sendMessage(ProBending.errorPrefix +  "Ta gra jeszcze sie nie rozpoczela!");
                                return true;
                            }
                            arena.stopGame();
                            player.sendMessage(ProBending.successPrefix +  "Zakonczono gre!");
                            return true;
                        }
                        case "resettempteams": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                player.sendMessage(ProBending.errorPrefix +  "Ta gra juz sie rozpoczela!");
                            } else {
                                arena.getTempTeamByTag(TeamTag.BLUE).removeAllPlayers();
                                arena.getTempTeamByTag(TeamTag.RED).removeAllPlayers();
                                player.sendMessage(ProBending.successPrefix +  "Usunieto wszystkich playerow z oczekujacych druzyn!");
                            }
                            return true;
                        }
                        case "forcenextround": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            if (arena.isInGame()) {
                                arena.nextMidRound();
                                player.sendMessage(ProBending.successPrefix +  "Zaczeto runde " + arena.getRoundNumber() + "!");
                            } else {
                                player.sendMessage(ProBending.errorPrefix +  "Ta gra jeszcze sie nie rozpoczela!");
                            }
                            return true;
                        }
                        case "add": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            player.sendMessage(ProBending.errorPrefix +  "Prosze podaj jakiego gracza dodac!");
                            return true;
                        }
                        case "remove": {
                            if (!player.hasPermission("probending.arena.modify")) {
                                player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                                return true;
                            }
                            player.sendMessage(ProBending.errorPrefix +  "Prosze podaj jakiego gracza usunac!");
                            return true;
                        }
                        default: {
                            player.sendMessage(ProBending.errorPrefix +  "Nie poprawny argument!");
                            return true;
                        }
                    }
                }
                else {
                    if (args.length == 3) {
                        if (!player.hasPermission("probending.arena.modify")) {
                            player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                            return true;
                        }
                        player.sendMessage(ProBending.errorPrefix +  "Prosze podaj do jakiej druzyny dodac gracza!");
                    }
                    if (args.length == 4) {
                        if (!player.hasPermission("probending.arena.modify")) {
                            player.sendMessage(ProBending.errorPrefix +  "Nie masz wystarczajacych permisji");
                            return true;
                        }
                        Arena arena;
                        try {
                            arena = Arena.getArenaFromID(args[0]);
                        }
                        catch (NumberFormatException e) {
                            player.sendMessage(ProBending.errorPrefix +  "ID areny musi byc liczba!");
                            return true;
                        }
                        if (arena == null) {
                            player.sendMessage(ProBending.errorPrefix +  "Takie ID areny nie istnieje!");
                            return true;
                        }
                        if (arena.isInGame()) {
                            player.sendMessage(ProBending.errorPrefix +  "Ta gra juz sie rozpoczela!");
                            return true;
                        }
                        final Player target = ProBending.plugin.getServer().getPlayer(args[2]);
                        if (target == null) {
                            player.sendMessage(ProBending.errorPrefix +  "Player " + args[2] + " nie jest online!");
                            return true;
                        }
                        if (Arena.playersPlaying.contains(target)) {
                            player.sendMessage(ProBending.errorPrefix +  "Gracz " + target.getName() + " juz jest w grze!");
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
                                player.sendMessage(ProBending.errorPrefix +  "Nie poprawna druzyna!");
                                return true;
                            }
                        }
                        final String s3 = args[1];
                        switch (s3.toLowerCase()) {
                            case "add": {
                                if (!arena.getTempTeamByTag(joinTeam).readyToPlay()) {
                                    arena.getTempTeamByTag(joinTeam).addPlayer(target);
                                    player.sendMessage(ProBending.successPrefix +  "Dodano gracza " + target.getName() + " do druzyny!");
                                    return true;
                                }
                                player.sendMessage(ProBending.errorPrefix +  "Ta druzyna jest pelna!");
                                return true;
                            }
                            case "remove": {
                                if (arena.getTempTeamByTag(joinTeam).getAllPlayers().contains(target)) {
                                    arena.getTempTeamByTag(joinTeam).removePlayer(target);
                                    player.sendMessage(ProBending.successPrefix +  "Usunieto gracza " + target.getName() + " z druzyny!");
                                    return true;
                                }
                                player.sendMessage(ProBending.errorPrefix +  "Ta osoba nie jest w tej druzynie!");
                                return true;
                            }
                            default: {
                                player.sendMessage(ProBending.errorPrefix +  "Nie poprawny argument! (add, remove)");
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }


    public void displayHelp(Player player, String[] args) {
        ArrayList<String> info = new ArrayList<>();
        ChatColor c = ChatColor.GREEN;
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
        if (args.length == 1 || args[1].equalsIgnoreCase("1")) {
            info.add(c + "/pbc rules - Wyswietla zasady gry");
            info.add(c + "/pbc (ID ARENY) - Wyswietla informacje o arenie.");
            info.add(c + "/pbc stopspectate - Zakoncza spektacje meczu.");
            info.add(c + "/pbc autostart - Automatycznie startuje jedna gotowa gre.");
            info.add(c + "/pbc (ID ARENY) spectate - Zaczyna spektacje meczu.");
            info.add(c + "/pbc (ID ARENY) add (GRACZ) (RED/BLUE) - Dodaje gracza do oczekujacej druzyny.");
            info.add(c + "/pbc (ID ARENY) remove (GRACZ) (RED/BLUE) - Usuwa gracza z oczekujacej druzyny.");
            info.add("");
            info.add(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "(Strona 1 z 2)");

        } else {
            if (args[1].equalsIgnoreCase("2")) {
                info.add(c + "/pbc (ID ARENY) start - Rozpoczyna gre");
                info.add(c + "/pbc (ID ARENY) autostart - Automatycznie rozpoczyna gre");
                info.add(c + "/pbc (ID ARENY) stop - Zakoncza gre");
                info.add(c + "/pbc (ID ARENY) forcestart - Wymusza rozpoczenie gry");
                info.add(c + "/pbc (ID ARENY) forcenextround - Wymusza nastepna runde");
                info.add(c + "/pbc (ID ARENY) resettempteams - Usuwa wszystkich graczy z oczekujacych druzynyn.");
                info.add(c + "/pbc (ID ARENY) forcetiebreaker - Wymusza tiebreaker (NIE STABILNE)");
                info.add("");
                info.add(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "(Strona 2 z 2)");
            } else {
                player.sendMessage(ProBending.errorPrefix +  "Nie poprawna strona!");
                return;
            }
        }
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
        info.forEach(player::sendMessage);
    }




}
