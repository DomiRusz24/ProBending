package me.domirusz24.pk.probending.probending.arena.team;

import com.projectkorra.projectkorra.BendingPlayer;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.pbgroup.PBGroup;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team
{
    private PBTeamPlayer player1;
    private PBTeamPlayer player2;
    private PBTeamPlayer player3;
    private TeamTag teamTag;
    private Arena arena;
    private PBGroup pbGroup;
    private int points;
    
    public Team(final Player player1, final Player player2, final Player player3, final TeamTag tag, Arena arena) {
        this.points = 0;
        this.arena = arena;
        this.player1 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player1), "WATER", this);
        this.player2 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player2), "WATER", this);
        this.player3 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player3), "WATER", this);
        this.teamTag = tag;
    }

    public Team(TempTeam team, TeamTag tag, Arena arena) {
        this.points = 0;
        this.arena = arena;
        this.player1 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(team.getPlayer1()), "WATER", this);
        this.player2 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(team.getPlayer2()), "WATER", this);
        this.player3 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(team.getPlayer3()), "WATER", this);
        if (team instanceof PBGroup) {
            setPbGroup((PBGroup) team);
        }
        this.teamTag = tag;

    }

    public void setPbGroup(PBGroup pbGroup) {
        if (pbGroup != null) {
            this.pbGroup = pbGroup;
        }
    }
    private void setPlayerTo(int id, Player player) {
        switch(id) {
            case 1: {
                if (player == null) {
                    player1 = null;
                }
                player1 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player), "WATER", this);
                return;
            }
            case 2: {
                if (player == null) {
                    player2 = null;
                }
                player2 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player), "WATER", this);
                return;
            }
            case 3: {
                if (player == null) {
                    player3 = null;
                }
                player3 = new PBTeamPlayer(BendingPlayer.getBendingPlayer(player), "WATER", this);
            }
        }
    }
    
    public PBTeamPlayer getPBPlayer(final Player player) {
        for (final PBTeamPlayer teamPlayer : this.getPBPlayers()) {
            if (player.equals(teamPlayer.getPlayer())) {
                return teamPlayer;
            }
        }
        return null;
    }

    public Arena getArena() {
        return arena;
    }

    public ArrayList<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add(ChatColor.BOLD + "" + this.getColor() + this.getPolishName().toUpperCase() + ":");
        if (pbGroup != null) {
            info.add("DRUZYNA: " + pbGroup.getName());
        }
        int i = 0;
        for (final PBTeamPlayer teamPlayer : getPBPlayers(true)) {
            ++i;
            final String playerstatus = (teamPlayer == null || teamPlayer.getPlayer() == null) ? "NIE DODANY" : teamPlayer.getPlayer().getName();
            if (teamPlayer == null) {
                info.add("Gracz " + i + ": " + playerstatus);
            } else {
                String playerstatuslife = teamPlayer.isKilled() ? " (ODPADL)" : " (NIE ODPADL)";
                String inTieBreaker = teamPlayer.isInTieBreaker() ? "(W TIEBREAKER)" : "";
                info.add("Gracz " + i + ": " + playerstatus + playerstatuslife + " " + inTieBreaker);
            }
        }
        info.add("Punkty: " + getPoints());
        return info;
    }

    public int getPBPlayerNumber(final PBTeamPlayer pbTeamPlayer) {
        int i = 0;
        for (final PBTeamPlayer teamPlayer : this.getPBPlayers()) {
            ++i;
            if (pbTeamPlayer.equals(teamPlayer)) {
                return i;
            }
        }
        return 0;
    }
    
    public PBTeamPlayer getPBPlayer(final int value) {
        switch (value) {
            case 1: {
                return this.player1;
            }
            case 2: {
                return this.player2;
            }
            case 3: {
                return this.player3;
            }
            default: {
                return null;
            }
        }
    }

    public boolean checkWipeOut() {
        if (getPBPlayers().isEmpty()) {
            return true;
        }
        boolean bool = true;
        for (PBTeamPlayer player : this.getPBPlayers()) {
            if (!player.isKilled()) {
                bool = false;
                break;
            }
        }
        return bool;
    }

    public String getPolishName() {
        return this.getTeamTag() == TeamTag.BLUE ? "niebieska" : "czerwona";

    }

    public ChatColor getColor() {
        return this.getTeamTag() == TeamTag.BLUE ? ChatColor.BLUE : ChatColor.RED;
    }

    public TeamTag getEnemyTeamTag() {
        return this.getTeamTag() == TeamTag.BLUE ? TeamTag.RED : TeamTag.BLUE;
    }
    
    public PBTeamPlayer getPlayer1() {
        return this.player1;
    }
    
    public PBTeamPlayer getPlayer2() {
        return this.player2;
    }
    
    public PBTeamPlayer getPlayer3() {
        return this.player3;
    }
    
    public TeamTag getTeamTag() {
        return this.teamTag;
    }
    
    public void raisePoint() {
        ++this.points;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    public ArrayList<Player> getPlayers() {
        final ArrayList<Player> i = new ArrayList<Player>();
        i.add(this.player1 == null ? null : this.player1.getPlayer());
        i.add(this.player2 == null ? null : this.player2.getPlayer());
        i.add(this.player3 == null ? null : this.player3.getPlayer());
        while (i.remove(null)) {
        }
        return i;
    }
    
    public ArrayList<Player> getPlayers(final boolean nullValues) {
        final ArrayList<Player> i = new ArrayList<>();
        i.add(this.player1 == null ? null : this.player1.getPlayer());
        i.add(this.player2 == null ? null : this.player2.getPlayer());
        i.add(this.player3 == null ? null : this.player3.getPlayer());
        if (!nullValues) {
            while (i.remove(null)) {
            }
        }
        return i;
    }
    
    public boolean isFull() {
        return this.player1 != null && this.player2 != null && this.player3 != null;
    }
    
    public ArrayList<PBTeamPlayer> getPBPlayers(final boolean nullValues) {
        final ArrayList<PBTeamPlayer> i = new ArrayList<>();
        i.add(this.player1.getPlayer() == null ? null : this.player1);
        i.add(this.player2.getPlayer() == null ? null : this.player2);
        i.add(this.player3.getPlayer() == null ? null : this.player3);
        if (!nullValues) {
            while (i.remove(null)) {
            }
            i.removeIf(e -> e.getPlayer() == null);
        }
        return i;
    }
    
    public ArrayList<PBTeamPlayer> getPBPlayers() {
        final ArrayList<PBTeamPlayer> i = new ArrayList<>();
        i.add(this.player1.getPlayer() == null ? null : this.player1);
        i.add(this.player2.getPlayer() == null ? null : this.player2);
        i.add(this.player3.getPlayer() == null ? null : this.player3);
        while (i.remove(null)) {
        }
        i.removeIf(e -> e.getPlayer() == null);
        return i;
    }
}
