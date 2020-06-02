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
        this.stage = 4;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public Player getPlayer() {
        if (player == null) {
            return null;
        }
        return this.player;
    }

    public void setPlayerToNull() {
        this.player = null;
    }

    public BendingPlayer getBPlayer() {
        if (BPlayer == null) {
            return null;
        }
        return this.BPlayer;
    }

    public int getStage() {
        return StageEnum.getFromID(stage).getID(this.getTeam().getTeamTag());
    }

    public int getStageAbsolute() {
        return StageEnum.getFromID(stage).getID();
    }

    public String getElement() {
        return this.element;
    }

    public void lowerStage() {
        if (player == null) {
            return;
        }
        int add = (!isInTieBreaker() && getStage() == 7) ? -3 : -1;
        this.stage = StageEnum.convertID(getStage() + add, team.getTeamTag());
        this.player.sendTitle("---", ChatColor.BOLD + "" + ChatColor.RED + "Zostales cofniety z strefy " + (this.getStage() - 1) + " na strefe " + this.getStage(), 2, 60, 2);
        getPlayer().getWorld().spawnParticle(Particle.VILLAGER_ANGRY, getPlayer().getLocation().clone().add(0, 4, 0), 30, 0.1, 0.1, 0.1);
        getPlayer().getWorld().spawnParticle(Particle.VILLAGER_ANGRY, getPlayer().getLocation().clone().add(0, 5, 0), 30, 0.1, 0.1, 0.1);
    }

    public void raiseStage() {
        if (player == null) {
            return;
        }
        int add = (!isInTieBreaker() && getStage() == 4) ? 3 : 1;
        this.stage = StageEnum.convertID(getStage() + add, team.getTeamTag());
        Location loc = team.getArena().getStages().get(StageEnum.convertID(getStage(), this.getTeam().getTeamTag())).getTeleportByNumber(getTeam().getPBPlayerNumber(this));
        loc.setPitch(getPlayer().getLocation().getPitch());
        loc.setYaw(getPlayer().getLocation().getYaw());
        getPlayer().teleport(loc);
        this.player.sendTitle("", ChatColor.BOLD + "" + ChatColor.GREEN + "Zdobyles strefe " + getStage() + "!", 2, 40, 2);
        getPlayer().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, getPlayer().getLocation().clone().add(0, 4, 0), 30, 0.1, 0.1, 0.1);
        getPlayer().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, getPlayer().getLocation().clone().add(0, 5, 0), 30, 0.1, 0.1, 0.1);
    }

    public void debug() {
        if (ProBending.plugin.getConfig().getBoolean("debug")) {
            System.out.println(player.getName() + ":");
            System.out.println("AbsStage: " + getStageAbsolute());
            System.out.println("AbsCurrent: " + getStageAbsolute());
            System.out.println("Stage: " + this.getStage());
            System.out.println("Current: " + this.getStage());
            System.out.println("Killed: " + this.killed);
            System.out.println("InGame: " + this.inGame);
        }
    }

    public void debug(String message) {
        if (this.getPlayer() == null) {
            return;
        }
        if (ProBending.plugin.getConfig().getBoolean("debug")) {
            System.out.println(player.getName() + " (REASON: " + message + ")" + ":");
            System.out.println("AbsStage: " + getStageAbsolute());
            System.out.println("AbsCurrent: " + getStageAbsolute());
            System.out.println("Stage: " + this.getStage());
            System.out.println("Current: " + this.getStage());
            System.out.println("Killed: " + this.killed);
            System.out.println("InGame: " + this.inGame);
        }
    }

    public boolean belowStage(int ID, boolean equal) {
        if (player == null) {
            return false;
        }
        return equal ? this.getCurrentStage().getID() <= ID : this.getCurrentStage().getID() < ID;
    }

    public boolean aboveStage(int ID, boolean equal) {
        if (player == null) {
            return false;
        }
        return equal ? this.getCurrentStage().getID() >= ID : this.getCurrentStage().getID() > ID;
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
        StageEnum s = StageEnum.getFromID(StageEnum.getFromData(y.getBlock().getData()).getID(this.getTeam().getTeamTag()));
        if (s.equals(StageEnum.Line)) {
            return StageEnum.getFromID(getStage());
        }
        if (s.getID() == 5 || s.getID() == 6) {
            if (!this.isInTieBreaker()) {
                return s.getID() == 5 ? StageEnum.getFromID(4) : StageEnum.getFromID(7);
            }
        }
        return s;
    }

    public StageEnum getCurrentStageAbsolute() {
        if (player == null) {
            return null;
        }
        Location y = this.getPlayer().getLocation();
        y.setY(ProBending.plugin.getConfig().getInt("stage.y"));
        StageEnum s = StageEnum.getFromID(StageEnum.getFromData(y.getBlock().getData()).getID());
        if (s.equals(StageEnum.Line)) {
            return StageEnum.getFromID(getStage());
        }
        if (s.getID() == 5 || s.getID() == 6) {
            if (!this.isInTieBreaker()) {
                return s.getID() == 5 ? StageEnum.getFromID(4) : StageEnum.getFromID(7);
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
