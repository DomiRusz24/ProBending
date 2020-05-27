package me.domirusz24.pk.probending.probending.arena;

import org.bukkit.entity.*;
import com.projectkorra.projectkorra.*;
import org.bukkit.*;

public class PBTeamPlayer
{
    private Player player;
    private BendingPlayer BPlayer;
    private boolean inTieBreaker;
    private boolean inGame;
    private boolean killed;
    private String element;
    private int stage;
    private Team team;
    
    public PBTeamPlayer(final BendingPlayer BPlayer, final String element, final Team team) {
        this.inTieBreaker = false;
        this.inGame = true;
        this.killed = false;
        this.element = element;
        this.BPlayer = BPlayer;
        this.player = BPlayer.getPlayer();
        this.team = team;
        this.stage = ((team.getTeamTag() == TeamTag.RED) ? 4 : 5);
    }
    
    public void removePlayer() {
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.teleport(Arena.spawn);
        this.inGame = false;
        Arena.playersPlaying.remove(player);
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public BendingPlayer getBPlayer() {
        return this.BPlayer;
    }
    
    public int getStage() {
        return this.stage;
    }
    
    public String getElement() {
        return this.element;
    }
    
    public void lowerStage() {
        this.stage = ((this.team.getTeamTag() == TeamTag.RED) ? (this.stage - 1) : (this.stage + 1));
        this.player.sendTitle("", ChatColor.BOLD + "" + ChatColor.RED + "Zostales cofniety o strefe!", 2, 20, 2);
    }
    
    public void raiseStage() {
        this.stage = ((this.team.getTeamTag() == TeamTag.RED) ? (this.stage + 1) : (this.stage - 1));
        this.player.sendTitle("", ChatColor.BOLD + "" + ChatColor.GREEN + "Zdobyles strefe!", 2, 50, 2);
    }
    
    public boolean isInGame() {
        if (this.stage > 8 || this.stage < 1 || this.killed) {
            this.inGame = false;
        }
        return this.inGame;
    }
    
    public Team getTeam() {
        return this.team;
    }
    
    public boolean isKilled() {
        return this.killed;
    }
    
    public void setKilled(final boolean value) {
        this.killed = value;
    }
    
    public StageEnum getCurrentStage() {
        return StageEnum.getFromBiome(this.getPlayer().getLocation().getBlock().getBiome().toString());
    }
    
    public boolean isInTieBreaker() {
        return this.inTieBreaker;
    }
    
    public void setInTieBreaker(final boolean inTieBreaker) {
        this.inTieBreaker = inTieBreaker;
    }
    
    public void setStage(final int value) {
        this.stage = value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof Player && this.getPlayer().equals(obj)) || super.equals(obj);
    }
}
