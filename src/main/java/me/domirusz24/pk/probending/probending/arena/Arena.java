package me.domirusz24.pk.probending.probending.arena;

import me.domirusz24.pk.probending.probending.ProBending;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class Arena {
    public static ArrayList<Arena> Arenas = new ArrayList<>();
    public static ArrayList<Player> playersPlaying = new ArrayList<>();
    public static Location spawn;

    private boolean inGame = false;
    public boolean inProgressOfCreating = false;
    private Arena instance;
    private HashMap<Integer, Stage> stages = new HashMap<>();
    private Location center;
    private Team TeamBlue;
    private Team TeamRed;
    private int ID;

    public Arena(Location location) {
        center = location;
        Arena.Arenas.add(this);
        ID = Arena.Arenas.size();
        setUpArena();
    }

    public void startGame(Team team1, Team team2) {
        this.TeamBlue = team1;
        this.TeamRed = team2;
        this.inGame = true;

        TeamRed.getPlayer1().getPlayer().teleport(stages.get(4).getPlayer1Teleport());
        TeamRed.getPlayer2().getPlayer().teleport(stages.get(4).getPlayer2Teleport());
        TeamRed.getPlayer3().getPlayer().teleport(stages.get(4).getPlayer3Teleport());

        TeamBlue.getPlayer1().getPlayer().teleport(stages.get(5).getPlayer1Teleport());
        TeamBlue.getPlayer2().getPlayer().teleport(stages.get(5).getPlayer2Teleport());
        TeamBlue.getPlayer3().getPlayer().teleport(stages.get(5).getPlayer3Teleport());

        for (Player player : getAllPlayers()) {
            ArenaListener.freezePlayers.add(player);
            Arena.playersPlaying.add(player);
            player.sendTitle(ChatColor.BOLD + "Gra zaczyna sie za 10sekund!", "",5,100, 5);
        }
        instance = this;
        final int[] i = {5};
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getAllPlayers()) {
                    ArenaListener.freezePlayers.add(player);
                    if (i[0] != 0) {
                        player.sendTitle(ChatColor.BOLD + Integer.toString(i[0]), "", 0, 20, 0);
                    } else {
                        player.sendTitle(ChatColor.BOLD + "START!", "", 0, 15, 5);
                        instance.runChecker();
                        this.cancel();
                    }
                }
                i[0]--;
            }
        }.runTaskTimer(ProBending.plugin, 100, 20);
    }

    public void stopGame() {
        if(!inGame) {
            inGame = false;
            for (Player player : getAllPlayers()) {
                Arena.playersPlaying.remove(player);
                this.getPBPlayer(player).removePlayer();
                player.setGameMode(GameMode.SURVIVAL);
                player.sendTitle(ChatColor.DARK_RED + "Koniec gry!", "", 10, 20, 10);
            }

        }
    }

    public void killPlayer(PBTeamPlayer player) {
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    public PBTeamPlayer getPBPlayer(Player player) {
        PBTeamPlayer pbplayer = null;
        if (this.getTeamRed().getPlayer1().getPlayer().equals(player)) {
            pbplayer = this.getTeamRed().getPlayer1();

        } else if (this.getTeamRed().getPlayer2().getPlayer().equals(player)) {
            pbplayer = this.getTeamRed().getPlayer2();

        } else if (this.getTeamRed().getPlayer3().getPlayer().equals(player)) {
            pbplayer = this.getTeamRed().getPlayer3();

        } else if (this.getTeamBlue().getPlayer1().getPlayer().equals(player)) {
            pbplayer = this.getTeamBlue().getPlayer1();

        } else if (this.getTeamBlue().getPlayer2().getPlayer().equals(player)) {
            pbplayer = this.getTeamBlue().getPlayer2();

        } else if (this.getTeamBlue().getPlayer3().getPlayer().equals(player)) {
            pbplayer = this.getTeamBlue().getPlayer3();
        }
        return pbplayer;
    }

    public int getID() {
        return ID;
    }

    public void runChecker() {
        ArrayList<PBTeamPlayer> teamPlayers = new ArrayList<>();
        teamPlayers.add(TeamBlue.getPlayer1());
        teamPlayers.add(TeamBlue.getPlayer2());
        teamPlayers.add(TeamBlue.getPlayer3());
        teamPlayers.add(TeamRed.getPlayer1());
        teamPlayers.add(TeamRed.getPlayer2());
        teamPlayers.add(TeamRed.getPlayer3());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!instance.inGame) {
                    this.cancel();
                }
                for (PBTeamPlayer player : teamPlayers) {
                    if (!player.isInGame() || player.getTeam().claimingStage) {
                        break;
                    }
                    StageEnum stage = StageEnum.getFromBiome(player.getPlayer().getLocation().getBlock().getBiome().toString()); // 3
                    StageEnum currentStage = StageEnum.getFromID(player.getStage()); // 4
                    TeamTag playerTag = player.getTeam().getTeamTag();
                    if (stage == StageEnum.WholeArena) {
                        // TODO: System karania
                        // TODO: TieBreaker
                        // TODO: Punktacja
                        // TODO: Komenda do dodawania graczy
                        // TODO: Koniec rundy
                        player.getPlayer().teleport(instance.stages.get(currentStage.getID()).getCenter());
                        break;

                    }

                        // RED
                    if (playerTag == TeamTag.RED) {

                        // STAGE BACKWARD
                        if (stage.getID() < currentStage.getID()) {
                            player.lowerStage();

                            // CLAIMING STAGE FOR BLUE
                            if (TeamRed.getPlayer1().getStage() <= player.getStage() && TeamRed.getPlayer2().getStage() <= player.getStage() && TeamRed.getPlayer3().getStage() <= player.getStage()) {
                                TeamBlue.getPlayer1().raiseStage();
                                TeamBlue.getPlayer2().raiseStage();
                                TeamBlue.getPlayer3().raiseStage();
                                for (Player p : instance.getAllPlayers()) {
                                    p.sendMessage(ChatColor.BOLD + "" + ChatColor.BLUE + "Druzyna niebieska zdobywa strefe!");
                                }
                                TeamRed.claimingStage = true;
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (TeamBlue.getPlayer1().getStage() == stage.getID() - 1) {
                                            TeamBlue.getPlayer1().getPlayer().teleport(instance.stages.get(TeamBlue.getPlayer1().getStage()).getPlayer1Teleport());
                                        }
                                        if (TeamBlue.getPlayer2().getStage() == stage.getID() - 1) {
                                            TeamBlue.getPlayer2().getPlayer().teleport(instance.stages.get(TeamBlue.getPlayer2().getStage()).getPlayer2Teleport());
                                        }
                                        if (TeamBlue.getPlayer3().getStage() == stage.getID() - 1) {
                                            TeamBlue.getPlayer3().getPlayer().teleport(instance.stages.get(TeamBlue.getPlayer3().getStage()).getPlayer3Teleport());
                                        }
                                        TeamRed.claimingStage = false;
                                    }
                                }.runTaskLater(ProBending.plugin, 60);

                                // KNOCKOUT
                            } else {
                                if (!player.isInGame()) {
                                    instance.killPlayer(player);
                                    for (Player p : instance.getAllPlayers()) {
                                        p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Player " + player.getPlayer().getDisplayName() + " dostal K-O!");
                                    }
                                }
                            }

                            // WARNING FORWARD
                        } else if (stage.getID() > currentStage.getID()) {
                            player.getPlayer().setVelocity(stages.get(player.getStage()).getCenter().subtract(player.getPlayer().getLocation()).toVector().normalize().multiply(3));
                            player.getPlayer().sendMessage(ChatColor.BOLD + "Jeszcze nie przejales tej strefy!");
                        }


                        // BLUE
                    } else {

                            // STAGE BACKWARD
                        if (stage.getID() > currentStage.getID()) {
                            player.lowerStage();

                            // CLAIMING STAGE FOR RED
                            if (TeamBlue.getPlayer1().getStage() >= player.getStage() && TeamBlue.getPlayer2().getStage() >= player.getStage() && TeamBlue.getPlayer3().getStage() >= player.getStage()) {
                                TeamRed.getPlayer1().raiseStage();
                                TeamRed.getPlayer2().raiseStage();
                                TeamRed.getPlayer3().raiseStage();
                                for (Player p : instance.getAllPlayers()) {
                                    p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Druzyna czerwona zdobywa strefe!");
                                }
                                TeamRed.claimingStage = true;
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (TeamRed.getPlayer1().getStage() == stage.getID() - 1) {
                                            TeamRed.getPlayer1().getPlayer().teleport(instance.stages.get(TeamRed.getPlayer1().getStage()).getPlayer1Teleport());
                                        }
                                        if (TeamRed.getPlayer2().getStage() == stage.getID() - 1) {
                                            TeamRed.getPlayer2().getPlayer().teleport(instance.stages.get(TeamRed.getPlayer2().getStage()).getPlayer2Teleport());
                                        }
                                        if (TeamRed.getPlayer3().getStage() == stage.getID() - 1) {
                                            TeamRed.getPlayer3().getPlayer().teleport(instance.stages.get(TeamRed.getPlayer3().getStage()).getPlayer3Teleport());
                                        }
                                        TeamRed.claimingStage = false;
                                    }
                                }.runTaskLater(ProBending.plugin, 60);

                                // KNOCKOUT
                            } else {
                                if (!player.isInGame()) {
                                    instance.killPlayer(player);
                                    for (Player p : instance.getAllPlayers()) {
                                        p.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Player " + player.getPlayer().getDisplayName() + " dostal K-O!");
                                    }
                                }
                            }

                            // WARNING FORWARD
                        } else if (stage.getID() < currentStage.getID()) {
                            player.getPlayer().setVelocity(stages.get(player.getStage()).getCenter().subtract(player.getPlayer().getLocation()).toVector().normalize().multiply(3));
                            player.getPlayer().sendMessage(ChatColor.BOLD + "Jeszcze nie przejales tej strefy!");
                        }
                    }
                }

            }
        }.runTaskTimer(ProBending.plugin, 0, 5);

    }

    public Location getCenter() {
        return center;
    }

    public Team getTeamBlue() {
        return TeamBlue;
    }

    public Team getTeamRed() {
        return TeamRed;
    }

    public ArrayList<Player> getAllPlayers() {
        ArrayList<Player> i = new ArrayList<>();
        i.addAll(TeamRed.getPlayers());
        i.addAll(TeamBlue.getPlayers());
        return i;
    }

    public Team getTeamByTag(TeamTag teamTag) {
        return teamTag == TeamTag.BLUE ? TeamBlue : TeamRed;
    }

    public ArrayList<PBTeamPlayer> getAllPBPlayers() {
        ArrayList<PBTeamPlayer> i = new ArrayList<>();
        i.addAll(TeamBlue.getPBPlayers());
        i.addAll(TeamRed.getPBPlayers());
        return i;
    }

    public void setUpArena() {
        int i = 0;

        for (StageEnum e : StageEnum.values()) {
            i++;
            this.stages.put(i, new Stage(e, this));
        }



    }

    public Stage getStage(int ID) {
        return this.stages.get(ID);
    }

    public boolean isInGame() {
        return this.inGame;
    }
}


