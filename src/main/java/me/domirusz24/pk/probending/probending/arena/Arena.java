package me.domirusz24.pk.probending.probending.arena;

import org.bukkit.entity.*;
import me.domirusz24.pk.probending.probending.arena.temp.*;
import org.bukkit.*;
import org.bukkit.scheduler.*;
import me.domirusz24.pk.probending.probending.*;
import java.io.IOException;
import java.util.*;
import org.bukkit.util.Vector;

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
    private static HashMap<Player, Arena> playersSpectating = new HashMap<>();
    private ArrayList<Player> spectators = new ArrayList<>();
    public TempTeam blueTempTeam;
    public TempTeam redTempTeam;
    private boolean inGame;
    private Arena instance;
    private HashMap<Integer, Stage> stages;
    private int roundTimeCounter = 0;
    private Team TeamBlue = null;
    private Team TeamRed = null;
    private String ID;
    private final int tickUpdate;
    private final int winningRound;
    private final int tieBreakerRoundTime;
    private final int roundTime;
    private int roundNumber;
    private boolean inTieBreaker;
    private PBTeamPlayer tieBreakerPlayerBlue;
    private PBTeamPlayer tieBreakerPlayerRed;

    public static HashMap<Player, Arena> getPlayersSpectating() {
        return playersSpectating;
    }

    public static Arena getArenaFromID(final String id) {
        for (final Arena arena : Arena.Arenas) {
            if (arena.getID().equalsIgnoreCase(id)) {
                return arena;
            }
        }
        return null;
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }

    public Arena(final Location location, String ID) throws IOException {
        tickUpdate = ProBending.plugin.getConfig().getInt("arena.tickUpdate");
        winningRound = ProBending.plugin.getConfig().getInt("arena.tickUpdate");
        tieBreakerRoundTime = ProBending.plugin.getConfig().getInt("arena.tieBreakerRound");
        roundTime = ProBending.plugin.getConfig().getInt("arena.roundTime");
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
            startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE, this), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED, this));
            player.sendMessage(ChatColor.BOLD + "Rozpoczela sie gra! (Arena " + this.ID + ")");
            System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
            return;
        }
        player.sendMessage(ChatColor.BOLD + "Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
        System.out.println("Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
    }

    public boolean isInTieBreaker() {
        return inTieBreaker;
    }

    public void startGame() {
        if (blueTempTeam.readyToPlay() && redTempTeam.readyToPlay()) {
            startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE, this), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED, this));
            System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
            return;
        }
        System.out.println("Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
    }

    public void forceStart(final Player player) {
        startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE, this), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED, this));
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
            this.startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE, this), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED, this));
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
            if (getAllPBPlayers() != null && !getAllPBPlayers().isEmpty()) {
                broadcastTitle(ChatColor.DARK_RED + "Koniec gry!", "", 10, 20, 10);
                broadcastTitleSpectatorsOnly(ChatColor.DARK_RED + "Koniec gry!", "", 10, 20, 10);
                for (PBTeamPlayer player : this.getAllPBPlayers()) {
                    if (player == null) {
                        continue;
                    }
                    if (player.getPlayer() == null) {
                        continue;
                    }
                    Arena.playersPlaying.remove(player.getPlayer());
                    ArenaListener.freezePlayers.remove(player.getPlayer());
                    System.out.println("Usunieto z gry playera " + player.getPlayer().getName());
                    this.removePlayer(this.getPBPlayer(player.getPlayer()));
                }
            }
            this.inGame = false;
            this.blueTempTeam.removeAllPlayers();
            this.redTempTeam.removeAllPlayers();
            this.TeamRed = null;
            this.TeamBlue = null;
        }
    }

    public void stopGame(TeamTag teamTag) {
        if (this.inGame) {
            if (getAllPBPlayers() != null && !getAllPBPlayers().isEmpty()) {
                if (teamTag != null) {
                    broadcastTitle(ChatColor.DARK_GREEN + "Koniec gry!", getTeamByTag(teamTag).getColor() + "Wygrala druzyna " + getTeamByTag(teamTag).getPolishName() + "!", 10, 20, 10);
                    broadcastTitleSpectatorsOnly(ChatColor.DARK_GREEN + "Koniec gry!", getTeamByTag(teamTag).getColor() + "Wygrala druzyna " + getTeamByTag(teamTag).getPolishName() + "!", 10, 20, 10);
                } else {
                    broadcastTitle(ChatColor.DARK_GREEN + "Koniec gry!",  ChatColor.WHITE + "Gra zostala zakonczona remisem!", 10, 20, 10);
                    broadcastTitleSpectatorsOnly(ChatColor.DARK_GREEN + "Koniec gry!",  ChatColor.WHITE + "Gra zostala zakonczona remisem!", 10, 20, 10);
                }
                for (PBTeamPlayer player : this.getAllPBPlayers()) {
                    if (player == null) {
                        continue;
                    }
                    if (player.getPlayer() == null) {
                        continue;
                    }
                    Arena.playersPlaying.remove(player.getPlayer());
                    ArenaListener.freezePlayers.remove(player.getPlayer());
                    System.out.println("Usunieto z gry playera " + player.getPlayer().getName());
                    this.removePlayer(this.getPBPlayer(player.getPlayer()));
                }
            }
            this.inGame = false;
            this.blueTempTeam.removeAllPlayers();
            this.redTempTeam.removeAllPlayers();
            this.TeamRed = null;
            this.TeamBlue = null;
        }
    }

    private void nextRound() {
        if (!this.inGame) {
            this.stopGame();
            return;
        }
        this.roundTimeCounter = 0;
        roundNumber++;
        broadcastTitle(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Gra rozpoczyna sie za 12 sekund!", "", 5, 40, 5);
        broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "Niedlugo rozpocznie sie gra!", "", 5, 40, 5);
        for (final PBTeamPlayer player : this.getAllPBPlayers()) {
            if (player == null) {
                continue;
            }
            if (player.getPlayer() == null) {
                continue;
            }
            if (!player.getPlayer().isOnline()) {
                continue;
            }
            if (!player.isKilled()) {
                player.setStage(StageEnum.convertID(4, player.getTeam().getTeamTag()));
                player.getPlayer().teleport(this.stages.get(player.getStageAbsolute()).getTeleportByNumber(player.getTeam().getPBPlayerNumber(player)));
                ArenaListener.freezePlayers.add(player.getPlayer());
            }
        }
        this.instance = this;
        final int[] i = { 10 };
        new BukkitRunnable() {
            public void run() {
                if (!inGame) {
                    cancel();
                    return;
                }
                if (i[0] != 0) {
                    broadcastTitle(ChatColor.BOLD + "" + NumberChatColor.getFromValue(i[0]).getChatColor() + i[0], "", 0, 20, 0);
                    broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + NumberChatColor.getFromValue(i[0]).getChatColor() + i[0], "", 0, 20, 0);
                } else {
                    broadcastTitle(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "START!", "", 0, 15, 5);
                    broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "Gra zostala rozpoczeta!", "", 0, 20, 5);
                }
                for (PBTeamPlayer pbTeamPlayer : Arena.this.instance.getAllPBPlayers()) {
                    if (pbTeamPlayer.getPlayer() == null) {
                        continue;
                    }
                    if (!pbTeamPlayer.getPlayer().isOnline()) {
                        continue;
                    }
                    Player player = pbTeamPlayer.getPlayer();
                    if (pbTeamPlayer.isKilled() || pbTeamPlayer == null || pbTeamPlayer.getPlayer() == null) {
                        continue;
                    }
                    if (i[0] == 0) {
                        ArenaListener.freezePlayers.remove(player);
                        instance.runChecker();
                        this.cancel();
                    }
                }
                i[0]--;
            }
        }.runTaskTimer(ProBending.plugin, 40L, 20L);
    }

    public void nextMidRound() {
        if (!this.inGame) {
            return;
        }
        this.roundTimeCounter = 0;
        broadcastTitle(ChatColor.BOLD + "" + ChatColor.RED + "Koniec rundy!", "", 0, 40, 5);
        broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.RED + "Runda " + getRoundNumber() + " zostala zakonczona!", "", 0, 40, 5);
        for (PBTeamPlayer player : this.getAllPBPlayers()) {
            if (player.getPlayer() == null) {
                continue;
            }
            if (!player.getPlayer().isOnline()) {
                continue;
            }
            if (!player.isKilled()) {
                ArenaListener.freezePlayers.add(player.getPlayer());
                player.getPlayer().setHealth(20);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!inGame) {
                        return;
                    }
                    if (getTeamRed().getPoints() == winningRound) {
                        stopGame(TeamTag.RED);
                    } else if (getTeamBlue().getPoints() == winningRound) {
                        stopGame(TeamTag.BLUE);
                    } else {


                        TeamTag winningTeam = whoIsWinning();
                        if (winningTeam == null) {


                            broadcastTitle(ChatColor.BOLD + "" + ChatColor.WHITE + "Runda zakonczyla sie remisem!", ChatColor.BOLD + "" + ChatColor.RED + "Za 5 sekund zacznie sie TieBreaker!", 5, 50, 5);
                            broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.WHITE + "Runda zakonczyla sie remisem!", ChatColor.BOLD + "" + ChatColor.RED + "Niedlugo zacznie sie TieBreaker!", 5, 50, 5);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!inGame) {
                                        return;
                                    }
                                    startTieBreaker();
                                }
                            }.runTaskLater(ProBending.plugin, 100);


                        } else {
                            Team team = getTeamByTag(winningTeam);
                            team.raisePoint();
                            broadcastTitle(ChatColor.BOLD + "" + team.getColor() + "Runde wygrala druzyna " + team.getPolishName() + "!", ChatColor.BOLD + "" + ChatColor.RED + "Za 5 sekund zacznie sie nastepna rudna!", 5, 50, 5);
                            broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + team.getColor() + "Runde wygrala druzyna " + team.getPolishName() + "!", ChatColor.BOLD + "" + ChatColor.RED + "Zaraz zacznie sie nastepna rudna!", 5, 50, 5);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!inGame) {
                                        return;
                                    }
                                    nextRound();
                                }
                            }.runTaskLater(ProBending.plugin, 100);
                        }
                    }
                }
            }.runTaskLater(ProBending.plugin, 45);
        }


    }

    public void startTieBreaker() {
        if (!inGame) {
            return;
        }
        roundNumber++;
        int redPlayerNumber;
        int bluePlayerNumber;
        do {
            redPlayerNumber = (int) (Math.random() * ((getTeamRed().getPBPlayers().size() - 1) + 1)) + 1;
            tieBreakerPlayerRed = TeamRed.getPBPlayer(redPlayerNumber);
        } while (tieBreakerPlayerRed == null || tieBreakerPlayerRed.getPlayer() == null);
        do {
            bluePlayerNumber = (int) (Math.random() * ((getTeamBlue().getPBPlayers().size() - 1) + 1)) + 1;
            tieBreakerPlayerBlue = TeamBlue.getPBPlayer(bluePlayerNumber);
        } while (tieBreakerPlayerBlue == null || tieBreakerPlayerBlue.getPlayer() == null);


        // TODO: Tie Breaker


    }

    public void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : getAllPlayers()) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public void broadcastTitleSpectatorsOnly(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : spectators) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public TeamTag whoIsWinning() {
        if (!inGame) {
            return null;
        }
        int redAlivePlayerCount = 0;
        for (PBTeamPlayer player : this.getTeamRed().getPBPlayers()) {
            if (!player.isKilled()) {
                redAlivePlayerCount++;
            }
        }
        int blueAlivePlayerCount = 0;
        for (PBTeamPlayer player : this.getTeamBlue().getPBPlayers()) {
            if (!player.isKilled()) {
                blueAlivePlayerCount++;
            }
        }
        if (redAlivePlayerCount > blueAlivePlayerCount) {
            return TeamTag.RED;
        } else if (redAlivePlayerCount < blueAlivePlayerCount) {
            return TeamTag.BLUE;
        } else {
            int redPlayerPoints = 0;
            for (PBTeamPlayer player : this.getTeamRed().getPBPlayers()) {
                redPlayerPoints = redPlayerPoints + player.getStage();
            }
            int bluePlayerPoints = 0;
            for (PBTeamPlayer player : this.getTeamBlue().getPBPlayers()) {
                bluePlayerPoints = bluePlayerPoints + player.getStage();
            }
            if (redPlayerPoints > bluePlayerPoints) {
                return TeamTag.RED;
            } else if (redPlayerPoints < bluePlayerPoints) {
                return TeamTag.BLUE;
            } else {
                return null;
            }
        }
    }

    public int getRoundNumber() {
        return this.roundNumber;
    }

    public void killPlayer(final PBTeamPlayer player) {
        if (player.isKilled()) {
            return;
        }
        ArenaListener.freezePlayers.remove(player.getPlayer());
        player.setInTieBreaker(false);
        player.setKilled(true);
        player.setStage(11);
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        final ChatColor teamColor = player.getTeam().getColor();
        for (Player p : getAllPlayers()) {
            p.sendMessage(ChatColor.BOLD + "" + teamColor + "Player " + player.getPlayer().getDisplayName() + " dostal K-O!");
        }
        broadcastTitleSpectatorsOnly("", teamColor + "Gracz " + player.getPlayer().getName() + " dostal K-O!", 5, 40, 5);
        player.debug();
        if (player.getTeam().checkWipeOut()) {
            stopGame(player.getTeam().getEnemyTeamTag());
        }
    }

    public void removePlayer(final PBTeamPlayer player) {
        if (!getAllPBPlayers().contains(player)) {
            return;
        }
        ArenaListener.freezePlayers.remove(player.getPlayer());
        if (player.getPlayer() == null) {
            return;
        }
        player.getPlayer().setGameMode(GameMode.SURVIVAL);
        player.getPlayer().teleport(Arena.spawn());
        player.setInTieBreaker(false);
        player.setInGame(false);
        Arena.playersPlaying.remove(player.getPlayer());
        player.setPlayerToNull();
        boolean stop = true;
        for (PBTeamPlayer teamPlayer : getAllPBPlayers(true)) {
            if (teamPlayer != null) {
                if (teamPlayer.getPlayer() != null) {
                    stop = false;
                } else if (!teamPlayer.isKilled()) {
                    stop = false;
                }
            }
        }
        if (stop) {
            stopGame(player.getTeam().getEnemyTeamTag());
        }
    }

    public void removeSpectator(Player player) {
        if (this.spectators.contains(player)) {
            player.setGameMode(GameMode.SURVIVAL);
            ArenaListener.freezePlayers.remove(player);
            player.teleport(Arena.spawn());
            this.spectators.remove(player);
            Arena.playersSpectating.remove(player);
        }
    }

    public void addSpectator(Player player, boolean force) {
        if (Arena.playersPlaying.contains(player)) {
            if (this.getAllPlayers().contains(player)) {
                if (force) {
                    this.removePlayer(this.getPBPlayer(player));
                } else {
                    return;
                }
            }
        }
        if (Arena.getPlayersSpectating().containsKey(player)) {
            Arena.getPlayersSpectating().get(player).removeSpectator(player);
        }
        Location t = getCenter().clone();
        t.setY(t.getY() + 6);
        player.teleport(t);
        player.setGameMode(GameMode.SPECTATOR);
        ArenaListener.freezePlayers.remove(player);
        this.spectators.add(player);
        Arena.playersSpectating.put(player, this);
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
        final ChatColor enemyTeamColor = enemyTeam.getColor();
        final String enemyTeamName = enemyTeam.getPolishName();
        for (final PBTeamPlayer i : enemyTeam.getPBPlayers()) {
            if (!i.isKilled()) {
                i.debug("BEFORE RAISE");
                i.raiseStage();
                i.debug("AFTER RAISE");
            }
        }
        for (Player p : getAllPlayers()) {
            p.sendMessage(ChatColor.BOLD + "" + enemyTeamColor + "Druzyna " + enemyTeamName + " zdobywa strefe!");
        }
        broadcastTitleSpectatorsOnly("", enemyTeamColor + "Druzyna " + enemyTeamName + " zdobyla strefe!", 5, 40, 5);

    }

    public HashMap<Integer, Stage> getStages() {
        return stages;
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
                roundTimeCounter = roundTimeCounter + tickUpdate;
                if (roundTimeCounter > roundTime) {
                    nextMidRound();
                    this.cancel();
                    return;
                }
                for (PBTeamPlayer player : getAllPBPlayers()) {
                    if (player.getPlayer() != null) {
                        if (player.isKilled()) {
                            continue;
                        }
                        if (isInTieBreaker() && !player.isInTieBreaker()) {
                            continue;
                        }
                        final TeamTag playerTag = player.getTeam().getTeamTag();
                        final Team team = getTeamByTag(playerTag);
                        final ChatColor teamColor = team.getColor();

                        if (player.getCurrentStage() == StageEnum.WholeArena) {
                            Location loc = stages.get(player.getStageAbsolute()).getCenter();
                            loc.setPitch(player.getPlayer().getLocation().getPitch());
                            loc.setYaw(player.getPlayer().getLocation().getYaw());
                            player.getPlayer().teleport(loc);
                            return;


                        } else if (player.belowStage(player.getStage(), false)) {
                            player.debug("BEFORE LOWER");
                            if (player.isInTieBreaker() && instance.isInTieBreaker()) {
                            stopGame(team.getEnemyTeamTag());
                            } else {
                                player.lowerStage();
                                player.debug("AFTER LOWER");
                                if (player.getStage() == 10 || player.getStage() == 1) {
                                    player.debug("BEFORE KILL");
                                    killPlayer(player);
                                    player.debug("AFTER KILL");
                                    continue;
                                }
                                broadcastTitleSpectatorsOnly("", player.getTeam().getColor() + "Gracz " + player.getPlayer().getName() + " zostal cofniety o stefe!", 5, 40, 5);
                                Vector vector = (stages.get(player.getStageAbsolute()).getCenter().subtract(player.getPlayer().getLocation()).toVector().normalize());
                                player.getPlayer().setVelocity(vector);
                                Iterator<PBTeamPlayer> i = team.getPBPlayers().iterator();
                                while (i.hasNext()) {
                                    if (i.next().aboveStage(player.getStage(), false)) {
                                        break;
                                    }
                                    if (!i.hasNext()) {
                                        claimStage(team.getEnemyTeamTag());
                                        break;
                                    }
                                }
                            }
                        } else if (player.aboveStage(player.getStage(), false)) {
                            player.debug("BEFORE WARNING");
                            player.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Jeszcze nie przejales strefy " + player.getCurrentStageAbsolute().polishName() + "!");
                            Vector vector = (stages.get(player.getStageAbsolute()).getCenter().subtract(player.getPlayer().getLocation()).toVector().normalize());
                            player.getPlayer().setVelocity(vector);
                            player.debug("AFTER WARNING");
                        }
                    }
                }
            }
        }.runTaskTimer(ProBending.plugin, 0, tickUpdate);
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
        i.removeIf(e -> e.getPlayer() == null);
        return i;
    }

    public ArrayList<PBTeamPlayer> getAllPBPlayers(boolean nullValues) {
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
        if (!nullValues) {
            while (i.remove(null)) {
            }
            i.removeIf(e -> e.getPlayer() == null);
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
