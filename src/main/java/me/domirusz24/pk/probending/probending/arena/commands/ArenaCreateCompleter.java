package me.domirusz24.pk.probending.probending.arena.commands;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import me.domirusz24.pk.probending.probending.arena.*;
import java.util.*;

public class ArenaCreateCompleter implements TabCompleter
{
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("arena")) {
            final List<String> complete = new ArrayList<String>();
            if (args.length == 1) {
                complete.add("create");
                complete.add("setspawn");
                for (final Arena arena : Arena.Arenas) {
                    complete.add(String.valueOf(arena.getID()));
                }
            }else
            if (args.length == 2) {
                for (final StageEnum e : StageEnum.values()) {
                    complete.add(e.toString());
                }
            }else
            if (args.length == 3) {
                complete.add("player1");
                complete.add("player2");
                complete.add("player3");
                complete.add("center");
            }else
            if (args.length == 4) {
                complete.add("teleport");
            }
            return complete;
        }
        return null;
    }
}
