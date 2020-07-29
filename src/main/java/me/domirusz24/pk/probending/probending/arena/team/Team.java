package me.domirusz24.pk.probending.probending.arena.team;

import com.projectkorra.projectkorra.BendingPlayer;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.misc.GeneralMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team
{
    private PBTeamPlayer player1;
    private PBTeamPlayer player2;
    private PBTeamPlayer player3;
    private final TeamTag teamTag;
    private final Arena arena;
    private int points;

    public Team(TempTeam team, TeamTag tag, Arena arena) {
        this.points = 0;
        this.arena = arena;
        for (int i = 0; i < 3; i++) {
            if (team.getPlayer(i + 1) == null) {
                continue;
            }
            BendingPlayer bp = BendingPlayer.getBendingPlayer(team.getPlayer(i + 1));
            setPlayerTo(i + 1, new PBTeamPlayer(bp, GeneralMethods.getPlayerElement(bp), this));
        }
        this.teamTag = tag;
    }
    private void setPlayerTo(int id, PBTeamPlayer player) {
        switch(id) {
            case 1: {
                if (player == null) {
                    player1 = null;
                }
                player1 = player;
                return;
            }
            case 2: {
                if (player == null) {
                    player2 = null;
                }
                player2 = player;
                return;
            }
            case 3: {
                if (player == null) {
                    player3 = null;
                }
                player3 = player;
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
    
    public TeamTag getTeamTag() {
        return this.teamTag;
    }
    
    public void raisePoint() {
        ++this.points;
    }
    
    public int getPoints() {
        return this.points;
    }
    
    @SuppressWarnings("StatementWithEmptyBody")
    public ArrayList<Player> getPlayers() {
        final ArrayList<Player> i = new ArrayList<>();
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
        if (this.player1 != null) {
            i.add(this.player1.getPlayer() == null ? null : this.player1);
        } else {
            i.add(null);
        }
        if (this.player2 != null) {
            i.add(this.player2.getPlayer() == null ? null : this.player2);
        } else {
            i.add(null);
        }
        if (this.player3 != null) {
            i.add(this.player3.getPlayer() == null ? null : this.player3);
        } else {
            i.add(null);
        }
        if (!nullValues) {
            while (i.remove(null)) {
            }
            i.removeIf(e -> e.getPlayer() == null);
        }
        return i;
    }
    
    public ArrayList<PBTeamPlayer> getPBPlayers() {
        final ArrayList<PBTeamPlayer> i = new ArrayList<>();
        if (this.player1 != null) {
            i.add(this.player1.getPlayer() == null ? null : this.player1);
        }
        if (this.player2 != null) {
            i.add(this.player2.getPlayer() == null ? null : this.player2);
        }
        if (this.player3 != null) {
            i.add(this.player3.getPlayer() == null ? null : this.player3);
        }
        while (i.remove(null)) {
        }
        i.removeIf(e -> e.getPlayer() == null);
        return i;
    }
}
