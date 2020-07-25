package me.domirusz24.pk.probending.probending.arena.misc;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.team.PBTeamPlayer;
import me.domirusz24.pk.probending.probending.arena.team.Team;
import me.domirusz24.pk.probending.probending.arena.team.TeamTag;
import me.domirusz24.pk.probending.probending.config.ConfigMethods;
import me.domirusz24.pk.probending.probending.misc.GeneralMethods;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class HologramManager {


    private Hologram arenaInfo;
    private Hologram teamBlue;
    private Hologram teamRed;
    private final Arena arena;

    public HologramManager(Arena arena) {
        this.arena = arena;
        refreshInfo();
    }

    public Arena getArena() {
        return arena;
    }

    public void refreshInfo() {
        if (getLocation() == null) {
            return;
        }
        if (arenaInfo == null) {
            replace();
        }
        arenaInfo.clearLines();
        teamBlue.clearLines();
        teamRed.clearLines();
        getArenaInfo().forEach(arenaInfo::appendTextLine);
        getTeamInfo(TeamTag.BLUE).forEach(teamBlue::appendTextLine);
        getTeamInfo(TeamTag.RED).forEach(teamRed::appendTextLine);
    }

    public void replace() {
        if (getLocation() == null) {
            return;
        }
        Location info = getLocation();
        Vector e = GeneralMethods.rotateVectorAroundY(getLocation().getDirection(), 90);
        e.add(new Vector(0, -0.8, 0));
        Location blue = getLocation().clone().add(e.clone().multiply(new Vector(2, 1, 2)));
        Location red = getLocation().clone().add(e.clone().multiply(new Vector(-2, 1, -2)));
        if (arenaInfo == null) {
            arenaInfo = HologramsAPI.createHologram(ProBending.plugin, info);
            teamBlue = HologramsAPI.createHologram(ProBending.plugin, blue);
            teamRed = HologramsAPI.createHologram(ProBending.plugin, red);
        } else {
            arenaInfo.teleport(info);
            teamBlue.teleport(blue);
            teamRed.teleport(red);
        }
    }

    public List<String> getArenaInfo() {
        List<String> info = new ArrayList<>();
        if (arena.isInGame()) {
            info.add(ChatColor.BOLD + "" + ChatColor.RED + "Arena zajeta! Arena ID: " + getArena().getID() + ".");
            info.add(ChatColor.BLUE + "Czas rundy: " + (Math.round((double) arena.getRoundTime() / 20 / 60)) + " minut.");
            if (arena.isInTieBreaker()) {
                info.add(ChatColor.GRAY + "W tiebreaker!");
            } else {
                info.add(ChatColor.GRAY + "Runda: " + arena.getRoundNumber());
            }
        } else {
            info.add(ChatColor.BOLD + "" + ChatColor.GREEN + "Arena wolna! Arena ID: " + getArena().getID() + ".");
        }
        return info;
    }

    public List<String> getTeamInfo(TeamTag tag) {
        Team team = getArena().getTeamByTag(tag);
        List<String> info = new ArrayList<>();
        if (arena.isInGame()) {
            info.add(ChatColor.BOLD + "" + team.getColor() + "Druzyna " + team.getPolishName() + ":");
            info.add(ChatColor.GOLD + "Punkty: " + team.getPoints());
            for (PBTeamPlayer p : team.getPBPlayers(true)) {
                if (p == null) {
                    info.add("");
                } else {
                    if (getArena().isInTieBreaker()) {
                        String TB = p.isInTieBreaker() ? " (Walczy w TieBreaker!)" : "";
                        ChatColor TBC = p.isInTieBreaker() ? ChatColor.GREEN : ChatColor.GRAY;
                        info.add(p.getElement().getPolish() + " " + TBC + p.getPlayer().getName() + TB);
                    } else {
                        String TB = p.isKilled() ? "" : "";
                        ChatColor TBC = p.isKilled() ? ChatColor.RED : ChatColor.GREEN;
                        info.add(p.getElement().getPolish() + " " + TBC + p.getPlayer().getName() + TB);
                    }
                }
            }
        }
        return info;
    }

    public List<String> getInfo() {
        List<String> info = new ArrayList<>();
        info.add(ChatColor.BLUE + "Hologram info in Arena " + getArena().getID());
        info.add(ChatColor.GRAY + "Location: " + (getLocation() == null ? "Not set!" : " X: " + getLocation().getX() + " Y: " + getLocation().getY() + " Z: " + getLocation().getZ() + " World: " + getLocation().getWorld()));
        return info;
    }
    
    public Location getLocation() {
        return ConfigMethods.getLocation("Arena.nr" + arena.getID() + ".hologram");
    }

    public void setLocation(Location location) {
        ConfigMethods.saveLocation("Arena.nr" + arena.getID() + ".hologram", location);
        replace();
        refreshInfo();
    }
}
