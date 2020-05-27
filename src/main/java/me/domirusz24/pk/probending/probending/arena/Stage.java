package me.domirusz24.pk.probending.probending.arena;

import org.bukkit.*;

public class Stage
{
    private Location center;
    private Location player1Teleport;
    private Location player2Teleport;
    private Location player3Teleport;
    private final StageEnum stage;
    
    public Stage(final StageEnum stage, final Arena arena) {
        this.stage = stage;
        final String configEntry = "Arena." + arena.getID() + ".stages." + stage + ".";
    }
    
    public Location getTeleportByNumber(final int value) {
        switch (value) {
            case 1: {
                return this.player1Teleport;
            }
            case 2: {
                return this.player2Teleport;
            }
            case 3: {
                return this.player3Teleport;
            }
            default: {
                return null;
            }
        }
    }
    
    public void setCenter(final Location center) {
        this.center = center;
    }
    
    public Location getCenter() {
        return this.center;
    }
    
    public Location getPlayer1Teleport() {
        return this.player1Teleport;
    }
    
    public void setPlayer1Teleport(final Location player1Teleport) {
        this.player1Teleport = player1Teleport;
    }
    
    public Location getPlayer2Teleport() {
        return this.player2Teleport;
    }
    
    public void setPlayer2Teleport(final Location player2Teleport) {
        this.player2Teleport = player2Teleport;
    }
    
    public Location getPlayer3Teleport() {
        return this.player3Teleport;
    }
    
    public void setPlayer3Teleport(final Location player3Teleport) {
        this.player3Teleport = player3Teleport;
    }
    
    public StageEnum getStage() {
        return this.stage;
    }
}
