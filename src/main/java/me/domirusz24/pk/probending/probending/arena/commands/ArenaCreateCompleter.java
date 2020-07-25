package me.domirusz24.pk.probending.probending.arena.commands;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.misc.ArenaGetters;
import me.domirusz24.pk.probending.probending.arena.stages.StageEnum;
import me.domirusz24.pk.probending.probending.arena.stages.StageTeleports;
import me.domirusz24.pk.probending.probending.misc.GeneralMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaCreateCompleter implements TabCompleter
{
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("arena")) {
            final List<String> complete = new ArrayList<>();
            if (args.length == 1) {
                if (sender.hasPermission("probending.arena.create") || sender.isOp()) {
                    complete.add("create");
                }
                if (sender.hasPermission("probending.arena.config") || sender.isOp()) {
                    for (final Arena arena : Arena.Arenas) {
                        complete.add(String.valueOf(arena.getID()));
                    }
                    complete.add("setSpawn");
                    complete.add("getSpawn");
                    complete.add("setArenaList");
                    complete.add("getArenaList");
                }
            }else
            if (args.length == 2) {
                if (sender.hasPermission("probending.arena.config") || sender.isOp()) {
                    complete.add("set");
                    complete.add("get");
                }
            }else
            if (args.length == 3) {
                if (sender.hasPermission("probending.arena.config") || sender.isOp()) {
                    for (StageEnum e : StageEnum.values()) {
                        if (e.equals(StageEnum.Line) || e.equals(StageEnum.WholeArena) || e.equals(StageEnum.BackRED) || e.equals(StageEnum.BackBLUE)) {
                            continue;
                        }
                        complete.add(e.toString());
                    }
                    for (ArenaGetters e : ArenaGetters.values()) {
                        complete.add(e.getName());
                    }
                    complete.add("RollBack");
                    complete.add("TBStage");
                    complete.add("hologram");
                    complete.add("center");
                }
            }else
            if (args.length == 4) {
                if (sender.hasPermission("probending.arena.config") || sender.isOp()) {
                    if (args[2].equalsIgnoreCase("tbstage")) {
                        for (int i = 1; i < 11; i++) {
                            complete.add(String.valueOf(i));
                        }
                    }
                    ArrayList<String> triggers = new ArrayList<>();
                    for (StageEnum e : StageEnum.values()) {
                        assert e.toString() != null;
                        triggers.add(e.toString().toLowerCase());
                    }
                    if (triggers.contains(args[2].toLowerCase())) {
                        for (StageTeleports s : StageTeleports.values()) {
                            complete.add(s.getShortcut());
                        }
                    }
                }
            }
            return GeneralMethods.getPossibleCompletions(args, complete);
        }
        return null;
    }
}
