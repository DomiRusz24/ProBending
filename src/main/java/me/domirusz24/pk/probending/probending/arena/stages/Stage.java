package me.domirusz24.pk.probending.probending.arena.stages;

import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.config.ConfigMethods;
import org.bukkit.Location;

import java.io.IOException;

public class Stage
{
    private final StageEnum stage;
    private final String configEntry;
    
    public Stage(final StageEnum stage, final Arena arena) {
        this.stage = stage;
        configEntry = "Arena.nr" + arena.getID() + ".stages." + stage.toString();
    }
    
    public Location getTeleportByNumber(final int value) {
        switch (value) {
            case 1: {
                return this.getPlayer1Teleport();
            }
            case 2: {
                return this.getPlayer2Teleport();
            }
            case 3: {
                return this.getPlayer3Teleport();
            }
            default: {
                return null;
            }
        }
    }
    
    public void setCenter(final Location center) throws IOException {
        ConfigMethods.saveLocation(configEntry + ".center", center);
    }
    
    public Location getCenter() {
        return ConfigMethods.getLocation(configEntry + ".center");
    }
    
    public Location getPlayer1Teleport() {
        return ConfigMethods.getLocation(configEntry + ".player1");
    }
    
    public void setPlayer1Teleport(final Location player1Teleport) throws IOException {
        ConfigMethods.saveLocation(configEntry + ".player1", player1Teleport);
    }
    
    public Location getPlayer2Teleport() {
        return ConfigMethods.getLocation(configEntry + ".player2");
    }
    
    public void setPlayer2Teleport(final Location player2Teleport) throws IOException {
        ConfigMethods.saveLocation(configEntry + ".player2", player2Teleport);
    }
    
    public Location getPlayer3Teleport() {
        return ConfigMethods.getLocation(configEntry + ".player3");
    }
    
    public void setPlayer3Teleport(final Location player3Teleport) throws IOException {
        ConfigMethods.saveLocation(configEntry + ".player3", player3Teleport);
    }

    public Location getTeleport(StageTeleports teleport) {
        return ConfigMethods.getLocation(configEntry + "." + teleport.getName());
    }

    public void setTeleport(StageTeleports teleport, Location location) throws IOException {
        ConfigMethods.saveLocation(configEntry + "." + teleport.getName(), location);
    }
    
    public StageEnum getStage() {
        return this.stage;
    }
}
