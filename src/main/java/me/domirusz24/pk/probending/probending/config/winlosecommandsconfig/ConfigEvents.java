package me.domirusz24.pk.probending.probending.config.winlosecommandsconfig;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum ConfigEvents {
    ArenaStart("Arena.Start"),
    ArenaWin("Arena.Win"),
    ArenaLose("Arena.Lose"),
    RoundWin("Round.Win"),
    RoundLose("Round.Lose"),
    PlayerDeath("Player.KnockOut"),
    StageClaimPlayer("Player.Stage.Claim"),
    StageLosePlayer("Player.Stage.Lose"),
    PlayerJoinSpectate("Player.Spectate.Join"),
    PlayerLeaveSpectate("Player.Spectate.Leave"),
    PlayerClickLeave("Player.Leave");

    private final String path;

    ConfigEvents(String path) {
        this.path = path;
    }

    public void set(Arena arena, List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ConfigManager.getWinLoseCommands().getConfig().set(getPath(arena), list);
        ConfigManager.getWinLoseCommands().saveConfig();
    }

    public boolean isSet(Arena arena) {
        ConfigManager.getWinLoseCommands().reloadConfig();
        return ConfigManager.getWinLoseCommands().getConfig().isSet(getPath(arena));
    }

    public String getPath() {
        return path;
    }

    public List<String> getCommands(Arena arena) {
        if (isSet(arena)) {
            return ConfigManager.getWinLoseCommands().getConfig().getStringList(getPath(arena));
        }
        return new ArrayList<>();
    }

    public void run(Arena arena) {
        List<String> commands = getCommands(arena);
        for (String com : commands) {
            if (!com.contains("%PLAYER%")) {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), com);
            }
        }

    }

    public void run(Arena arena, ArrayList<Player> players) {
        List<String> commands = getCommands(arena);
        for (String com : commands) {
            if (!com.contains("%PLAYER%")) {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), com);
                continue;
            }
            for (Player player : players) {
                String command = com.replace("%PLAYER%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }
        }
    }

    public void run(Arena arena, Player player) {
        List<String> commands = getCommands(arena);
        for (String com : commands) {
            String command = com.replace("%PLAYER%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        }
    }

    public String getPath(Arena arena) {
        return "Arena" + arena.getID() + "." + path;
    }

    public void setDefault() {
        for (Arena arena : Arena.Arenas) {
            if (isSet(arena)) {
                continue;
            }
            String p = getPath(arena);
            List<String> com = new ArrayList<>();
            com.add("");
            com.add("");
            set(arena, com);
        }
    }
}
