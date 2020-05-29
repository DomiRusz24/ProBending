package me.domirusz24.pk.probending.probending.arena;

import org.bukkit.entity.*;
import me.domirusz24.pk.probending.probending.arena.temp.*;
import org.bukkit.*;
import org.bukkit.scheduler.*;
import me.domirusz24.pk.probending.probending.*;
import org.bukkit.plugin.*;
import org.graalvm.compiler.hotspot.EconomyCompilerConfigurationFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.*;

public class Arena
{

    public static Location spawn() {
        return ConfigMethods.getLocation("spawn");
    }

    public static void setSpawn(Location location) throws IOException {
        ConfigMethods.saveLocation("spawn", location);
    }



    public static ArrayList<Arena> Arenas = new ArrayList<>();
    public static ArrayList<Player> playersPlaying = new ArrayList<>();
    public TempTeam blueTempTeam;
    public TempTeam redTempTeam;
    private boolean inGame;
    private Arena instance;
    private HashMap<Integer, Stage> stages;
    private Team TeamBlue = null;
    private Team TeamRed = null;
    private String ID;
    private int roundNumber;
    
    public static Arena getArenaFromID(final String id) {
        for (final Arena arena : Arena.Arenas) {
            if (arena.getID().equalsIgnoreCase(id)) {
                return arena;
            }
        }
        return null;
    }
    
    public Arena(final Location location, String ID) throws IOException {
        this.inGame = false;
        this.stages = new HashMap<>();
        this.roundNumber = 0;
        Arena.Arenas.add(this);
        this.ID = ID;
        setCenter(location);
        this.blueTempTeam = new TempTeam();
        this.redTempTeam = new TempTeam();
        this.setUpArena();
    }
    
