package me.domirusz24.pk.probending.probending.arena.team;

import com.projectkorra.projectkorra.BendingPlayer;
import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.misc.Elements;
import me.domirusz24.pk.probending.probending.arena.stages.StageEnum;
import me.domirusz24.pk.probending.probending.arena.stages.StageTeleports;
import me.domirusz24.pk.probending.probending.config.winlosecommandsconfig.ConfigEvents;
import me.domirusz24.pk.probending.probending.data.PlayerData;
import me.domirusz24.pk.probending.probending.data.datatype.PlayerDataType;
import me.domirusz24.pk.probending.probending.misc.TempInventory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class PBTeamPlayer
{
    private Player player;
    private final BendingPlayer BPlayer;
    private boolean inTieBreaker;
    private boolean inGame;
    private boolean killed;
    private final Elements element;
    private final Arena arena;
    private TempInventory tempInventory;
    private boolean hasntGainedStageYet = false;
    private final HashMap<PlayerDataType, Integer> tempStats = new HashMap<>();
    private PlayerData data;
    private int tiredMeter = 0;
    private int illegalStageCombo = 0;
    private final int ID;
    private int stage;
    private final Team team;

    public PBTeamPlayer(final BendingPlayer BPlayer, final Elements element, final Team team) {
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
            tempInventory = new TempInventory(player);
            data = new PlayerData(player);
            for (PlayerDataType e : PlayerDataType.values()) {
                tempStats.put(e, data.getData(e));
            }
            tempInventory.remove();

        }
        this.ID = team.getPBPlayerNumber(this);
        this.team = team;
        this.arena = team.getArena();
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
        return this.BPlayer;
    }

    public PlayerData getData() {
        return data;
    }

    public void setData(PlayerDataType type, int value) {
        tempStats.put(type, value);
    }

    public void raiseData(PlayerDataType type, int value) {
        int t = tempStats.get(type);
        t+= value;
        tempStats.put(type, t);
    }

    public void transferData() {
        for (PlayerDataType e : PlayerDataType.values()) {
            data.setData(e, tempStats.get(e));
        }
    }

    public int getStage() {
        return StageEnum.getFromID(stage).getID(this.getTeam().getTeamTag());
    }

    public int getStageAbsolute() {
        return StageEnum.getFromID(stage).getID();
    }

    public Elements getElement() {
        return this.element;
    }

    public void lowerStage() {
        if (player == null) {
            return;
        }
        debug("BEFORE LOWER");
        int add = (!isInTieBreaker() && getStage() == 7) ? -3 : -1;
        this.stage = StageEnum.convertID(getStage() + add, team.getTeamTag());
        for (Player p : arena.getAllPlayers()) {
            p.sendMessage(ChatColor.BOLD + "" + ChatColor.GRAY + "Gracz " + getTeamName() + " zostal cofniety o strefe!");
        }
        arena.broadcastTitleSpectatorsOnly("", ChatColor.GRAY + "Gracz " + getTeamName() + " zostal cofniety o stefe!", 5, 40, 5);
        if (getStage() == 1) {
            debug("BEFORE KILL");
            arena.killPlayer(this);
            debug("AFTER KILL");
        } else {
            this.player.sendTitle("---", ChatColor.BOLD + "" + ChatColor.RED + "Zostales cofniety z strefy " + (getStage() - 1) + " na strefe " + getStage(), 2, 60, 2);
            getPlayer().getWorld().spawnParticle(Particle.VILLAGER_ANGRY, getPlayer().getLocation().clone().add(0, 4, 0), 30, 0.1, 0.1, 0.1);
            getPlayer().getWorld().spawnParticle(Particle.VILLAGER_ANGRY, getPlayer().getLocation().clone().add(0, 5, 0), 30, 0.1, 0.1, 0.1);
            if (getStage() != getCurrentStage().getID()) {
                teleportToStage(getStageAbsolute(), StageTeleports.getStageFromNumber(ID));
            } else {
                dragToStage(getStageAbsolute(), StageTeleports.Center, 0.3);
            }
            ConfigEvents.StageLosePlayer.run(getArena(), player);
            if (getArena().checkForEmptyStage(getStage(), team.getTeamTag())) {
                getArena().claimStage(team.getEnemyTeamTag());
            }
        }
        debug("AFTER LOWER");
    }

    public void resetIllegalCombo() {
        this.illegalStageCombo = 0;
    }

    public void raiseStage() {
        if (player == null) {
            return;
        }
        int add = (!isInTieBreaker() && getStage() == 4) ? 3 : 1;
        this.stage = StageEnum.convertID(getStage() + add, team.getTeamTag());
        if (getCurrentStage().getID() != getStage()) {
            teleportToStage(getStageAbsolute(), StageTeleports.getStageFromNumber(ID));
        } else {
            dragToStage(getStageAbsolute(), StageTeleports.getStageFromNumber(ID), 0.3);
        }
        this.player.sendTitle("", ChatColor.BOLD + "" + ChatColor.GREEN + "Zdobyles strefe " + getStage() + "!", 2, 40, 2);
        getPlayer().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, getPlayer().getLocation().clone().add(0, 4, 0), 30, 0.1, 0.1, 0.1);
        getPlayer().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, getPlayer().getLocation().clone().add(0, 5, 0), 30, 0.1, 0.1, 0.1);
    }

    public String getTeamName() {
        return team.getColor() + player.getName() + ChatColor.RESET;
    }

    public int getTiredMeter() {
        return tiredMeter;
    }

    public void setTiredMeter(int value) {
        this.tiredMeter = value;
        if (value > 300) {
            tiredMeter = 300;
        }
    }

    public void increaseTiredMeter(int value) {
        this.tiredMeter+= value;
        if (value > 300) {
            tiredMeter = 300;
        }
        arena.updateScoreboard();
    }

    public void illegalStageGain() {
        debug("BEFORE WARNING");
        player.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Jeszcze nie przejales strefy " + getCurrentStageAbsolute().polishName() + "!");
        dragToStage(getStageAbsolute(), StageTeleports.Center, 0.5);
        debug("AFTER WARNING");
    }

    public void teleportToStage(int stage, StageTeleports teleport) {
        if (teleport == null) {
            return;
        }
        Location loc = getArena().getStages().get(stage).getTeleport(teleport);
        if (StageEnum.getFromID(stage).getTeamTag() == team.getEnemyTeamTag()) {
            loc.setDirection(loc.getDirection().multiply(-1));
        }
        getPlayer().teleport(loc);
    }

    public void dragToStage(int stage, StageTeleports teleport, double power) {
        if (illegalStageCombo >= 3) {
            teleportToStage(stage, teleport);
            return;
        }
        Location l = getArena().getStages().get(stage).getTeleport(teleport).clone();
        l.add(0, 4, 0);
        Vector vector = l.subtract(player.getPlayer().getLocation()).toVector().normalize();
        double y = vector.getY();
        vector.setY(0);
        vector.normalize();
        vector.multiply(power);
        vector.setY(y * power * 1.5);
        getPlayer().setVelocity(vector);
        illegalStageCombo++;
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

    @SuppressWarnings({"deprecated"})
    public StageEnum getCurrentStage() {
        if (player == null) {
            return null;
        }
        Location y = this.getPlayer().getLocation();
        y.setY(ProBending.plugin.getConfig().getInt("stage.y"));
        @SuppressWarnings("deprecation") StageEnum s = StageEnum.getFromID(StageEnum.getFromData(y.getBlock().getData()).getID(getTeam().getTeamTag()));
        if (s.equals(StageEnum.Line)) {
            return StageEnum.getFromID(getStage());
        }
        if (s.getID() == 5 || s.getID() == 6) {
            if (!isInTieBreaker()) {
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
        //noinspection deprecation
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

    public Arena getArena() {
        return arena;
    }

    public void revertInventory() {
        tempInventory.revert();
    }

    public boolean isInTieBreaker() {
        return this.inTieBreaker;
    }

    public int getID() {
        return ID;
    }

    public void setInTieBreaker(final boolean inTieBreaker) {
        this.inTieBreaker = inTieBreaker;
    }

    public void setStage(final int value) {
        this.stage = value;
    }

    public boolean hasntGainedStageYet() {
        return hasntGainedStageYet;
    }

    public void setHasntGainedStageYet(boolean hasntGainedStageYet) {
        this.hasntGainedStageYet = hasntGainedStageYet;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof Player && this.getPlayer().equals(obj)) || super.equals(obj);
    }
}
