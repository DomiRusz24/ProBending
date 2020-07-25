package me.domirusz24.pk.probending.probending.misc;

import me.domirusz24.pk.probending.probending.ProBending;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomScoreboard {


    private final ArrayList<Player> players = new ArrayList<>();
    private final HashMap <Integer, CustomScore> display = new HashMap<>();
    private final String displayName;
    private final String name;
    private final DisplaySlot displaySlot;
    private final Scoreboard scoreboard;
    private Objective objective;

    public CustomScoreboard(String name, String displayName, DisplaySlot displaySlot) {
        scoreboard = ProBending.scoreboardManager.getNewScoreboard();
        objective = scoreboard.registerNewObjective(name, "dummy");
        this.name = name;
        this.displayName = displayName;
        this.displaySlot = displaySlot;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            player.setScoreboard(scoreboard);
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            player.setScoreboard(ProBending.scoreboardManager.getNewScoreboard());
        }
    }

    public void removeAll() {
        for (Player player : players) {
            player.setScoreboard(ProBending.scoreboardManager.getNewScoreboard());
        }
        players.clear();
    }

    public void update() {
        objective.unregister();
        objective = scoreboard.registerNewObjective(name, "dummy");
        objective.setDisplayName(displayName);
        objective.setDisplaySlot(displaySlot);
        for (int i = 0; i < display.size(); i++) {
            objective.getScore(display.get(i).getCombined()).setScore(display.size() - i);
        }
        for (Player p : players) {
            p.setScoreboard(scoreboard);
        }
    }

    public void reset() {
        display.clear();
    }

    public void edit(int place, String string) {
        display.putIfAbsent(place, new CustomScore(string));
        display.get(place).setString(string);
    }

    public void edit(int place, String string, String value) {
        display.putIfAbsent(place, new CustomScore(string, value));
        display.get(place).setString(string);
        display.get(place).setValue(value);
    }

    public void setDisplayName(String name) {
        objective.setDisplayName(name);
    }
}
