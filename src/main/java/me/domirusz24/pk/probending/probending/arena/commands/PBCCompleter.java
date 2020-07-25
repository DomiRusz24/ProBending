package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.misc.GeneralMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PBCCompleter implements TabCompleter
{
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("pbc")) {
            final List<String> complete = new ArrayList<>();
            if (args.length == 1) {
                if (sender.hasPermission("probending.autostart")) {
                    complete.add("autostart");
                }
                complete.add("stopspectate");
                complete.add("rules");
                for (final Arena arena : Arena.Arenas) {
                    complete.add(String.valueOf(arena.getID()));
                }
            }
            if (args.length == 2) {
                if (sender.hasPermission("probending.arena.modify") || sender.isOp()) {
                    complete.add("add");
                    complete.add("start");
                    complete.add("autostart");
                    complete.add("stop");
                    complete.add("remove");
                    complete.add("teleport");
                    complete.add("forcestart");
                    complete.add("resetTempTeams");
                    complete.add("forceNextRound");
                }
                complete.add("spectate");
            }
            if (args.length == 3) {
                if (sender.hasPermission("probending.arena.modify") || sender.isOp()) {
                    for (final Player player : ProBending.plugin.getServer().getOnlinePlayers()) {
                        if (!Arena.playersPlaying.contains(player)) {
                            complete.add(player.getName());
                        }
                    }
                }
            }
            if (args.length == 4) {
                if (sender.hasPermission("probending.arena.modify") || sender.isOp()) {
                    complete.add("blue");
                    complete.add("red");
                }
            }
            return GeneralMethods.getPossibleCompletions(args, complete);
        }
        return null;
    }
}
