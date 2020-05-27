package me.domirusz24.pk.probending.probending.arena;

import me.domirusz24.pk.probending.probending.ProBending;
import org.bukkit.Location;

public class Stage {

    private Location center;
    private Location player1Teleport;
    private Location player2Teleport;
    private Location player3Teleport;
    private final StageEnum stage;

    public Stage(StageEnum stage, Arena arena) {
        this.stage = stage;
        String configEntry = "Arena." + arena.getID() + ".stages." + stage + ".";
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public Location getCenter() {
        return center;
    }

    public Location getPlayer1Teleport() {
        return player1Teleport;
    }

    public void setPlayer1Teleport(Location player1Teleport) {
        this.player1Teleport = player1Teleport;
    }

    public Location getPlayer2Teleport() {
        return player2Teleport;
    }

    public void setPlayer2Teleport(Location player2Teleport) {
        this.player2Teleport = player2Teleport;
    }

    public Location getPlayer3Teleport() {
        return player3Teleport;
    }

    public void setPlayer3Teleport(Location player3Teleport) {
        this.player3Teleport = player3Teleport;
    }

    public StageEnum getStage() {
        return stage;
    }
}
