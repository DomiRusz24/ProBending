package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.StageEnum;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArenaCreateCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            if (command.getName().equalsIgnoreCase("arena")) {
                List<String> complete = new ArrayList<>();
                if (args.length == 0) {
                    complete.add("create");
                    complete.add("setspawn");
                    for (Arena arena : Arena.Arenas) {
                        complete.add(String.valueOf(arena.getID()));
                    }
                }
                if (args.length == 1) {
                    for (StageEnum e : StageEnum.values()) {
                        complete.add(e.name());
                    }
                }
                if (args.length == 2) {
                    complete.add("player1");
                    complete.add("player2");
                    complete.add("player3");
                    complete.add("center");
                }

                return complete;
            }

        }
        return null;
    }
}
