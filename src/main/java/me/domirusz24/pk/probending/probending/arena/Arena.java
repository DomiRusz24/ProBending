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
    private Team TeamBlue;
    private Team TeamRed;
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
        this.nextRound();
    }
    
    public void stopGame() {
        if (this.inGame) {
            this.inGame = false;
            for (final Player player : this.getAllPlayers()) {
                Arena.playersPlaying.remove(player);
                this.getPBPlayer(player).removePlayer();
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(Arena.spawn());
                player.sendTitle(ChatColor.DARK_RED + "Koniec gry!", "", 10, 20, 10);
            }
        }
    }
    
    private void nextRound() {
        roundNumber++;
        for (final PBTeamPlayer player : this.getAllPBPlayers()) {
            if (!player.isKilled()) {
                final int stage = (player.getTeam().getTeamTag() == TeamTag.BLUE) ? 5 : 4;
                player.setStage(stage);
                player.getPlayer().teleport(this.stages.get(stage).getTeleportByNumber(player.getTeam().getPBPlayerNumber(player)));
                ArenaListener.freezePlayers.add(player.getPlayer());
                Arena.playersPlaying.add(player.getPlayer());
                player.getPlayer().sendTitle(ChatColor.BOLD + "Gra zaczyna sie za 10sekund!", "", 5, 100, 5);
            }
        }
        this.instance = this;
        final int[] i = { 5 };
        new BukkitRunnable() {
            public void run() {
                for (PBTeamPlayer pbTeamPlayer : Arena.this.instance.getAllPBPlayers()) {
                    Player player = pbTeamPlayer.getPlayer();
                    if (!pbTeamPlayer.isInGame()) {
                        break;
                    }
                    if (i[0] != 0) {
                        player.sendTitle(ChatColor.BOLD + Integer.toString(i[0]), "", 0, 20, 0);
                    }
                    else {
                        player.sendTitle(ChatColor.BOLD + "START!", "", 0, 15, 5);
                        ArenaListener.freezePlayers.remove(player);
                        Arena.this.instance.runChecker();
                        this.cancel();
                    }
                }
                i[0]--;
            }
        }.runTaskTimer(ProBending.plugin, 100L, 20L);
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
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
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
        for (Player p : this.instance.getAllPlayers()) {
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
                    enemyTeamPlayer.getPlayer().teleport(Arena.this.instance.stages.get(enemyTeamPlayer.getStage()).getTeleportByNumber(enemyTeam.getPBPlayerNumber(enemyTeamPlayer)));
                }
                Arena.this.TeamRed.claimingStage = false;
            }
        }.runTaskLater(ProBending.plugin, 60L);
    }
    
    public void runChecker() {
        final ArrayList<PBTeamPlayer> teamPlayers = new ArrayList<>(this.getAllPBPlayers());
        new BukkitRunnable() {
            int round = roundNumber;
            public void run() {
                if (!Arena.this.instance.inGame || round != roundNumber) {
                    this.cancel();
                }
                for (final PBTeamPlayer player : teamPlayers) {
                    if (player.isInGame()) {
                        if (player.getTeam().claimingStage) {
                            continue;
                        }
                        StageEnum stage = player.getCurrentStage();


                        if (!player.isInTieBreaker() && (stage.getID() == 12 || stage.getID() == 13)) {
                            stage = StageEnum.getFromID((stage.getID() == 12) ? 4 : 5);
                        }


                        final StageEnum currentStage = StageEnum.getFromID(player.getStage());
                        final TeamTag playerTag = player.getTeam().getTeamTag();
                        final Team team = Arena.this.instance.getTeamByTag(playerTag);
                        final ChatColor teamColor = (playerTag == TeamTag.BLUE) ? ChatColor.BLUE : ChatColor.RED;

                        if (stage.equals(StageEnum.Line)) {
                            stage = currentStage;
                        }

                        if (stage == StageEnum.WholeArena) {
                            // TODO: SYSTEM KARANIA
                            player.getPlayer().teleport(Arena.this.instance.stages.get(currentStage.getID()).getTeleportByNumber(team.getPBPlayerNumber(player)));
                        }
                        else if (stage.getID() < currentStage.getID()) {
                            player.lowerStage();
                            if (team.getPlayer1().getStage() <= player.getStage() && team.getPlayer2().getStage() <= player.getStage() && team.getPlayer3().getStage() <= player.getStage()) {
                                Arena.this.instance.claimStage((team.getTeamTag() == TeamTag.BLUE) ? TeamTag.RED : TeamTag.BLUE);
                            } else {
                                if (player.isInGame()) {
                                    continue;
                                }
                                Arena.this.instance.killPlayer(player);
                                for (final Player p : Arena.this.instance.getAllPlayers()) {
                                    p.sendMessage(ChatColor.BOLD + "" + teamColor + "Player " + player.getPlayer().getDisplayName() + " dostal K-O!");
                                }
                            }
                        } else {
                            if (stage.getID() <= currentStage.getID()) {
                                continue;
                            }
                            player.getPlayer().setVelocity(Arena.this.stages.get(player.getStage()).getCenter().subtract(player.getPlayer().getLocation()).toVector().normalize().multiply(3));
                            player.getPlayer().sendMessage(ChatColor.BOLD + "Jeszcze nie przejales tej strefy!");
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
        i.addAll(this.TeamRed.getPlayers());
        i.addAll(this.TeamBlue.getPlayers());
        i.removeIf(Objects::isNull);
        return i;
    }
    
    public Team getTeamByTag(final TeamTag teamTag) {
        return (teamTag == TeamTag.BLUE) ? this.TeamBlue : this.TeamRed;
    }
    
    public ArrayList<PBTeamPlayer> getAllPBPlayers() {
        final ArrayList<PBTeamPlayer> i = new ArrayList<>();
        i.addAll(this.TeamBlue.getPBPlayers());
        i.addAll(this.TeamRed.getPBPlayers());
        i.removeIf(Objects::isNull);
        return i;
    }
    
    public void setUpArena() {
        int i = 0;
        for (final StageEnum e : StageEnum.values()) {
            ++i;
            this.stages.put(i, new Stage(e, this));
        }
    }
    
    public Stage getStage(final int ID) {
        return this.stages.get(ID);
    }
    
    public boolean isInGame() {
        return this.inGame;
    }
}