    public void startGameWithFeedBack(final Player player) {
        if (blueTempTeam.readyToPlay() && this.redTempTeam.readyToPlay()) {
            startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED));
            player.sendMessage(ChatColor.BOLD + "Rozpoczela sie gra! (Arena " + this.ID + ")");
            System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
            return;
        }
        player.sendMessage(ChatColor.BOLD + "Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
        System.out.println("Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
    }
    
    public void startGame() {
        if (blueTempTeam.readyToPlay() && redTempTeam.readyToPlay()) {
            startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED));
            System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
            return;
        }
        System.out.println("Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
    }

    public void forceStart(final Player player) {
        startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED));
        player.sendMessage(ChatColor.BOLD + "Rozpoczela sie gra! (Arena " + this.ID + ")");
        System.out.println("Rozpoczela sie gra! (Arena " + this.ID + " (FORCE) )");
    }
    
    public void startGame(final Team team1, final Team team2) {
        this.setUpStart(team1, team2);
    }
    
    public TempTeam getTempTeamByTag(final TeamTag teamTag) {
        return (teamTag == TeamTag.BLUE) ? this.blueTempTeam : this.redTempTeam;
    }
    
    public void startGame(TempTeam blueTempTeam, TempTeam redTempTeam) {
        if (blueTempTeam.readyToPlay() && redTempTeam.readyToPlay()) {
            this.startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED));
            System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
            return;
        }
        System.out.println("Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
    }
    
    private void setUpStart(final Team team1, final Team team2) {
        this.TeamBlue = team1;
        this.TeamRed = team2;
        this.inGame = true;
        System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
        for (PBTeamPlayer e : this.getAllPBPlayers()) {
            Arena.playersPlaying.add(e.getPlayer());
        }
        this.nextRound();
    }

    public void stopGame() {
        if (this.inGame) {
            for (PBTeamPlayer player : this.getAllPBPlayers()) {
                Arena.playersPlaying.remove(player.getPlayer());
                System.out.println("Usunieto z gry playera " + player.getPlayer().getName());
                this.getPBPlayer(player.getPlayer()).removePlayer();
                player.getPlayer().sendTitle(ChatColor.DARK_RED + "Koniec gry!", "", 10, 20, 10);
            }
            this.inGame = false;
            this.blueTempTeam.removeAllPlayers();
            this.redTempTeam.removeAllPlayers();
            this.TeamRed = null;
            this.TeamBlue = null;
        }
    }

    private void nextRound() {
        roundNumber++;
        for (final PBTeamPlayer player : this.getAllPBPlayers()) {
            if (player == null) {
                continue;
            }
            if (!player.isKilled()) {
                final int stage = (player.getTeam().getTeamTag() == TeamTag.BLUE) ? 4 : 6;
                player.setStage(stage);
                if (stages.get(stage).getTeleportByNumber(player.getTeam().getPBPlayerNumber(player)) == null) {
                    System.out.println("Arena " + this.getID() + " nie ma teleportu " + StageEnum.getFromID(stage).toString());
                    stopGame();
                    return;
                }
                player.getPlayer().teleport(this.stages.get(stage).getTeleportByNumber(player.getTeam().getPBPlayerNumber(player)));
                ArenaListener.freezePlayers.add(player.getPlayer());
                player.getPlayer().sendTitle(ChatColor.BOLD + "Gra zaczyna sie za 12sekund!", "", 5, 40, 5);
            }
        }
        this.instance = this;
        final int[] i = { 10 };
        new BukkitRunnable() {
            public void run() {
                for (PBTeamPlayer pbTeamPlayer : Arena.this.instance.getAllPBPlayers()) {
                    Player player = pbTeamPlayer.getPlayer();
                    if (!pbTeamPlayer.isInGame() || pbTeamPlayer == null || pbTeamPlayer.getPlayer() == null) {
                        continue;
                    }
                    if (i[0] != 0) {
                        player.sendTitle(ChatColor.BOLD + "" + NumberChatColor.getFromValue(i[0]).getChatColor() + Integer.toString(i[0]), "", 0, 20, 0);
                    } else {
                        player.sendTitle(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "START!", "", 0, 15, 5);
                        ArenaListener.freezePlayers.remove(player);
                        instance.runChecker();
                        this.cancel();
                    }
                }
                i[0]--;
            }
        }.runTaskTimer(ProBending.plugin, 40L, 20L);
    }

    public int getRoundNumber() {
        return this.roundNumber;
    }

    public void killPlayer(final PBTeamPlayer player) {
        if (!player.isInGame()) {
            return;
        }
        player.setKilled(true);
        player.setStage(11);
        player.getPlayer().teleport(Arena.spawn());
    }

    public void removePlayer(final PBTeamPlayer player) {
        if (!player.isInGame()) {
            return;
        }
        player.removePlayer();
    }

    public PBTeamPlayer getPBPlayer(final Player player) {
        for (final PBTeamPlayer i : this.getAllPBPlayers()) {
            if (i.getPlayer().equals(player)) {
                return i;
            }
        }
        return null;
    }

    public String getID() {
        return this.ID;
    }

    public void claimStage(final TeamTag teamTag) {
        final Team enemyTeam = this.getTeamByTag(teamTag);
        final ChatColor enemyTeamColor = (enemyTeam.getTeamTag() == TeamTag.BLUE) ? ChatColor.BLUE : ChatColor.RED;
        final String enemyTeamName = (enemyTeam.getTeamTag() == TeamTag.RED) ? "czerwona" : "niebieska";
        for (final PBTeamPlayer i : enemyTeam.getPBPlayers()) {
            if (i.isInGame()) {
                i.raiseStage();
            }
        }
        for (Player p : getAllPlayers()) {
            p.sendMessage(ChatColor.BOLD + "" + enemyTeamColor + "Druzyna " + enemyTeamName + " zdobywa strefe!");
        }
        enemyTeam.claimingStage = true;
        new BukkitRunnable() {
            public void run() {
                for (final PBTeamPlayer enemyTeamPlayer : enemyTeam.getPBPlayers()) {
                    if (!enemyTeamPlayer.isInGame()) {
                        continue;
                    }
                    final int e = (enemyTeam.getTeamTag() == TeamTag.RED) ? -1 : 1;
                    if (enemyTeamPlayer.getCurrentStage().getID() != enemyTeamPlayer.getStage() + e) {
                        continue;
                    }
                    enemyTeamPlayer.getPlayer().teleport(stages.get(enemyTeamPlayer.getStage()).getTeleportByNumber(enemyTeam.getPBPlayerNumber(enemyTeamPlayer)));
                }
                TeamRed.claimingStage = false;
            }
        }.runTaskLater(ProBending.plugin, 60L);
    }

    public void runChecker() {
        new BukkitRunnable() {
            int round = roundNumber;
            public void run() {
                if (getAllPlayers() == null) {
                    instance.stopGame();
                    this.cancel();
                    return;
                }
                if (getAllPlayers().isEmpty()) {
                    instance.stopGame();
                    this.cancel();
                    return;
                }
                if (!inGame) {
                    this.cancel();
                    return;
                }
                if (round != getRoundNumber()) {
                    this.cancel();
                    return;
                }
                for (final PBTeamPlayer player : getAllPBPlayers()) {
                    if (player.isInGame() && player.getPlayer() != null) {
                        if (player.getTeam().claimingStage) {
                            continue;
                        }
                        final TeamTag playerTag = player.getTeam().getTeamTag();
                        final Team team = getTeamByTag(playerTag);
                        final ChatColor teamColor = (playerTag == TeamTag.BLUE) ? ChatColor.BLUE : ChatColor.RED;

                        if (player.getCurrentStage() == StageEnum.WholeArena) {
                            // TODO: SYSTEM KARANIA
                            player.getPlayer().teleport(stages.get(player.getStage()).getCenter());
                            return;


                        } else if (player.belowStage(player.getStage(), false)) {
                            player.lowerStage();
                            player.getPlayer().teleport(stages.get(player.getStage()).getCenter());
                            Iterator<PBTeamPlayer> iterator = team.getPBPlayers().iterator();
                            while (iterator.hasNext()) {
                                if (iterator.next().aboveStage(player.getStage(), false)) {
                                    break;
                                }
                                if (!iterator.hasNext()) {
                                    claimStage((team.getTeamTag() == TeamTag.BLUE) ? TeamTag.RED : TeamTag.BLUE);
                                    return;
                                }
                            }
                            if (player.getStage() == 8 || player.getStage() == 1) {
                                killPlayer(player);
                                for (Player p : getAllPlayers()) {
                                    p.sendMessage(ChatColor.BOLD + "" + teamColor + "Player " + player.getPlayer().getDisplayName() + " dostal K-O!");
                                }
                            }

                        } else if (player.aboveStage(player.getStage(), false)) {
                            player.getPlayer().teleport(stages.get(player.getStage()).getCenter());
                            player.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Jeszcze nie przejales strefy " + player.getCurrentStage() + "!");
                        }
                    }
                }
            }
        }.runTaskTimer(ProBending.plugin, 0L, 5L);
    }

    public Location getCenter() {
       return ConfigMethods.getLocation("Arena.nr" + this.ID + ".center");
    }

    public void setCenter(Location location) throws IOException {
        ConfigMethods.saveLocation("Arena.nr" + this.ID + ".center", location);
    }

    public Team getTeamBlue() {
        return this.TeamBlue;
    }

    public Team getTeamRed() {
        return this.TeamRed;
    }

    public ArrayList<Player> getAllPlayers() {
        final ArrayList<Player> i = new ArrayList<>();
        if (TeamRed != null) {
            if (!TeamRed.getPlayers().isEmpty()) {
                i.addAll(this.TeamRed.getPlayers());
            }
        }
        if (TeamBlue != null) {
            if (!TeamBlue.getPlayers().isEmpty()) {
                i.addAll(this.TeamBlue.getPlayers());
            }
        }
        while (i.remove(null)) {
        }
        return i;
    }

    public Team getTeamByTag(final TeamTag teamTag) {
        return (teamTag == TeamTag.BLUE) ? this.TeamBlue : this.TeamRed;
    }

    public ArrayList<PBTeamPlayer> getAllPBPlayers() {
        final ArrayList<PBTeamPlayer> i = new ArrayList<>();
        if (TeamRed != null) {
            if (!TeamRed.getPBPlayers().isEmpty()) {
                i.addAll(this.TeamRed.getPBPlayers());
            }
        }
        if (TeamBlue != null) {
            if (!TeamBlue.getPBPlayers().isEmpty()) {
                i.addAll(this.TeamBlue.getPBPlayers());
            }
        }
        while (i.remove(null)) {
        }
        return i;
    }
    
    public void setUpArena() {
        for (final StageEnum e : StageEnum.values()) {
            this.stages.put(e.getID(), new Stage(e, this));
            System.out.println("Tworzenie areny " + stages.get(e.getID()).getStage().toString() + "...");
        }
        System.out.println("Zakonono tworzenie areny "+ getID() + "!");
    }
    
    public Stage getStage(final int ID) {
        return this.stages.get(ID);
    }
    
    public boolean isInGame() {
        return this.inGame;
    }
}
