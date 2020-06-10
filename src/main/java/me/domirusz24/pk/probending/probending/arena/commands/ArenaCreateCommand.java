package me.domirusz24.pk.probending.probending.arena.commands;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.domirusz24.pk.probending.probending.ConfigMethods;
import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.Stage;
import me.domirusz24.pk.probending.probending.arena.StageEnum;
import net.shadowxcraft.rollbackcore.Copy;
import net.shadowxcraft.rollbackcore.Paste;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ArenaCreateCommand implements CommandExecutor {

    public static void copy(Player player, String arenaID, String TBstage, boolean addToConf) {
        WorldEditPlugin worldEditPlugin;
        worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            player.sendMessage(ProBending.prefix + "Error: WorldEdit is null.");
        } else {
            Selection sel = worldEditPlugin.getSelection(player);
            if (sel instanceof CuboidSelection) {
                Vector min = sel.getNativeMinimumPoint();
                Vector max = sel.getNativeMaximumPoint();
                String name = Paths.get(ProBending.plugin.getDataFolder().getAbsolutePath(), "/TB/" + arenaID + "TB" + TBstage + ".dat").toString();
                if (addToConf) {
                    ConfigMethods.setTBLocation(arenaID, TBstage, min.getBlockX(), min.getBlockY(), min.getBlockZ(), player.getWorld());
                }

                (new Copy(min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ(), player.getWorld(), name, player)).run();
                player.sendMessage(ProBending.prefix + " Saving the arena... " + min.getBlockX() + " " + min.getBlockY() + " " + min.getBlockZ());
            } else {
                player.sendMessage(ProBending.prefix + (ChatColor.DARK_RED + " Invalid Selection!"));
            }

        }
    }

    public static boolean paste(Player player, String arenaID, String TBstage) {
        Location min = ConfigMethods.getTBLocation(arenaID, TBstage);
        if (min == null) {
            return false;
        }
        String name = Paths.get(ProBending.plugin.getDataFolder().getAbsolutePath(), "/TB/" + arenaID + "TB" + TBstage + ".dat").toString();
        new Paste(min, name, player, true, false, ProBending.prefix + " ").run();
        return true;
    }

    public static void setRollBack(Player player, String arenaID, boolean addToConf) throws IOException {
        WorldEditPlugin worldEditPlugin;
        worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            player.sendMessage(ProBending.prefix + "Error: WorldEdit is null.");
        } else {
            Selection sel = worldEditPlugin.getSelection(player);
            if (sel instanceof CuboidSelection) {
                Vector min = sel.getNativeMinimumPoint();
                Vector max = sel.getNativeMaximumPoint();
                String name = Paths.get(ProBending.plugin.getDataFolder().getAbsolutePath(), "/Arena/" + arenaID + "RollBack.dat").toString();
                if (addToConf) {
                    ConfigMethods.saveLocation("nr" + arenaID + ".rollback", new Location(sel.getWorld(), min.getBlockX(), min.getBlockY(), min.getBlockZ(), 0, 0));
                }

                (new Copy(min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ(), player.getWorld(), name, player)).run();
                player.sendMessage(ProBending.prefix + " Saving the arena... " + min.getBlockX() + " " + min.getBlockY() + " " + min.getBlockZ());
            } else {
                player.sendMessage(ProBending.prefix + (ChatColor.DARK_RED + " Invalid Selection!"));
            }

        }
    }

    public static boolean getRollBack(Player player, String arenaID) {
        Location min = ConfigMethods.getLocation("nr" + arenaID + ".rollback");
        if (min == null) {
            return false;
        }
        String name = Paths.get(ProBending.plugin.getDataFolder().getAbsolutePath(), "/Arena/" + arenaID + "RollBack.dat").toString();
        new Paste(min, name, player, true, false, ProBending.prefix + " ").run();
        return true;
    }


    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            if (command.getLabel().equalsIgnoreCase("arena")) {
                final Player player = (Player) sender;

                if (!player.hasPermission("probending.arena.config")) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("help")) {
                    displayHelp(player, args);
                    return true;
                }

                if (args.length == 0) {
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Please type in an arena ID or create / setspawn!");
                    return true;
                }

                if (args[0].equalsIgnoreCase("create")) {
                    if (!player.hasPermission("probending.arena.create")) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie masz wystarczajacych permisji.");
                        return true;
                    }

                    try {
                        new Arena(player.getLocation(), String.valueOf(Arena.Arenas.size() + 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Created an arena of the ID: " + Arena.Arenas.size());
                    return true;

                } else {

                    if (args[0].equalsIgnoreCase("setspawn")) {
                        try {
                            Arena.setSpawn(player.getLocation());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Set spawn!");
                        return true;

                    } else if (args[0].equalsIgnoreCase("teleportspawn")) {
                        if (Arena.spawn() == null) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Spawn is not set!");
                        } else {
                            player.teleport(Arena.spawn());
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "You have been teleported!");
                        }
                        return true;
                    }

                    if (args.length == 1) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Please type in an Arena ID!");
                        return true;
                    }

                    if (args.length == 2) {
                        Arena arena;
                        try {
                            arena = Arena.getArenaFromID(args[0]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "ID must be a number!");
                            return true;
                        }
                        if (arena == null) {
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "That arena ID doesn't exist!");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("setRollBack")) {
                            try {
                                setRollBack(player, arena.getID(), true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;

                        } else if (args[1].equalsIgnoreCase("getRollBack")) {
                            getRollBack(player, arena.getID());
                            return true;
                        }

                        System.out.println("Wrong argument!");
                        return true;
                    }
                    if (args.length > 4) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Too much arguments!");
                        return true;
                    }
                    Arena arena;
                    try {
                        arena = Arena.getArenaFromID(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "ID must be a number!");
                        return true;
                    }
                    if (arena == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "That arena ID doesn't exist!");
                        return true;
                    }
                    final StageEnum stageEnum = StageEnum.getFromConfigName(args[1]);
                    String s = args[2].toLowerCase();
                    if (stageEnum == null) {
                        if (args[1].equalsIgnoreCase("setTBStage")) {
                            int num;
                            try {
                                num = Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Please enter a valid TBStage number or Stage!");
                                return true;
                            }
                            if (num < 0 || num > 10) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Please enter a valid TBStage number or Stage!");
                                return true;
                            }
                            copy(player, arena.getID(), String.valueOf(num), true);
                        } else if (args[1].equalsIgnoreCase("getTBStage")) {
                            int num;
                            try {
                                num = Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Please enter a valid TBStage number or Stage!");
                                return true;
                            }
                            if (num < 0 || num > 10) {
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Please enter a valid TBStage number or Stage!");
                                return true;
                            }
                            paste(player, arena.getID(), String.valueOf(num));
                        } else if (args[1].equalsIgnoreCase("setRollBack")) {
                            try {
                                setRollBack(player, arena.getID(), true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        } else if (args[1].equalsIgnoreCase("getRollBack")) {
                            getRollBack(player, arena.getID());
                            return true;
                        }
                        return true;
                    }
                    final Stage stage = arena.getStage(stageEnum.getID());
                    if (stage == null) {
                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Arena didnt create that stage yet!");
                        return true;
                    }
                    if (args.length == 3) {
                        switch (s) {
                            case "player1": {
                                try {
                                    stage.setPlayer1Teleport(player.getLocation());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Set Player 1 in " + stage.getStage().toString() + "!");
                                return true;
                            }
                            case "player2": {
                                try {
                                    stage.setPlayer2Teleport(player.getLocation());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Set Player 2 in " + stage.getStage().toString() + "!");
                                return true;
                            }
                            case "player3": {
                                try {
                                    stage.setPlayer3Teleport(player.getLocation());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Set Playera 3 in " + stage.getStage().toString() + "!");
                                return true;
                            }
                            case "center": {
                                try {
                                    stage.setCenter(player.getLocation());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Set center in " + stage.getStage().toString() + "!");
                                return true;
                            }
                        }
                    } else {
                        if (args[3].equalsIgnoreCase("teleport")) {
                            switch (s) {
                                case "player1": {
                                    if (stage.getPlayer1Teleport() == null) {
                                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Teleport player1 in " + stage.getStage().toString() + " doesnt exist!");
                                    }
                                    player.teleport(stage.getPlayer1Teleport());
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Teleported to player1 in " + stage.getStage().toString() + "!");
                                    return true;
                                }
                                case "player2": {
                                    if (stage.getPlayer2Teleport() == null) {
                                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Teleport player2 in " + stage.getStage().toString() + " doesnt exist!");
                                    }
                                    player.teleport(stage.getPlayer2Teleport());
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Teleported to player2 in " + stage.getStage().toString() + "!");
                                    return true;
                                }
                                case "player3": {
                                    if (stage.getPlayer3Teleport() == null) {
                                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Teleport player3 in " + stage.getStage().toString() + " doesnt exist!");
                                    }
                                    player.teleport(stage.getPlayer3Teleport());
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Teleported to player3 in " + stage.getStage().toString() + "!");
                                    return true;
                                }
                                case "center": {
                                    if (stage.getCenter() == null) {
                                        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Teleport center in " + stage.getStage().toString() + "  doesnt exist!");
                                    }
                                    player.teleport(stage.getCenter());
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Teleported to center in " + stage.getStage().toString() + "!");
                                    return true;
                                }
                                default: {
                                    player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Invalid teleport type.");
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("You must be a player to use this command!");
        }
        return true;
    }

    public void displayHelp(Player player, String[] args) {
        ArrayList<String> info = new ArrayList<>();
        ChatColor c = ChatColor.GREEN;
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
        if (args.length == 1 || args[1].equalsIgnoreCase("1")) {
            info.add(c + "/arena setspawn - Ustawia ogolny spawn");
            info.add(c + "/arena teleportspawn - Teleportuje do ogolnego spawnu");
            info.add(c + "/arena create - Tworzy nowa arene");
            info.add(c + "/arena (ID ARENY) setrollback - Tworzy RollBack areny");
            info.add(c + "/arena (ID ARENY) getrollback - Manualnie rollbackuje arene");
            info.add("");
            info.add(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "(Strona 1 z 2)");

        } else {
            if (args[1].equalsIgnoreCase("2")) {
                info.add(c + "/arena (ID ARENY) settbstage (ETAP ANIMACJI) - Tworzy etap animacji TieBreakera z selekcja w WorldEdit. (0 = STAN POCZATKOWY)");
                info.add(c + "/arena (ID ARENY) gettbstage (ETAP ANIMACJI) - Manulanie pokazuje animacje TieBreakera");
                info.add(c + "/arena (ID ARENY) (STREFA) (TYP TELEPORTA) - Ustawia teleport dla podanego typu na podanej strefie.");
                info.add(c + "/arena (ID ARENY) (STREFA) (TYP TELEPORTA) teleport - Teleportuje na podany typ teleporta na podanej strefie.");
                info.add("");
                info.add(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "(Strona 2 z 2)");
            } else {
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Nie poprawna strona!");
                return;
            }
        }
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
        info.forEach(player::sendMessage);
    }
}
