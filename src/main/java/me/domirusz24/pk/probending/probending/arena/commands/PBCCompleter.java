package me.domirusz24.pk.probending.probending.arena.commands;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import me.domirusz24.pk.probending.probending.arena.*;
import me.domirusz24.pk.probending.probending.*;
import java.util.*;

public class PBCCompleter implements TabCompleter
{
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("pbc")) {
            final List<String> complete = new ArrayList<String>();
            if (args.length == 1) {
                for (final Arena arena : Arena.Arenas) {
                    complete.add(String.valueOf(arena.getID()));
                }
            }
            if (args.length == 2) {
                complete.add("stop");
                complete.add("add");
                complete.add("remove");
                complete.add("start");
            }
            if (args.length == 3) {
                for (final Player player : ProBending.plugin.getServer().getOnlinePlayers()) {
                    if (!Arena.playersPlaying.contains(player)) {
                        complete.add(player.getName());
                    }
                }
            }
            if (args.length == 4) {
                complete.add("blue");
                complete.add("red");
            }
            return complete;
        }
        return null;
    }
}
