package me.domirusz24.pk.probending.probending.arena;

import me.domirusz24.pk.probending.probending.ConfigMethods;
import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.commands.ArenaCreateCommand;
import me.domirusz24.pk.probending.probending.arena.temp.TempTeam;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Arena
{

    public static Location spawn() {
        return ConfigMethods.getLocation("spawn");
    }

    public static void setSpawn(Location location) throws IOException {
        ConfigMethods.saveLocation("spawn", location);
    }

    public static ArrayList<String> getArenaRules() {
        ArrayList<String> info = new ArrayList<>();
        info.add(ChatColor.BOLD + "" + ChatColor.GREEN + "ZASADY GRY:");
        info.add(ChatColor.RED + " - Arena ma 6 stref, jak sie cofniesz o strefe to tam zostajesz, i nie mozesz przejsc o strefe do przodu.");
        info.add(ChatColor.RED + " - Jezeli wypadniesz z areny (z tylu) lub kiedy zginiesz to zostajesz dyskwalifikowany");
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "JAK WYGRAC GRE:");
        info.add(ChatColor.DARK_AQUA + " - Zabij wszystkich przecinikow lub");
        info.add(ChatColor.DARK_AQUA + " - Wygraj " + winningRound + " rund lub");
        info.add(ChatColor.DARK_AQUA + " - W przypadku remisu, wygraj TieBreaker.");
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "JAK WYGRAC RUNDE:");
        info.add(ChatColor.YELLOW + " - Miej wiecej osob nie dyskwalifikowanych lub");
        info.add(ChatColor.YELLOW + " - jezeli jest po tyle samo osob, miej wiecej stref.");
        info.add(ChatColor.YELLOW + " - Runda trwa " + roundTime / (20 * 60)  + " minut.");
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "TIEBREAKER:");
        info.add(ChatColor.AQUA + " - TieBreaker jest to walka 1 na 1 ktora odbywa sie na srodkowej strefie (kolo).");
        info.add(ChatColor.AQUA + " - Zeby wygrac musisz zabic, lub pchnac przeciwnika poza kolo.");
        info.add(ChatColor.AQUA + " - TieBreaker trwa " + tieBreakerRoundTime / (20 * 60)  + " minut, jezeli nikt nie wygra w ciagu tego czasu, gra konczy sie remisem.");
        return info;
    }


    public static ArrayList<Arena> Arenas = new ArrayList<>();
    public static ArrayList<Player> playersPlaying = new ArrayList<>();
    private static HashMap<Player, Arena> playersSpectating = new HashMap<>();
    private ArrayList<Player> spectators = new ArrayList<>();
    private static final int tickUpdate =ProBending.plugin.getConfig().getInt("arena.tickUpdate");
    private static final int winningRound= ProBending.plugin.getConfig().getInt("arena.winningRound");
    private static final int tieBreakerRoundTime= ProBending.plugin.getConfig().getInt("arena.tieBreakerRound");
    private static final int roundTime= ProBending.plugin.getConfig().getInt("arena.roundTime");
    private static final int TBraisingStages = ProBending.plugin.getConfig().getInt("TB.raisingStages");

    public TempTeam blueTempTeam;
    public TempTeam redTempTeam;
    private boolean inGame;
    private Arena instance;
    private HashMap<Integer, Stage> stages;
    private int roundTimeCounter = 0;
    private Team TeamBlue = null;
    private Team TeamRed = null;
    private String ID;
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

    public Arena(final Location location, String ID, boolean firsttime) throws IOException {
        this.inGame = false;
        this.stages = new HashMap<>();
        this.roundNumber = 0;
        Arena.Arenas.add(this);
        this.ID = ID;
        if (firsttime) {
            setCenter(location);
        }
        this.blueTempTeam = new TempTeam();
        this.redTempTeam = new TempTeam();
        this.setUpArena();
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }
    
    public void startGameWithFeedBack(final Player player) {
        if (!checkForMissingStages()) {
            this.stopGame();
            return;
        }
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
        if (!checkForMissingStages()) {
            this.stopGame();
            return;
        }
        if (blueTempTeam.readyToPlay() && redTempTeam.readyToPlay()) {
            startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE, this), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED, this));
            System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
            return;
        }
        System.out.println("Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
    }

    public void forceStart(final Player player) {
        if (!checkForMissingStages()) {
            return;
        }
        startGame(new Team(blueTempTeam.getPlayer1(), blueTempTeam.getPlayer2(), blueTempTeam.getPlayer3(), TeamTag.BLUE, this), new Team(redTempTeam.getPlayer1(), redTempTeam.getPlayer2(), redTempTeam.getPlayer3(), TeamTag.RED, this));
        player.sendMessage(ChatColor.BOLD + "Rozpoczela sie gra! (Arena " + this.ID + ")");
        System.out.println("Rozpoczela sie gra! (Arena " + this.ID + " (FORCE) )");
    }
    
    public void startGame(final Team team1, final Team team2) {
        if (!checkForMissingStages()) {
            return;
        }
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
        for (Player p : getAllPlayers()) {
            if (p.isDead()) {
                System.out.println("Nie moze sie rozpoczac gra z powodu smierci gracza! (Arena " + this.ID + " (FORCE) )");
                return;
            }
        }
        this.deleteTBArena();
        this.inGame = true;
        this.TeamBlue = team1;
        this.TeamRed = team2;
        this.inTieBreaker = false;
        roundNumber = 1;
        System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
        ArrayList<String> rules = getArenaRules();
        for (PBTeamPlayer e : this.getAllPBPlayers()) {
            Arena.playersPlaying.add(e.getPlayer());
            rules.forEach(e.getPlayer()::sendMessage);
        }
        this.nextRound();
    }

    public void displayArenaInfo(Player player) {
        final ArrayList<String> info = new ArrayList<>();
        final ChatColor inGame = isInGame() ? ChatColor.RED : ChatColor.GREEN;
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
        info.add(ChatColor.BOLD + "" + inGame + "Arena " + getID());
        info.add(ChatColor.BOLD + "Srodek areny: " + getCenter().getBlock().getLocation().getX() + " " + getCenter().getBlock().getLocation().getY() + " " + getCenter().getBlock().getLocation().getZ());
        info.add(ChatColor.BOLD + "ID:" + getID());
        final String inGameInfo = isInGame() ? "TAK" : "NIE";
        info.add(ChatColor.BOLD + "" + inGame + "W grze: " + inGameInfo);
        if (!isInGame()) {
            info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "DRUZYNY CZEKAJACE NA START: ");
            info.add("");
            info.add(ChatColor.BOLD + "" + ChatColor.RED + "CZERWONA: ");
            if (getTempTeamByTag(TeamTag.RED).getAllPlayers(true) != null) {
                int i = 0;
                for (final Player player2 : getTempTeamByTag(TeamTag.RED).getAllPlayers(true)) {
                    ++i;
                    final String playerstatus = (player2 == null) ? "NIE DODANY" : player2.getName();
                    info.add("Gracz " + i + ": " + playerstatus);
                }
            }
            else {
                info.add("Gracz 1: NIE DODANY");
                info.add("Gracz 2: NIE DODANY");
                info.add("Gracz 3: NIE DODANY");
            }
            info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "NIEBIESKA: ");
            if (getTempTeamByTag(TeamTag.BLUE).getAllPlayers(true) != null) {
                int i = 0;
                for (final Player player2 : getTempTeamByTag(TeamTag.BLUE).getAllPlayers(true)) {
                    ++i;
                    final String playerstatus = (player2 == null) ? "NIE DODANY" : player2.getName();
                    info.add("Gracz " + i + ": " + playerstatus);
                }
            }
            else {
                info.add("Gracz 1: NIE DODANY");
                info.add("Gracz 2: NIE DODANY");
                info.add("Gracz 3: NIE DODANY");
            }
            info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
            info.forEach(player::sendMessage);
        } else {

            info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "RUNDA: " + getRoundNumber());
            info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "W TIEBREAKER: " + (isInTieBreaker() ? "TAK" : "NIE"));
            info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "DRUZYNY: ");
            info.add("");
            info.add(ChatColor.BOLD + "" + ChatColor.RED + "CZERWONA: ");
            int i = 0;
            for (final PBTeamPlayer teamPlayer : getTeamRed().getPBPlayers(true)) {
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
            info.add("Punkty: " + getTeamRed().getPoints());



            info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "NIEBIESKA: ");
            int y = 0;
            for (final PBTeamPlayer teamPlayer : getTeamBlue().getPBPlayers(true)) {
                ++y;
                final String playerstatus2 = (teamPlayer == null || teamPlayer.getPlayer() == null) ? "NIE DODANY" : teamPlayer.getPlayer().getName();
                if (teamPlayer == null) {
                    info.add("Gracz " + y + ": " + playerstatus2);
                } else {
                    String playerstatus4 = teamPlayer.isKilled() ? " (ODPADL)" : " (NIE ODPADL)";
                    info.add("Gracz " + y + ": " + playerstatus2 + playerstatus4);
                }
            }

            info.add("Punkty: " + getTeamBlue().getPoints());

            info.add("");

            info.add(ChatColor.BOLD + "SPEKTATORZY: ");
            for (Player p : getSpectators()) {
                info.add(p.getName());
            }
            info.forEach(player::sendMessage);
        }
    }

    public void stopGame() {
        if (this.inGame) {
            if (getAllPBPlayers() != null && !getAllPBPlayers().isEmpty()) {
                broadcastTitle(ChatColor.DARK_RED + "Koniec gry!", "", 10, 60, 10);
                broadcastTitleSpectatorsOnly(ChatColor.DARK_RED + "Koniec gry!", "", 10, 60, 10);
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
            this.deleteTBArena();
            this.inGame = false;
            this.tieBreakerPlayerBlue = null;
            this.tieBreakerPlayerRed = null;
            this.inTieBreaker = false;
            this.blueTempTeam.removeAllPlayers();
            this.redTempTeam.removeAllPlayers();
            this.TeamRed = null;
            this.TeamBlue = null;
            ArenaCreateCommand.getRollBack(null, getID());
        }
    }

    public void stopGame(TeamTag teamTag) {
        if (this.inGame) {
            if (getAllPBPlayers() != null && !getAllPBPlayers().isEmpty()) {
                if (teamTag != null) {
                    broadcastTitle(ChatColor.DARK_GREEN + "Koniec gry!", getTeamByTag(teamTag).getColor() + "Wygrala druzyna " + getTeamByTag(teamTag).getPolishName() + "!", 10, 60, 10);
                    broadcastTitleSpectatorsOnly(ChatColor.DARK_GREEN + "Koniec gry!", getTeamByTag(teamTag).getColor() + "Wygrala druzyna " + getTeamByTag(teamTag).getPolishName() + "!", 10, 60, 10);
                } else {
                    broadcastTitle(ChatColor.DARK_GREEN + "Koniec gry!",  ChatColor.WHITE + "Gra zostala zakonczona remisem!", 10, 60, 10);
                    broadcastTitleSpectatorsOnly(ChatColor.DARK_GREEN + "Koniec gry!",  ChatColor.WHITE + "Gra zostala zakonczona remisem!", 10, 60, 10);
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
            for (Player player : new ArrayList<>(spectators)) {
                removeSpectator(player);
            }
            this.deleteTBArena();
            this.inGame = false;
            this.tieBreakerPlayerBlue = null;
            this.tieBreakerPlayerRed = null;
            this.inTieBreaker = false;
            this.blueTempTeam.removeAllPlayers();
            this.redTempTeam.removeAllPlayers();
            this.TeamRed = null;
            this.TeamBlue = null;
            ArenaCreateCommand.getRollBack(null, getID());
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
                player.getBPlayer().blockChi();
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
                        while(ArenaListener.freezePlayers.remove(player)) {
                        }
                        pbTeamPlayer.getBPlayer().unblockChi();
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
                player.getBPlayer().blockChi();
                ArenaListener.freezePlayers.add(player.getPlayer());
                player.getPlayer().setHealth(20);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!inGame) {
                    return;
                }


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
                        if (getTeamRed().getPoints() == winningRound) {
                            stopGame(TeamTag.RED);
                        } else if (getTeamBlue().getPoints() == winningRound) {
                            stopGame(TeamTag.BLUE);
                        }
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
        }.runTaskLater(ProBending.plugin, 45);


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

        broadcastTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "Wybrano playerow: ", tieBreakerPlayerRed.getPlayer().getName() + " i " + tieBreakerPlayerBlue.getPlayer().getName() + "!", 5, 40, 5);
        broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.GREEN + "Wybrano playerow: ", tieBreakerPlayerRed.getPlayer().getName() + " i " + tieBreakerPlayerBlue.getPlayer().getName() + "!", 5, 40, 5);
        tieBreakerPlayerRed.setInTieBreaker(true);
        tieBreakerPlayerBlue.setInTieBreaker(true);
        tieBreakerPlayerRed.setStage(5);
        tieBreakerPlayerRed.setStage(5);
        tieBreakerPlayerBlue.setStage(6);

        for (PBTeamPlayer p : getAllPBPlayers()) {
            if (!p.isKilled() && !p.isInTieBreaker()) {
                while(ArenaListener.freezePlayers.remove(p.getPlayer())) {
                }
                this.addSpectator(p.getPlayer(), true);
            } else if (p.isInTieBreaker()) {
                p.getPlayer().teleport(stages.get(p.getStageAbsolute()).getCenter());
                p.getBPlayer().blockChi();
            }
        }
        new BukkitRunnable() {
            boolean moreStages = true;
            int i = 10;
            public void run() {
                if (!inGame) {
                    cancel();
                    return;
                }
                if (i != 0) {
                    broadcastTitle(ChatColor.BOLD + "" + NumberChatColor.getFromValue(i).getChatColor() + i, "", 0, 20, 0);
                    broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + NumberChatColor.getFromValue(i).getChatColor() + i, "", 0, 20, 0);
                    if (ArenaCreateCommand.paste(null, getID(), String.valueOf(11 - i))) {
                        if ((11 - i) <= Arena.TBraisingStages) {
                            tieBreakerPlayerRed.getPlayer().teleport(tieBreakerPlayerRed.getPlayer().getLocation().clone().add(0, 1, 0));
                            tieBreakerPlayerBlue.getPlayer().teleport(tieBreakerPlayerBlue.getPlayer().getLocation().clone().add(0, 1, 0));
                        }
                    } else {
                        moreStages = false;
                    }
                } else {
                    moreStages = false;
                    while (ArenaListener.freezePlayers.remove(tieBreakerPlayerBlue.getPlayer())) {
                    }
                    while (ArenaListener.freezePlayers.remove(tieBreakerPlayerRed.getPlayer())) {
                    }
                    tieBreakerPlayerRed.getBPlayer().unblockChi();
                    tieBreakerPlayerBlue.getBPlayer().unblockChi();
                    instance.runChecker();
                    broadcastTitle(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "START!", "", 0, 15, 5);
                    broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "Pojedynek zostal rozpoczety!", "", 0, 40, 5);
                    this.cancel();
                }
                i--;
            }
        }.runTaskTimer(ProBending.plugin, 40L, 20L);
    }

    public void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (PBTeamPlayer player : getAllPBPlayers()) {
            if (!player.isKilled()) {
                player.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }
        }
    }

    public void broadcastTitleSpectatorsOnly(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : spectators) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public void deleteTBArena() {
        ArenaCreateCommand.paste(null, getID(), "0");
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
        while(ArenaListener.freezePlayers.remove(player.getPlayer())) {
        }
        Iterator<PBTeamPlayer> i = player.getTeam().getPBPlayers().iterator();
        while (i.hasNext()) {
            PBTeamPlayer pb = i.next();
            if (!pb.isKilled()) {


                if (pb.aboveStage(player.getStage(), false)) {
                    break;
                }
                if (!i.hasNext()) {
                    claimStage(player.getTeam().getEnemyTeamTag());
                    break;
                }
            }
        }
        player.getBPlayer().unblockChi();
        player.getPlayer().getWorld().strikeLightningEffect(player.getPlayer().getLocation());
        player.setInTieBreaker(false);
        player.setKilled(true);
        player.setStage(11);
        addSpectator(player.getPlayer(), true);
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
        if (player.getPlayer() == null) {
            return;
        }
        if (!player.getPlayer().isDead()) {
            player.getPlayer().setGameMode(GameMode.SURVIVAL);
            player.getPlayer().teleport(Arena.spawn());
        }
        while(ArenaListener.freezePlayers.remove(player.getPlayer())) {
        }
        player.getBPlayer().unblockChi();
        player.setInTieBreaker(false);
        player.setInGame(false);
        Arena.playersPlaying.remove(player.getPlayer());
        player.setPlayerToNull();
        if (player.getTeam().checkWipeOut()) {
            stopGame(player.getTeam().getEnemyTeamTag());
        }
    }

    public void removeSpectator(Player player) {
        if (this.spectators.contains(player)) {
            if (getAllPlayers().contains(player)) {
                this.removePlayer(getPBPlayer(player));
            }
            if (!player.getPlayer().isDead()) {
                player.getPlayer().setGameMode(GameMode.SURVIVAL);
                player.getPlayer().teleport(Arena.spawn());
            }
            while(ArenaListener.freezePlayers.remove(player)) {
            }
            this.spectators.remove(player);
            Arena.playersSpectating.remove(player);
        }
    }

    public void addSpectator(Player player, boolean force) {
        if (Arena.playersPlaying.contains(player)) {
            if (this.getAllPlayers().contains(player)) {
                if (force) {
                    getPBPlayer(player).setKilled(true);
                    getPBPlayer(player).setInTieBreaker(false);
                    getPBPlayer(player).setKilled(true);
                    getPBPlayer(player).setStage(11);
                } else {
                    return;
                }
            }
        }
        Location t = getCenter().clone();
        t.setY(t.getY() + 6);
        player.teleport(t);
        player.setGameMode(GameMode.SPECTATOR);
        while(ArenaListener.freezePlayers.remove(player)) {
        }
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
                if (!isInTieBreaker()) {
                    if (roundTimeCounter > roundTime) {
                        nextMidRound();
                        this.cancel();
                        return;
                    }
                } else {
                    if (roundTimeCounter > tieBreakerRoundTime) {
                        stopGame(null);
                        this.cancel();
                    }
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

                        if (player.getCurrentStage() == StageEnum.WholeArena || player.getCurrentStage().getID() == 10) {
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
                                if (player.isInTieBreaker()) {
                                    stopGame(team.getEnemyTeamTag());
                                    break;
                                }
                                if (player.getStage() == 1) {
                                    player.debug("BEFORE KILL");
                                    killPlayer(player);
                                    player.debug("AFTER KILL");
                                }
                                broadcastTitleSpectatorsOnly("", player.getTeam().getColor() + "Gracz " + player.getPlayer().getName() + " zostal cofniety o stefe!", 5, 40, 5);
                                Vector vector = (stages.get(player.getStageAbsolute()).getCenter().subtract(player.getPlayer().getLocation()).toVector().normalize());
                                player.getPlayer().setVelocity(vector);
                                Iterator<PBTeamPlayer> i = team.getPBPlayers().iterator();
                                while (i.hasNext()) {
                                    PBTeamPlayer pb = i.next();
                                    if (pb.isKilled()) {
                                        continue;
                                    }
                                    if (pb.aboveStage(player.getStage(), false)) {
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

    public boolean checkForMissingStages() {
        if (stages.size() != 12) {
            System.out.println("Arena " + getID() + " hasnt created all stages! Current: " + stages.size());
            return false;
        }
        for (StageEnum se : StageEnum.values()) {
            if (!(se == StageEnum.BackBLUE || se == StageEnum.BackRED || se == StageEnum.Line || se == StageEnum.WholeArena)) {
                if (stages.get(se.getID()).getPlayer1Teleport() == null) {
                    System.out.println("In stage " + se.toString() + " there is no teleport player1! (Arena: " + this.getID() + ")");
                    return false;
                }
                if (stages.get(se.getID()).getPlayer2Teleport() == null) {
                    System.out.println("In stage " + se.toString() + " there is no teleport player2! (Arena: " + this.getID() + ")");
                    return false;
                }
                if (stages.get(se.getID()).getPlayer3Teleport() == null) {
                    System.out.println("In stage " + se.toString() + " there is no teleport player3! (Arena: " + this.getID() + ")");
                    return false;
                }
                if (stages.get(se.getID()).getCenter() == null) {
                    System.out.println("In stage " + se.toString() + " there is no teleport center! (Arena: " + this.getID() + ")");
                    return false;
                }
            }

        }
        return true;
    }
    
    public void setUpArena() {
        for (final StageEnum e : StageEnum.values()) {
            this.stages.put(e.getID(), new Stage(e, this));
            System.out.println("Creating arena " + stages.get(e.getID()).getStage().toString() + "...");
        }
        System.out.println("Ended creating arena "+ getID() + "!");
    }

    public Stage getStage(final int ID) {
        return this.stages.get(ID);
    }

    public boolean isInGame() {
        return this.inGame;
    }
}
