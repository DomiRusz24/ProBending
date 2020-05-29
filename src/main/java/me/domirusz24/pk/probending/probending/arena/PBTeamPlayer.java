package me.domirusz24.pk.probending.probending.arena;

import me.domirusz24.pk.probending.probending.ProBending;
import org.bukkit.entity.*;
import com.projectkorra.projectkorra.*;
import org.bukkit.*;
import org.bukkit.material.MaterialData;

public class PBTeamPlayer
{
    private Player player;
    private BendingPlayer BPlayer;
    private boolean inTieBreaker = false;
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
        if (BPlayer == null || BPlayer.getPlayer() == null) {
            this.BPlayer = null;
            this.player = null;
        } else {
            this.BPlayer = BPlayer;
            this.player = BPlayer.getPlayer();
        }
        this.team = team;
        this.stage = ((team.getTeamTag() == TeamTag.RED) ? 4 : 6);
    }
    
    public void removePlayer() {
        if (player == null) {
            return;
        }
        this.player.setGameMode(GameMode.SURVIVAL);
        this.player.teleport(Arena.spawn());
        this.inGame = false;
        Arena.playersPlaying.remove(player);
    }
    
    public Player getPlayer() {
        if (player == null) {
            return null;
        }
        return this.player;
    }
    
    public BendingPlayer getBPlayer() {
        if (BPlayer == null) {
            return null;
        }
        return this.BPlayer;
    }
    
    public int getStage() {
        return this.stage;
    }
    
    public String getElement() {
        return this.element;
    }
    
    public void lowerStage() {
        if (player == null) {
            return;
        }
        this.setStage((this.team.getTeamTag() == TeamTag.RED) ? (this.stage - 1) : (this.stage + 1));
        this.player.sendTitle("", ChatColor.BOLD + "" + ChatColor.RED + "Zostales cofniety z strefy " + (this.getStage() - 1) + " na strefe " + this.getStage(), 2, 20, 2);
    }
    
    public void raiseStage() {
        if (player == null) {
            return;
        }
        this.stage = ((this.team.getTeamTag() == TeamTag.RED) ? (this.stage + 1) : (this.stage - 1));
        this.player.sendTitle("", ChatColor.BOLD + "" + ChatColor.GREEN + "Zdobyles strefe " + getStage(), 2, 50, 2);
    }

    public boolean belowStage(int ID, boolean equal) {
        if (player == null) {
            return false;
        }
        if (this.getTeam().getTeamTag().equals(TeamTag.BLUE)) {
            return equal ? this.getCurrentStage().getID() >= ID : this.getCurrentStage().getID() > ID;
        } else {
            return equal ? this.getCurrentStage().getID() <= ID : this.getCurrentStage().getID() < ID;
        }
    }

    public boolean aboveStage(int ID, boolean equal) {
        if (player == null) {
            return false;
        }
        if (this.getTeam().getTeamTag().equals(TeamTag.BLUE)) {
            return equal ? this.getCurrentStage().getID() <= ID : this.getCurrentStage().getID() < ID;
        } else {
            return equal ? this.getCurrentStage().getID() >= ID : this.getCurrentStage().getID() > ID;
        }
    }
    
    public boolean isInGame() {
        if (this.stage == 8 || this.stage == 1 || this.killed) {
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
        if (player == null) {
            return null;
        }
        Location y = this.getPlayer().getLocation();
        y.setY(ProBending.plugin.getConfig().getInt("stage.y"));
        StageEnum s = StageEnum.getFromData(y.getBlock().getData());
        if (s.equals(StageEnum.Line)) {
            return StageEnum.getFromID(getStage());
        }
        if (s.equals(StageEnum.TieBreakerRED) || s.equals(StageEnum.TieBreakerBLUE)) {
            if (!this.isInTieBreaker()) {
                return s == StageEnum.TieBreakerRED ? StageEnum.FirstRED : StageEnum.FirstBLUE;
            }
        }
        return s;
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
