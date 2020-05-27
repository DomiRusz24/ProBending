package me.domirusz24.pk.probending.probending.arena;

import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PBTeamPlayer {
    private Player player;
    private BendingPlayer BPlayer;
    private boolean inGame = true;
    private String element;
    private int stage;
    private Team team;

    public PBTeamPlayer(BendingPlayer BPlayer, String element, Team team) {
        this.element = element;
        this.BPlayer = BPlayer;
        this.player = BPlayer.getPlayer();
        this.team = team;
        this.stage = team.getTeamTag() == TeamTag.RED ? 4 : 5;
    }

    public void removePlayer() {
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(Arena.spawn);
        inGame = false;
    }

    public Player getPlayer() {
        return player;
    }

    public BendingPlayer getBPlayer() {
        return BPlayer;
    }

    public int getStage() {
        return stage;
    }

    public String getElement() {
        return element;
    }

    public void lowerStage() {
        this.stage = team.getTeamTag() == TeamTag.RED ? this.stage - 1 : this.stage + 1;
        this.player.sendTitle("", ChatColor.BOLD + "" + ChatColor.RED +  "Zostales cofniety o strefe!", 2,20,2);
    }
    public void raiseStage() {
        this.stage = team.getTeamTag() == TeamTag.RED ? this.stage + 1 : this.stage - 1;
        this.player.sendTitle("", ChatColor.BOLD + "" + ChatColor.GREEN + "Zdobyles strefe!", 2,50,2);
    }

    public boolean isInGame() {
        if (stage > 8 || stage < 1) {
            inGame = false;
        }
        return inGame;
    }

    public Team getTeam() {
        return team;
    }



}
