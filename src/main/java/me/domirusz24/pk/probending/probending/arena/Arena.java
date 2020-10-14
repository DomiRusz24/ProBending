package me.domirusz24.pk.probending.probending.arena;

import com.projectkorra.projectkorra.BendingPlayer;
import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.commands.ArenaCreateCommand;
import me.domirusz24.pk.probending.probending.arena.kit.PlayerKit;
import me.domirusz24.pk.probending.probending.arena.misc.*;
import me.domirusz24.pk.probending.probending.arena.stages.Stage;
import me.domirusz24.pk.probending.probending.arena.stages.StageEnum;
import me.domirusz24.pk.probending.probending.arena.stages.StageTeleports;
import me.domirusz24.pk.probending.probending.arena.team.PBTeamPlayer;
import me.domirusz24.pk.probending.probending.arena.team.Team;
import me.domirusz24.pk.probending.probending.arena.team.TeamTag;
import me.domirusz24.pk.probending.probending.arena.team.TempTeam;
import me.domirusz24.pk.probending.probending.config.ConfigManager;
import me.domirusz24.pk.probending.probending.config.ConfigMethods;
import me.domirusz24.pk.probending.probending.config.winlosecommandsconfig.ConfigEvents;
import me.domirusz24.pk.probending.probending.data.PlayerData;
import me.domirusz24.pk.probending.probending.data.PlayerDataEnum;
import me.domirusz24.pk.probending.probending.misc.CountDown;
import me.domirusz24.pk.probending.probending.misc.CustomScoreboard;
import me.domirusz24.pk.probending.probending.misc.GeneralMethods;
import me.domirusz24.pk.probending.probending.misc.customitems.CustomItems;
import me.domirusz24.pk.probending.probending.misc.customsigns.TeamStartSign;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Arena {

    // Default spawn after game

    public static Location getSpawn() {
        return ConfigMethods.getLocation("spawn");
    }

    public static void setSpawn(Location location) {
        ConfigMethods.saveLocation("spawn", location);
    }

    // Static getters/setters

    public static Arena getArena(Player player) {
        for (Arena a : Arenas) {
            if (a.isInGame()) {
                if (a.getAllPlayers().contains(player)) {
                    return a;
                }
            }
        }
        return null;
    }

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

    public static ArrayList<String> getArenaRules() {
        ArrayList<String> info = new ArrayList<>();
        info.add("SKARGI:");
        info.add("Gameplay - DomiRusz24");
        info.add("Mapa - Ta osoba woli byc anonimowa");
        info.add("Config - Ta osoba woli byc anonimowa");
        info.add("");
        info.add(ChatColor.BOLD + "" + ChatColor.GREEN + "ZASADY GRY:");
        info.add(ChatColor.RED + " - Arena ma 6 stref, jak sie cofniesz o strefe to tam zostajesz, i nie mozesz przejsc o strefe do przodu.");
        info.add(ChatColor.RED + " - Gdy odepchnie sie cala druzyne przeciwna o jedna strefe, to wtedy mozna zdobyc nastepna strefe przechodzac na nia.");
        info.add(ChatColor.RED + " - Jezeli wypadniesz z areny (z tylu) lub kiedy zginiesz to zostajesz dyskwalifikowany");
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "JAK WYGRAC GRE:");
        info.add(ChatColor.DARK_AQUA + " - Zabij wszystkich przecinikow lub");
        info.add(ChatColor.DARK_AQUA + " - Wygraj " + winningRound + " rund lub");
        info.add(ChatColor.DARK_AQUA + " - W przypadku remisu, wygraj TieBreaker.");
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "JAK WYGRAC RUNDE:");
        info.add(ChatColor.YELLOW + " - Miej wiecej osob nie dyskwalifikowanych lub");
        info.add(ChatColor.YELLOW + " - jezeli jest po tyle samo osob, miej wiecej stref.");
        info.add(ChatColor.YELLOW + " - Runda trwa " + roundTime / (20 * 60) + " minut.");
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "TIEBREAKER:");
        info.add(ChatColor.AQUA + " - TieBreaker jest to walka 1 na 1 ktora odbywa sie na srodkowej strefie (kolo).");
        info.add(ChatColor.AQUA + " - Zeby wygrac musisz zabic, lub pchnac przeciwnika poza kolo.");
        info.add(ChatColor.AQUA + " - TieBreaker trwa " + tieBreakerRoundTime / (20 * 60) + " minut, jezeli nikt nie wygra w ciagu tego czasu, gra konczy sie remisem.");
        return info;
    }

    //----------------


    public static final ArrayList<Arena> Arenas = new ArrayList<>();
    public static final HashSet<Player> playersPlaying = new HashSet<>();
    private static final HashMap<Player, Arena> playersSpectating = new HashMap<>();
    private final HashSet<Player> spectators = new HashSet<>();

    //----------------

    private static final int tickUpdate = ProBending.plugin.getConfig().getInt("arena.tickUpdate");
    private static final int winningRound = ProBending.plugin.getConfig().getInt("arena.winningRound");
    private static final int tieBreakerRoundTime = ProBending.plugin.getConfig().getInt("arena.tieBreakerRound");
    private static final int roundTime = ProBending.plugin.getConfig().getInt("arena.roundTime");
    private static final int TBraisingStages = ProBending.plugin.getConfig().getInt("TB.raisingStages");

    // GameQueue

    private static ConcurrentLinkedQueue<String> gameQueue = new ConcurrentLinkedQueue<>();

    public static void addToQueue(Player player) {
        gameQueue.offer(player.getUniqueId().toString());
    }

    public static void updateQueue() {
        if (gameQueue.size() > 5) {
            for (Arena a : Arenas) {
                if (!a.isInGame()) {
                }
            }
        }
    }

    //----------------

    public final TempTeam blueTempTeam;
    public final TempTeam redTempTeam;
    public final TeamStartSign blueSign;
    public final TeamStartSign redSign;

    private Team TeamBlue = null;
    private Team TeamRed = null;

    //---

    private boolean inGame;
    private Arena instance;
    private final HashMap<Integer, Stage> stages;
    private int roundTimeCounter = 0;
    private final String ID;
    private int roundNumber;
    private TeamTag teamGainingStage = null;
    private boolean isClaimingStage = false;
    private boolean inTieBreaker;
    private PBTeamPlayer tieBreakerPlayerBlue;
    private PBTeamPlayer tieBreakerPlayerRed;
    private final ArenaGetter getter;
    private HologramManager hologramManager;
    private final CustomScoreboard scoreboard;

    //----------------

    public Arena(final Location location, String ID) {
        this.inGame = false;
        this.stages = new HashMap<>();
        this.roundNumber = 0;
        Arena.Arenas.add(this);
        this.ID = ID;
        setCenter(location);
        this.blueTempTeam = new TempTeam();
        this.redTempTeam = new TempTeam();
        this.getter = new ArenaGetter(this);
        scoreboard = new CustomScoreboard("Arena" + ID, ChatColor.BOLD + "" + ChatColor.GRAY + "Info o grze:", DisplaySlot.SIDEBAR);
        redSign = new TeamStartSign(this, TeamTag.RED);
        blueSign = new TeamStartSign(this, TeamTag.BLUE);
        this.setUpArena();
    }

    public Arena(final Location location, String ID, boolean firsttime) {
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
        this.getter = new ArenaGetter(this);
        scoreboard = new CustomScoreboard("Arena" + ID, ChatColor.BOLD + "" + ChatColor.GRAY + "Info o grze:", DisplaySlot.SIDEBAR);
        redSign = new TeamStartSign(this, TeamTag.RED);
        blueSign = new TeamStartSign(this, TeamTag.BLUE);
        this.setUpArena();
    }

    public void setUpArena() {
        for (final StageEnum e : StageEnum.values()) {
            this.stages.put(e.getID(), new Stage(e, this));
            System.out.println("Creating arena " + stages.get(e.getID()).getStage().toString() + "...");
        }
        System.out.println("Ended creating arena "+ getID() + "!");
    }

    //----------------

    public void hookUpHologram() {
        this.hologramManager = new HologramManager(this);
    }

    // Scoreboard manager

    public void updateScoreboard() {
        if (isInGame()) {
            scoreboard.edit(0, ChatColor.GREEN + "Runda", String.valueOf(getRoundNumber()));
            if (isInTieBreaker()) {
                scoreboard.edit(1, ChatColor.DARK_GRAY + "Pozostaly czas", ((tieBreakerRoundTime - roundTimeCounter) / 20 / 60) + " minut");
            } else {
                scoreboard.edit(1, ChatColor.DARK_GRAY + "Pozostaly czas", ((roundTime - roundTimeCounter) / 20 / 60) + " minut");
            }
            scoreboard.edit(2, " ");
            int i = 2;
            for (TeamTag teamTag : TeamTag.values()) {
                Team team = getTeamByTag(teamTag);
                i++;
                scoreboard.edit(i, ChatColor.BOLD + "" + team.getColor() + "Druzyna " + team.getPolishName(), ChatColor.GRAY + String.valueOf(team.getPoints()));
                for (PBTeamPlayer teamPlayer : team.getPBPlayers()) {
                    i++;
                    if (inTieBreaker) {
                        if (teamPlayer.isInTieBreaker()) {
                            scoreboard.edit(i, teamPlayer.getElement().getPolish() + team.getColor() + " - " + teamPlayer.getPlayer().getName(), teamPlayer.getTiredMeter() + "%");
                        } else {
                            scoreboard.edit(i, teamPlayer.getElement().getPolish() + ChatColor.GRAY + " - " + teamPlayer.getPlayer().getName(), "Oglada");
                        }
                    } else {
                        scoreboard.edit(i, teamPlayer.getElement().getPolish() + team.getColor() + " - " + teamPlayer.getPlayer().getName(), teamPlayer.isKilled() ? "Odpadl!" : teamPlayer.getTiredMeter() + "%");
                    }
                }
            }
        } else {
            scoreboard.reset();
        }
        scoreboard.removeAll();
        getAllPlayersAndSpectators().forEach(scoreboard::addPlayer);
        scoreboard.update();
    }

    // Start Game

    public void startGame(Player player, boolean force) {
        if (!isInGame()) {
            if (!checkForMissingStages()) {
                if (player != null) player.sendMessage(ProBending.errorPrefix + "Ta arena nie jest skonczona!");
                this.stopGame();
            } else if (force || (blueTempTeam.readyToPlay() && redTempTeam.readyToPlay())) {
                ArrayList<Player> all = new ArrayList<>(blueTempTeam.getAllPlayers());
                all.addAll(redTempTeam.getAllPlayers());
                for (Player p : all) {
                    if (p == null) {
                        continue;
                    }
                    Elements ele = GeneralMethods.getPlayerElement(BendingPlayer.getBendingPlayer(p));
                    if (ele == null) {
                        all.forEach(e -> e.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Gracz " + p.getPlayer().getName() + " probowal wejsc na arene z wieloma zywiolami!"));
                        all.forEach(e -> e.sendTitle(ChatColor.RED + "Nie udalo sie rozpoczac gre!", ChatColor.GRAY + "Gracz " + p.getPlayer().getName() + " probowal wejsc na arene z wieloma zywiolami!", 10, 60, 10));
                        return;
                    } else if (ele.equals(Elements.NonBender)) {
                        if (BendingPlayer.getBendingPlayer(p.getPlayer()).getElements().isEmpty()) {
                            p.sendMessage(ProBending.errorPrefix + "Prosze wybierz zywiol za pomoca " + ChatColor.BOLD + "Ksiegi Zywiolow!");
                            all.forEach(e -> e.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Gracz " + p.getPlayer().getName() + " nie wybral zywiol!"));
                            all.forEach(e -> e.sendTitle(ChatColor.RED + "Nie udalo sie rozpoczac gre!", ChatColor.GRAY + "Gracz " + p.getPlayer().getName() + " nie wybral zywiol!", 10, 60, 10));
                        } else {
                            all.forEach(e -> e.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Gracz " + p.getPlayer().getName() + " nie przypial zadne ruchy!"));
                            all.forEach(e -> e.sendTitle(ChatColor.RED + "Nie udalo sie rozpoczac gre!", ChatColor.GRAY + "Gracz " + p.getPlayer().getName() + " nie przypial zadne ruchy!", 10, 60, 10));
                        }
                        return;
                    } else if (ele.equals(Elements.Illegal)) {
                        all.forEach(e -> e.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Gracz " + p.getPlayer().getName() + " probowal wejsc na arene z zakazanymi ruchami!"));
                        all.forEach(e -> e.sendTitle(ChatColor.RED + "Nie udalo sie rozpoczac gre!", ChatColor.GRAY + "Gracz " + p.getPlayer().getName() + " probowal wejsc na arene z zakazanymi ruchami!", 10, 60, 10));
                        return;
                    }
                }
                this.setUpStart(new Team(blueTempTeam, TeamTag.BLUE, this), new Team(redTempTeam, TeamTag.RED, this));
                if (isInGame()) {
                    if (player != null)
                        player.sendMessage(ProBending.successPrefix + "Rozpoczela sie gra!");
                    System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
                } else {
                    if (player != null)
                        player.sendMessage(ProBending.errorPrefix + "Gra sie nie rozpoczela.");
                }
            } else {
                if (player != null)
                    player.sendMessage(ProBending.errorPrefix + "Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
            }
        } else {
            player.sendMessage(ProBending.errorPrefix + "Arena " + this.ID + " nie moze sie zaczac z powodu braku graczy!");
        }
    }

    private void setUpStart(final Team team1, final Team team2) {
        this.TeamBlue = team1;
        this.TeamRed = team2;
        if (TeamBlue.getPlayers().isEmpty() && TeamRed.getPlayers().isEmpty()) {
            return;
        }
        for (Player p : getAllPlayers()) {
            if (p.isDead()) {
                System.out.println("Nie moze sie rozpoczac gra z powodu smierci gracza! (Arena " + this.ID + " (FORCE) )");
                return;
            }
        }
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "wlaczarena " + getID());
        this.deleteTBArena();
        this.inGame = true;
        isClaimingStage = false;
        this.teamGainingStage = null;
        this.inTieBreaker = false;
        roundNumber = 0;
        System.out.println("Rozpoczela sie gra! (Arena " + this.ID + ")");
        ArrayList<String> rules = getArenaRules();
        for (PBTeamPlayer e : this.getAllPBPlayers()) {
            Arena.playersPlaying.add(e.getPlayer());
            rules.forEach(e.getPlayer()::sendMessage);
        }
        if (getter.getGetter(ArenaGetters.Spectator) != null) {
            ArrayList<Player> spec = getter.getPlayersInside(ArenaGetters.Spectator);
            if (!spec.isEmpty()) {
                spec.forEach(player -> addSpectator(player, false));
            }
        }
        ConfigEvents.ArenaStart.run(this, getAllPlayers());
        this.nextRound();
        ListHologram.update();
    }

    // Player Getter

    public void getPlayersFromGetters() {
        blueTempTeam.removeAllPlayers();
        redTempTeam.removeAllPlayers();
        ArrayList<Player> red = null;
        ArrayList<Player> blue = null;
        if (getter.getGetter(ArenaGetters.Red) != null) {
            red = getter.getPlayersInside(ArenaGetters.Red);
        }
        if (getter.getGetter(ArenaGetters.Blue) != null) {
            blue = getter.getPlayersInside(ArenaGetters.Blue);
        }
        for (int i = 0; i < 3; i++) {
            if (red != null) {
                if (i < red.size()) {
                    redTempTeam.addPlayer(red.get(i));
                }
            }
            if (blue != null) {
                if (i < blue.size()) {
                    blueTempTeam.addPlayer(blue.get(i));
                }
            }
        }
    }

    //----------------

    public void displayArenaInfo(Player player) {
        final ArrayList<String> info = new ArrayList<>();
        final ChatColor inGame = isInGame() ? ChatColor.RED : ChatColor.GREEN;
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
        info.add(ChatColor.BOLD + "" + inGame + "Arena " + getID());
        if (getCenter() == null) {
            info.add(ChatColor.BOLD + "Prosze uzyc komendy /arena " + getID() + " set center");
        } else {
            info.add(ChatColor.BOLD + "Srodek areny: " + getCenter().getBlock().getLocation().getX() + " " + getCenter().getBlock().getLocation().getY() + " " + getCenter().getBlock().getLocation().getZ());
        }
        info.add(ChatColor.BOLD + "ID:" + getID());
        final String inGameInfo = isInGame() ? "TAK" : "NIE";
        info.add(ChatColor.BOLD + "" + inGame + "W grze: " + inGameInfo);
        if (!isInGame()) {
            info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "DRUZYNY CZEKAJACE NA START: ");
            info.add("");
            info.add(ChatColor.BOLD + "" + ChatColor.RED + "CZERWONA: ");
            info.addAll(getTempTeamByTag(TeamTag.RED).getInfo());
            info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "NIEBIESKA: ");
            info.addAll(getTempTeamByTag(TeamTag.BLUE).getInfo());
            info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
        } else {
            info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "RUNDA: " + getRoundNumber());
            info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "W TIEBREAKER: " + (isInTieBreaker() ? "TAK" : "NIE"));
            info.add(ChatColor.BOLD + "" + ChatColor.ITALIC + "DRUZYNY: ");
            info.add("");
            info.addAll(getTeamRed().getInfo());
            info.addAll(getTeamBlue().getInfo());
            info.add("");
            info.add(ChatColor.BOLD + "SPEKTATORZY: ");
            for (Player p : getSpectators()) {
                info.add(p.getName());
            }
        }
        info.forEach(player::sendMessage);
    }

    // Stop game

    public void stopGame() {
        if (this.inGame) {
            ConfigEvents.ArenaLose.run(this);
            ConfigEvents.ArenaWin.run(this);
            if (getAllPBPlayers() != null && !getAllPBPlayers().isEmpty()) {
                broadcastTitle(ChatColor.DARK_RED + "Koniec gry!", "", 10, 60, 10);
                broadcastTitleSpectatorsOnly(ChatColor.DARK_RED + "Koniec gry!", "", 10, 60, 10);
            }
            removeAllPlayers();
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
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "wylaczarena " + getID());
            hologramManager.refreshInfo();
            ListHologram.update();
        }
    }

    public void stopGame(TeamTag teamTag) {
        if (this.inGame) {
            if (getAllPBPlayers() != null && !getAllPBPlayers().isEmpty()) {
                if (teamTag != null) {
                    broadcastTitle(ChatColor.DARK_GREEN + "Koniec gry!", getTeamByTag(teamTag).getColor() + "Wygrala druzyna " + getTeamByTag(teamTag).getPolishName() + "!", 10, 60, 10);
                    broadcastTitleSpectatorsOnly(ChatColor.DARK_GREEN + "Koniec gry!", getTeamByTag(teamTag).getColor() + "Wygrala druzyna " + getTeamByTag(teamTag).getPolishName() + "!", 10, 60, 10);
                    ConfigEvents.ArenaWin.run(this);
                    ConfigEvents.ArenaLose.run(this);
                    if (getTeamByTag(teamTag).getPlayers() != null) {
                        for (PBTeamPlayer e : getTeamByTag(teamTag).getPBPlayers()) {
                            ConfigEvents.ArenaWin.runPlayerOnly(this, e.getPlayer());
                            e.raiseData(PlayerDataEnum.PlayerWins, 1);
                            e.raiseData(PlayerDataEnum.WinStreak, 1);
                        }
                    }
                    if (getTeamByTag(getTeamByTag(teamTag).getEnemyTeamTag()).getPlayers() != null) {
                        for (PBTeamPlayer e : getTeamByTag(getTeamByTag(teamTag).getEnemyTeamTag()).getPBPlayers()) {
                            ConfigEvents.ArenaLose.runPlayerOnly(this, e.getPlayer());
                            e.raiseData(PlayerDataEnum.PlayerLoss, 1);
                            e.setData(PlayerDataEnum.WinStreak, 0);
                        }
                    }
                } else {
                    broadcastTitle(ChatColor.DARK_GREEN + "Koniec gry!", ChatColor.WHITE + "Gra zostala zakonczona remisem!", 10, 60, 10);
                    broadcastTitleSpectatorsOnly(ChatColor.DARK_GREEN + "Koniec gry!", ChatColor.WHITE + "Gra zostala zakonczona remisem!", 10, 60, 10);
                    ConfigEvents.ArenaLose.run(this);
                    ConfigEvents.ArenaWin.run(this);
                    getAllPBPlayers().forEach(e -> e.raiseData(PlayerDataEnum.PlayerTie, 1));
                }
            }
            removeAllPlayers();
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
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "wylaczarena " + getID());
            ListHologram.update();
        }
    }

    // Round manager

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
            if (!player.getPlayer().isOnline()) {
                continue;
            }
            if (!player.isKilled()) {
                player.setStage(StageEnum.convertID(4, player.getTeam().getTeamTag()));
                player.getPlayer().teleport(this.stages.get(player.getStageAbsolute()).getTeleportByNumber(player.getTeam().getPBPlayerNumber(player)));
                ArenaListener.freezePlayer(player.getPlayer());
                player.getPlayer().setHealth(20);
                player.getBPlayer().blockChi();
                player.getPlayer().getActivePotionEffects().forEach(e -> player.getPlayer().removePotionEffect(e.getType()));
                player.getPlayer().setWalkSpeed(0.2f);
                player.getPlayer().getInventory().clear();
                CustomItems.SpectatorLeave.getItem().givePlayer(player.getPlayer(), 8);
                player.setTiredMeter(0);
            }
        }
        hologramManager.refreshInfo();
        updateScoreboard();
        this.instance = this;
        new CountDown(10, "", () -> {
            broadcastTitle(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "START!", "", 0, 15, 5);
            broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "Gra zostala rozpoczeta!", "", 0, 20, 5);
            for (PBTeamPlayer pbTeamPlayer : getAllPBPlayers()) {
                if (!pbTeamPlayer.getPlayer().isOnline()) {
                    continue;
                }
                Player player = pbTeamPlayer.getPlayer();
                ArenaListener.unFreezePlayer(player);
                pbTeamPlayer.getBPlayer().unblockChi();
                player.getInventory().clear();
                PlayerKit.getKits(player);
            }
            runChecker();
        }, 40, true, this).run(getAllPlayersAndSpectators());
    }

    public void nextMidRound() {
        if (!this.inGame) {
            return;
        }
        this.roundTimeCounter = 0;
        broadcastTitle(ChatColor.BOLD + "" + ChatColor.RED + "Koniec rundy!", "", 0, 40, 5);
        broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.RED + "Runda " + getRoundNumber() + " zostala zakonczona!", "", 0, 40, 5);
        ArenaCreateCommand.getRollBack(null, getID());
        for (PBTeamPlayer player : this.getAllPBPlayers()) {
            if (player.getPlayer() == null) {
                continue;
            }
            if (!player.getPlayer().isOnline()) {
                continue;
            }
            if (!player.isKilled()) {
                player.getBPlayer().blockChi();
                ArenaListener.freezePlayer(player.getPlayer());
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
                    ConfigEvents.RoundWin.run(instance, team.getPlayers());
                    ConfigEvents.RoundLose.run(instance, getTeamByTag(team.getEnemyTeamTag()).getPlayers());
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

    public TeamTag whoIsWinning() {
        if (!inGame) {
            return null;
        }
        int redPlayerPoints = 0;
        for (PBTeamPlayer player : this.getTeamRed().getPBPlayers()) {
            if (!player.isKilled()) {
                redPlayerPoints = redPlayerPoints + player.getStage();
            }
        }
        int bluePlayerPoints = 0;
        for (PBTeamPlayer player : this.getTeamBlue().getPBPlayers()) {
            if (!player.isKilled()) {
                bluePlayerPoints = bluePlayerPoints + player.getStage();
            }
        }
        if (redPlayerPoints > bluePlayerPoints) {
            return TeamTag.RED;
        } else if (redPlayerPoints < bluePlayerPoints) {
            return TeamTag.BLUE;
        } else {
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
                return null;
            }
        }
    }

    // TieBreaker

    public void deleteTBArena() {
        ArenaCreateCommand.paste(null, getID(), "0");
    }

    public void startTieBreaker() {
        if (!inGame) {
            return;
        }
        roundNumber++;
        Random r = new Random();
        int redPlayerNumber = r.nextInt(2) + 1;
        int bluePlayerNumber = r.nextInt(2) + 1;
        tieBreakerPlayerRed = TeamRed.getPBPlayer(redPlayerNumber);
        while (tieBreakerPlayerRed == null || tieBreakerPlayerRed.getPlayer() == null || !tieBreakerPlayerRed.isKilled()) {
            if (redPlayerNumber == 3) {
                redPlayerNumber = 1;
            } else {
                redPlayerNumber++;
            }
            tieBreakerPlayerRed = TeamRed.getPBPlayer(redPlayerNumber);
        }
        while (tieBreakerPlayerBlue == null || tieBreakerPlayerBlue.getPlayer() == null || !tieBreakerPlayerBlue.isKilled()) {
            if (bluePlayerNumber == 3) {
                bluePlayerNumber = 1;
            } else {
                bluePlayerNumber++;
            }
            tieBreakerPlayerBlue = TeamBlue.getPBPlayer(bluePlayerNumber);
        }

        broadcastTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "Wybrano playerow: ", tieBreakerPlayerRed.getTeamName() + " i " + tieBreakerPlayerBlue.getTeamName() + "!", 5, 40, 5);
        broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.GREEN + "Wybrano playerow: ", tieBreakerPlayerRed.getTeamName() + " i " + tieBreakerPlayerBlue.getTeamName() + "!", 5, 40, 5);
        inTieBreaker = true;
        tieBreakerPlayerRed.setInTieBreaker(true);
        tieBreakerPlayerBlue.setInTieBreaker(true);
        tieBreakerPlayerRed.getPlayer().getInventory().clear();
        tieBreakerPlayerBlue.getPlayer().getInventory().clear();
        PlayerKit.getKits(tieBreakerPlayerBlue.getPlayer());
        PlayerKit.getKits(tieBreakerPlayerRed.getPlayer());
        tieBreakerPlayerRed.setStage(5);
        tieBreakerPlayerBlue.setStage(6);
        for (PBTeamPlayer p : getAllPBPlayers()) {
            if (!p.isKilled() && !p.isInTieBreaker()) {
                ArenaListener.unFreezePlayer(p.getPlayer());
                this.addSpectator(p.getPlayer(), true);
            } else if (p.isInTieBreaker()) {
                p.getPlayer().teleport(stages.get(p.getStageAbsolute()).getCenter());
                p.getBPlayer().blockChi();
            }
        }
        hologramManager.refreshInfo();
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
                    ArenaListener.unFreezePlayer(tieBreakerPlayerBlue.getPlayer());
                    ArenaListener.unFreezePlayer(tieBreakerPlayerRed.getPlayer());
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

    // -- Player interaction --

    // Players and Spectators

    public void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (PBTeamPlayer player : getAllPBPlayers()) {
            if (!player.isKilled()) {
                player.getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            }
        }
    }

    public void broadcastMessage(String message, boolean spectators) {
        if (spectators) {
            getAllPlayersAndSpectators().forEach(e -> e.sendMessage(message));
        } else {
            getAllPlayers().forEach(e -> e.sendMessage(message));
        }
    }

    public void broadcastTitleSpectatorsOnly(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : spectators) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    // Player

    public void killPlayer(final PBTeamPlayer player) {
        if (player.isKilled()) {
            return;
        }
        ArenaListener.unFreezePlayer(player.getPlayer());
        player.setInTieBreaker(false);
        player.setKilled(true);
        if (checkForEmptyStage(player.getStage() - 1, player.getTeam().getTeamTag())) {
            claimStage(player.getTeam().getEnemyTeamTag());
        }
        player.raiseData(PlayerDataEnum.PlayerDeaths, 1);
        Player killer = ArenaListener.lastDamage.get(player.getPlayer());
        if (killer != null) {
            getPBPlayer(killer).raiseData(PlayerDataEnum.PlayerKills, 1);
        }
        ConfigEvents.PlayerDeath.run(this, player.getPlayer());
        player.getBPlayer().unblockChi();
        player.getPlayer().getWorld().strikeLightningEffect(player.getPlayer().getLocation());
        addSpectator(player.getPlayer(), true);
        for (Player p : getAllPlayers()) {
            p.sendMessage(ChatColor.GRAY + "Gracz " + player.getTeamName() + " dostal K-O!");
        }
        broadcastTitleSpectatorsOnly("", ChatColor.GRAY + "Gracz " + player.getTeamName() + " dostal K-O!", 5, 40, 5);
        player.debug();
        if (player.getTeam().checkWipeOut()) {
            stopGame(player.getTeam().getEnemyTeamTag());
        }
        hologramManager.refreshInfo();
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
            player.getPlayer().teleport(Arena.getSpawn());
        }
        ArenaListener.unFreezePlayer(player.getPlayer());
        player.revertInventory();
        ConfigManager.getDataConfig().reloadConfig();
        player.transferData();
        ConfigManager.getDataConfig().saveConfig();
        player.getBPlayer().unblockChi();
        player.setInTieBreaker(false);
        player.setInGame(false);
        scoreboard.removePlayer(player.getPlayer());
        Arena.playersPlaying.remove(player.getPlayer());
        player.setPlayerToNull();
        if (player.getTeam().checkWipeOut()) {
            stopGame(player.getTeam().getEnemyTeamTag());
        }
        hologramManager.refreshInfo();
        updateScoreboard();
    }

    // Spectators

    public void removeSpectator(Player player) {
        if (this.spectators.contains(player)) {
            if (playersPlaying.contains(player)) {
                if (getAllPlayers().contains(player)) {
                    this.removePlayer(getPBPlayer(player));
                } else {
                    return;
                }
            }
            ConfigEvents.PlayerLeaveSpectate.run(this, player);
            player.getPlayer().setGameMode(GameMode.SURVIVAL);
            player.getPlayer().setAllowFlight(false);
            player.getPlayer().teleport(Arena.getSpawn());
            ArenaListener.unFreezePlayer(player);
            if (BendingPlayer.getBendingPlayer(player) != null) BendingPlayer.getBendingPlayer(player).unblockChi();
            player.getPlayer().setCollidable(true);
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
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        if (playersSpectating.containsKey(player)) {
            playersSpectating.get(player).removeSpectator(player);
        }
        Location t = getCenter().clone();
        t.setY(t.getY() + 6);
        player.teleport(t);
        ConfigEvents.PlayerJoinSpectate.run(this, player);
        scoreboard.addPlayer(player);
        if (BendingPlayer.getBendingPlayer(player) != null) BendingPlayer.getBendingPlayer(player).blockChi();
        ArenaListener.unFreezePlayer(player);
        this.spectators.add(player);
        Arena.playersSpectating.put(player, this);
        player.setGameMode(GameMode.SPECTATOR);
        player.getPlayer().setCollidable(false);
        player.getPlayer().setAllowFlight(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    player.getPlayer().setFlying(true);
                } catch (IllegalArgumentException ignored){
                }
            }
        }.runTaskLater(ProBending.plugin, 3);
    }

    public void removeAllPlayers() {
        for (PBTeamPlayer player : new ArrayList<>(getAllPBPlayers())) {
            if (!player.getPlayer().isDead()) {
                player.getPlayer().setGameMode(GameMode.SURVIVAL);
                player.getPlayer().teleport(Arena.getSpawn());
            }
            ArenaListener.unFreezePlayer(player.getPlayer());
            player.revertInventory();
            player.transferData();
            player.getBPlayer().unblockChi();
            player.setInTieBreaker(false);
            player.setInGame(false);
            scoreboard.removePlayer(player.getPlayer());
            Arena.playersPlaying.remove(player.getPlayer());
        }
        for (Player player : new ArrayList<>(getSpectators())) {
            ConfigEvents.PlayerLeaveSpectate.run(this, player);
            player.getPlayer().setGameMode(GameMode.SURVIVAL);
            player.getPlayer().setAllowFlight(false);
            player.getPlayer().teleport(Arena.getSpawn());
            ArenaListener.unFreezePlayer(player);
            if (BendingPlayer.getBendingPlayer(player) != null) BendingPlayer.getBendingPlayer(player).unblockChi();
            player.getPlayer().setCollidable(true);
            this.spectators.remove(player);
            Arena.playersSpectating.remove(player);
        }
        updateScoreboard();
        ConfigManager.getDataConfig().saveConfig();
    }

    // -- Claiming Stage --

    public boolean checkForEmptyStage(int stag, TeamTag tag) {
        for (PBTeamPlayer p : getTeamByTag(tag).getPBPlayers()) {
            if (!p.isKilled()) {
                if (p.aboveStage(stag, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void endClaimingStage() {
        Team t = getTeamByTag(teamGainingStage);
        isClaimingStage = false;
        broadcastTitle(ChatColor.BOLD + "" + t.getColor() + "Gra zostanie odnowiona za 3 sekundy!", "", 0, 40, 5);
        broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "Gra niedlugo sie odnowi.", "", 0, 20, 5);
        new CountDown(2, "", () -> {
            broadcastTitle(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "START!", "", 0, 15, 5);
            broadcastTitleSpectatorsOnly(ChatColor.BOLD + "" + ChatColor.DARK_GREEN + "Gra zostala odnowiona!", "", 0, 20, 5);
            hologramManager.refreshInfo();
            teamGainingStage = null;
        }, 40, true, this).run(getAllPlayersAndSpectators());
    }

    public void claimStage(final TeamTag teamTag) {
        roundTimeCounter-= 400;
        final Team enemyTeam = this.getTeamByTag(teamTag);
        final ChatColor enemyTeamColor = enemyTeam.getColor();
        final String enemyTeamName = enemyTeam.getPolishName();
        isClaimingStage = true;
        teamGainingStage = teamTag;
        for (PBTeamPlayer e : enemyTeam.getPBPlayers()) {
            if (e.isKilled()) continue;
            e.setHasntGainedStageYet(true);
        }
        ConfigEvents.StageClaimPlayer.run(this, enemyTeam.getPlayers());
        hologramManager.refreshInfo();
        new BukkitRunnable() {
            final int roundNum = roundNumber;
            int i = 3;
            @Override
            public void run() {
                if (!isInGame() || isInTieBreaker() || teamGainingStage == null || !isClaimingStage || roundNum != roundNumber) {
                    this.cancel();
                    return;
                } else if (i != 0) {
                    for (PBTeamPlayer e : enemyTeam.getPBPlayers()) {
                        if (e.isKilled()) continue;
                        if (e.hasntGainedStageYet()) {
                            e.getPlayer().sendTitle(enemyTeamColor + "Przejdz na nastepna strefe!", "" , 5, 30, 5);
                        }
                    }
                    for (PBTeamPlayer e : getTeamByTag(enemyTeam.getEnemyTeamTag()).getPBPlayers()) {
                        if (e.isKilled()) continue;
                            e.getPlayer().sendTitle(enemyTeamColor + "Druzyna przeciwna zdobywa strefe!", "" , 5, 30, 5);
                    }
                } else {
                    if (!isInGame() || isInTieBreaker() || teamGainingStage == null || !isClaimingStage || roundNum != roundNumber) {
                        this.cancel();
                        return;
                    }
                    for (PBTeamPlayer e : enemyTeam.getPBPlayers()) {
                        if (e.isKilled()) continue;
                        if (e.hasntGainedStageYet()) {
                            e.setHasntGainedStageYet(false);
                            e.raiseStage();
                        }
                    }
                    endClaimingStage();
                    this.cancel();
                }
                i--;
            }
        }.runTaskTimer(ProBending.plugin, 60, 60);
        broadcastTitle(enemyTeamColor + "Druzyna " + enemyTeamName + " zdobyla strefe!", "" , 5, 60, 5);
        broadcastTitleSpectatorsOnly("", enemyTeamColor + "Druzyna " + enemyTeamName + " zdobyla strefe!", 5, 40, 5);

    }

    //------------

    public void runChecker() {
        new BukkitRunnable() {
            final int round = roundNumber;

            public void run() {
                if (getAllPlayers() == null || getAllPlayers().isEmpty()) {
                    instance.stopGame();
                    this.cancel();
                    return;
                }
                if (!inGame || round != getRoundNumber()) {
                    this.cancel();
                    return;
                }
                roundTimeCounter = roundTimeCounter + tickUpdate;
                if (!isInTieBreaker()) {
                    // Round end
                    if (roundTimeCounter > roundTime) {
                        nextMidRound();
                        this.cancel();
                        return;
                    }
                    // TieBreaker end
                } else if (roundTimeCounter > tieBreakerRoundTime) {
                    stopGame(null);
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

                        // Fall out of arena
                        if (player.getCurrentStage() == StageEnum.WholeArena || player.getCurrentStage().getID() == 10) {

                            player.teleportToStage(player.getStageAbsolute(), StageTeleports.Center);

                            // Lose Stage
                        } else if (player.belowStage(player.getStage(), false)) {
                            if (isInTieBreaker()) {
                                (new PlayerData(team.getTeamTag().equals(TeamTag.BLUE) ? tieBreakerPlayerRed.getPlayer() : tieBreakerPlayerBlue.getPlayer())).raiseData(PlayerDataEnum.WonTieBreakerRounds, 1);
                                stopGame(team.getEnemyTeamTag());
                                return;
                            } else if (teamGainingStage != null) {
                                if (player.getStage() - 1 == 1) {
                                    player.teleportToStage(player.getStageAbsolute(), StageTeleports.getStageFromNumber(player.getID()));
                                } else {
                                    player.dragToStage(player.getStageAbsolute(), StageTeleports.Center, 0.3);
                                }
                                String e = player.getTeam().getTeamTag() == teamGainingStage ? "Zdobywasz strefe, nie mozesz sie cofnac!" : "Druzyna przeciwna zdobywa strefe, nie mozesz sie cofnac!";
                                player.getPlayer().sendTitle("", team.getColor() + e, 5, 60, 5);
                            } else {
                                player.lowerStage();
                            }
                            // Stage warning
                        } else if (player.aboveStage(player.getStage(), false)) {
                            if (player.getTeam().getTeamTag() == teamGainingStage) {
                                if (player.hasntGainedStageYet()) {
                                    player.raiseStage();
                                    player.setHasntGainedStageYet(false);
                                    boolean stopGainingStage = true;
                                    for (PBTeamPlayer p : team.getPBPlayers()) {
                                        if (p.isKilled()) continue;
                                        if (p.hasntGainedStageYet()) {
                                            stopGainingStage = false;
                                        }
                                    }
                                    if (stopGainingStage) {
                                        endClaimingStage();
                                    }
                                } else {
                                    player.illegalStageGain();
                                }
                            } else {
                                if (!isInTieBreaker()) {
                                    player.illegalStageGain();
                                } else {
                                    if (player.getCurrentStage().getID() != 6) {
                                        stopGame(team.getEnemyTeamTag());
                                    }
                                }
                            }
                        } else {
                            player.resetIllegalCombo();
                        }
                    }
                }
            }
        }.runTaskTimer(ProBending.plugin, 0, tickUpdate);
    }

    // Getters && Setters

    // -- Player --

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

    public ArrayList<Player> getAllPlayersAndSpectators() {
        ArrayList<Player> e = new ArrayList<>();
        e.addAll(getAllPlayers());
        e.addAll(getSpectators());
        return e;
    }

    // Spectators
    public HashSet<Player> getSpectators() {
        return spectators;
    }

    // PBPlayers
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

    public PBTeamPlayer getPBPlayer(final Player player) {
        for (final PBTeamPlayer i : this.getAllPBPlayers()) {
            if (i.getPlayer().equals(player)) {
                return i;
            }
        }
        return null;
    }

    // -- Stages --

    public boolean checkForMissingStages() {
        if (stages.size() != 12) {
            System.out.println("Arena " + getID() + " hasnt created all stages! Current: " + stages.size());
            return false;
        }
        for (StageEnum se : StageEnum.values()) {
            if (!(se == StageEnum.BackBLUE || se == StageEnum.BackRED || se == StageEnum.Line || se == StageEnum.WholeArena)) {
                for (StageTeleports tel : StageTeleports.values()) {
                    if (stages.get(se.getID()).getTeleport(tel) == null) {
                        System.out.println("In stage " + se.toString() + " teleport " + tel.getName() + " isn't set! (Arena: " + this.getID() + ")" );
                        return false;
                    }
                }
            }

        }
        return true;
    }

    public HashMap<Integer, Stage> getStages() {
        return stages;
    }

    public Stage getStage(final int ID) {
        return this.stages.get(ID);
    }

    // -- Player Getters --

    public ArenaGetter getGetter() {
        return getter;
    }

    // -- Team --

    public Team getTeamBlue() {
        return this.TeamBlue;
    }

    public Team getTeamRed() {
        return this.TeamRed;
    }

    public Team getTeamByTag(final TeamTag teamTag) {
        return (teamTag == TeamTag.BLUE) ? this.TeamBlue : this.TeamRed;
    }

    public TempTeam getTempTeamByTag(final TeamTag teamTag) {
        return (teamTag == TeamTag.BLUE) ? this.blueTempTeam : this.redTempTeam;
    }

    // -- Round --

    public int getRoundNumber() {
        return this.roundNumber;
    }

    public boolean isInTieBreaker() {
        return inTieBreaker;
    }

    public int getRoundTime() {
        return roundTimeCounter;
    }

    // -- Arena --

    public Location getCenter() {
        return ConfigMethods.getLocation(getPath() + ".center");
    }

    public void setCenter(Location location) {
        ConfigMethods.saveLocation(getPath() + ".center", location);
    }

    public boolean isInGame() {
        return this.inGame;
    }

    public boolean isInPeace() {
        return teamGainingStage != null;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public String getID() {
        return this.ID;
    }

    public String getPath() { return "Arena.nr" + this.ID; }
}
