package me.domirusz24.pk.probending.probending.arena.commands;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.misc.ArenaGetter;
import me.domirusz24.pk.probending.probending.arena.misc.ArenaGetters;
import me.domirusz24.pk.probending.probending.arena.misc.ListHologram;
import me.domirusz24.pk.probending.probending.arena.stages.Stage;
import me.domirusz24.pk.probending.probending.arena.stages.StageEnum;
import me.domirusz24.pk.probending.probending.arena.stages.StageTeleports;
import me.domirusz24.pk.probending.probending.config.ConfigMethods;
import net.shadowxcraft.rollbackcore.Copy;
import net.shadowxcraft.rollbackcore.Paste;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Paths;
import java.util.ArrayList;

public class ArenaCreateCommand implements CommandExecutor {

    public static void copy(Player player, String arenaID, String TBstage, boolean addToConf) {
        WorldEditPlugin worldEditPlugin;
        worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            player.sendMessage(ProBending.errorPrefix + "Error: WorldEdit is null.");
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
                player.sendMessage(ProBending.successPrefix + "Set TBStage number " + TBstage + "!");
            } else {
                player.sendMessage(ProBending.errorPrefix + " Invalid Selection!");
            }

        }
    }

    public static boolean paste(Player player, String arenaID, String TBstage) {
        Location min = ConfigMethods.getTBLocation(arenaID, TBstage);
        if (min == null) {
            return false;
        }
        String name = Paths.get(ProBending.plugin.getDataFolder().getAbsolutePath(), "/TB/" + arenaID + "TB" + TBstage + ".dat").toString();
        new Paste(min, name, player, true, false, ProBending.successPrefix + " ").run();
        return true;
    }

    public static void setRollBack(Player player, String arenaID, boolean addToConf) {
        WorldEditPlugin worldEditPlugin;
        worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            player.sendMessage(ProBending.errorPrefix + "Error: WorldEdit is null.");
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
                player.sendMessage(ProBending.successPrefix + "Set the RollBack of the arena!");
            } else {
                player.sendMessage(ProBending.errorPrefix + " Invalid Selection!");
            }

        }
    }

    public static boolean getRollBack(Player player, String arenaID) {
        Location min = ConfigMethods.getLocation("nr" + arenaID + ".rollback");
        if (min == null) {
            return false;
        }
        String name = Paths.get(ProBending.plugin.getDataFolder().getAbsolutePath(), "/Arena/" + arenaID + "RollBack.dat").toString();
        new Paste(min, name, player, true, false, ProBending.successPrefix + " ").run();
        return true;
    }

    public static void setGetter(Player player, ArenaGetter getter, ArenaGetters getterType) {
        WorldEditPlugin worldEditPlugin;
        worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            player.sendMessage(ProBending.errorPrefix + "Error: WorldEdit is null.");
        } else {
            Selection sel = worldEditPlugin.getSelection(player);
            if (sel instanceof CuboidSelection) {
                Vector min = sel.getNativeMinimumPoint();
                Vector max = sel.getNativeMaximumPoint();
                getter.setGetter(getterType, new Location(player.getLocation().getWorld(), min.getX(), min.getY(), min.getZ()), new Location(player.getLocation().getWorld(), max.getX(), max.getY(), max.getZ()));
                player.sendMessage(ProBending.successPrefix + "Saved getter in " + getterType.getName() + "!");
            } else {
                player.sendMessage(ProBending.errorPrefix + " Invalid Selection!");
            }
        }
    }


    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            if (command.getLabel().equalsIgnoreCase("arena")) {
                final Player player = (Player) sender;

                if (!player.hasPermission("probending.arena.config")) {
                    player.sendMessage(ProBending.errorPrefix + "Nie masz wystarczajacych permisji.");
                    return true;
                }

                if (args.length == 0) {
                    player.sendMessage(ProBending.errorPrefix + "Please type in an arena ID or create / setspawn!");
                    return true;
                } else if (args[0].equalsIgnoreCase("help")) {
                    displayHelp(player, args);
                    return true;
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("create")) {
                        if (!player.hasPermission("probending.arena.create")) {
                            player.sendMessage(ProBending.errorPrefix + "Nie masz wystarczajacych permisji.");
                            return true;
                        }
                        new Arena(player.getLocation(), String.valueOf(Arena.Arenas.size() + 1));
                        player.sendMessage(ProBending.successPrefix + "Created an arena of the ID: " + Arena.Arenas.size());
                        player.sendMessage(ProBending.successPrefix + "At the location: " + player.getLocation());
                        return true;

                    } else if (args[0].equalsIgnoreCase("setspawn")) {
                        Arena.setSpawn(player.getLocation());
                        player.sendMessage(ProBending.successPrefix + "Set spawn!");
                        return true;

                    } else if (args[0].equalsIgnoreCase("getspawn")) {
                        if (Arena.spawn() == null) {
                            player.sendMessage(ProBending.errorPrefix + "Spawn is not set!");
                        } else {
                            player.teleport(Arena.spawn());
                            player.sendMessage(ProBending.successPrefix + "You have been teleported!");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("setArenaList")) {
                        ListHologram.setLocation(player.getLocation());
                        ListHologram.update();
                        player.sendMessage(ProBending.successPrefix + "Set location of ArenaList hologram!");
                        return true;
                    } else if (args[0].equalsIgnoreCase("getArenaList")) {
                        ListHologram.getInfo().forEach(player::sendMessage);
                        return true;
                    }
                    Arena arena;
                    arena = Arena.getArenaFromID(args[0]);
                    if (arena == null) {
                        player.sendMessage(ProBending.errorPrefix + "Please type in an Arena ID!");
                        return true;
                    } else {
                        arena.displayArenaInfo(player);
                    }

                }

                if (args.length == 2) {
                    player.sendMessage(ProBending.errorPrefix + "Please set or get!");
                    return true;
                } else if (args.length == 3 || args.length == 4) {
                    Arena arena;
                    arena = Arena.getArenaFromID(args[0]);
                    if (arena == null) {
                        player.sendMessage(ProBending.errorPrefix + "That arena ID doesn't exist!");
                        return true;
                    }

                    StageEnum stageEnum = StageEnum.getFromConfigName(args[2]);
                    ArenaGetters getter = ArenaGetters.getGetterFromName(args[2]);

                    //GET
                    if (args[1].equalsIgnoreCase("get")) {

                        //GET TBSTAGE
                        if (args[2].equalsIgnoreCase("TBStage")) {
                            if (args.length < 4) {
                                player.sendMessage(ProBending.errorPrefix + "Please enter a valid TBStage number or Stage!");
                                return true;
                            }
                            int num;
                            try {
                                num = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ProBending.errorPrefix + "Please enter a valid TBStage number or Stage!");
                                return true;
                            }
                            if (num < 0 || num > 10) {
                                player.sendMessage(ProBending.errorPrefix + "Please enter a valid TBStage number or Stage!");
                                return true;
                            }
                            paste(player, arena.getID(), String.valueOf(num));
                            player.sendMessage(ProBending.successPrefix + "Pasted in TBStage number " + num + "!");
                            return true;
                        }

                        //GET ROLLBACK
                        else if (args[2].equalsIgnoreCase("RollBack")) {
                            if (getRollBack(player, arena.getID())) {
                                player.sendMessage(ProBending.successPrefix + "Got Arena's rollback!");
                            } else {
                                player.sendMessage(ProBending.errorPrefix + "Could not get Arena's rollback.");
                            }
                            return true;
                        } else if (args[2].equalsIgnoreCase("center")) {
                            if (arena.getCenter() != null) {
                                player.teleport(arena.getCenter());
                                player.sendMessage(ProBending.successPrefix + "Teleported to center in arena " + arena.getID() + "!");
                            } else {
                                player.sendMessage(ProBending.errorPrefix + "Center does not exist!");
                            }

                        } else if (args[2].equalsIgnoreCase("hologram")) {
                            if (arena.getHologramManager() == null) {
                                player.sendMessage(ProBending.errorPrefix + "Hologram nie zostal utworzony!");
                            } else {
                                arena.getHologramManager().getInfo().forEach(player::sendMessage);
                            }
                        }
                        //GET STAGE
                        else if (stageEnum != null) {
                            final Stage stage = arena.getStage(stageEnum.getID());
                            if (stage == null) {
                                player.sendMessage(ProBending.errorPrefix + "Arena didnt create that stage yet!");
                                return true;
                            }
                            if (args.length == 4) {
                                StageTeleports teleport = StageTeleports.getStageFromName(args[3]);
                                if (teleport == null) {
                                    player.sendMessage(ProBending.errorPrefix + "Invalid teleport type! (p1, p2, p3 or center)");
                                    return true;
                                }
                                Location loc = stage.getTeleport(teleport);
                                if (loc != null) {
                                    player.teleport(loc);
                                    player.sendMessage(ProBending.successPrefix + "Teleported you to " + teleport.getName() + " in " + stageEnum.toString() + "!");
                                } else {
                                    player.sendMessage(ProBending.errorPrefix + "Teleport " + teleport.getName() + " in " + stageEnum.toString() + " doesn't exist!");
                                }
                            } else {
                                player.sendMessage(ProBending.errorPrefix + "Invalid teleport type! (p1, p2, p3 or center)");
                            }
                            return true;

                            //GET GETTER
                        } else if (getter != null) {
                            ArrayList<String> info = arena.getGetter().getInfo(getter);
                            if (info != null) {
                                info.forEach(player::sendMessage);
                            } else {
                                player.sendMessage(ProBending.errorPrefix + "Getter for " + getter.getName() + " is not set!");
                            }
                            return true;

                        } else {
                            player.sendMessage(ProBending.errorPrefix + "Please enter a valid getter or stage value");
                            return true;
                        }

                        //SET
                    } else if (args[1].equalsIgnoreCase("set")) {

                        //SET TBSTAGE
                        if (args[2].equalsIgnoreCase("TBStage")) {
                            if (args.length < 4) {
                                player.sendMessage(ProBending.errorPrefix + "Please enter a valid TBStage number or Stage!");
                                return true;
                            }
                            int num;
                            try {
                                num = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                player.sendMessage(ProBending.errorPrefix + "Please enter a valid TBStage number!");
                                return true;
                            }
                            if (num < 0 || num > 10) {
                                player.sendMessage(ProBending.errorPrefix + "Please enter a valid TBStage number!");
                                return true;
                            }
                            copy(player, arena.getID(), String.valueOf(num), true);
                            return true;
                        }

                        //SET ROLLBACK
                        else if (args[2].equalsIgnoreCase("RollBack")) {
                            setRollBack(player, arena.getID(), true);
                            return true;
                        } else if (args[2].equalsIgnoreCase("center")) {
                            arena.setCenter(player.getLocation());
                            player.sendMessage(ProBending.successPrefix + "Set center in arena " + arena.getID() + "!");

                        } else if (args[2].equalsIgnoreCase("hologram")) {
                            arena.getHologramManager().setLocation(player.getLocation());
                            player.sendMessage(ProBending.successPrefix + "Set hologram in arena " + arena.getID() + "!");
                        }
                        //SET STAGE
                        else if (stageEnum != null) {
                            final Stage stage = arena.getStage(stageEnum.getID());
                            if (stage == null) {
                                player.sendMessage(ProBending.errorPrefix + "Arena didnt create that stage yet!");
                                return true;
                            }
                            if (args.length == 4) {
                                StageTeleports teleport = StageTeleports.getStageFromName(args[3]);
                                if (teleport == null) {
                                    player.sendMessage(ProBending.errorPrefix + "Invalid teleport type! (p1, p2, p3 or center)");
                                    return true;
                                }
                                    stage.setTeleport(teleport, player.getLocation());
                                player.sendMessage(ProBending.successPrefix + "Set " + teleport.getName() + " teleport in " + stageEnum.toString() + "!");

                            } else {
                                player.sendMessage(ProBending.errorPrefix + "Invalid teleport type! (p1, p2, p3 or center)");
                                return true;
                            }


                            //SET GETTER
                        } else if (getter != null) {
                            ArenaCreateCommand.setGetter(player, arena.getGetter(), getter);
                            return true;
                        } else {
                            player.sendMessage(ProBending.errorPrefix + "Please enter a valid getter or stage value");
                        }
                    }
                }
                if (args.length > 4) {
                    player.sendMessage(ProBending.errorPrefix + "Too much arguments!");
                    return true;
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
            info.add(c + "/arena getspawn - Teleportuje do ogolnego spawnu");
            info.add(c + "/arena setarenalist - Tworzy hologram z listami aren.");
            info.add(c + "/arena getarenalist - Podaje informacje na temat listy z arenami.");
            info.add(c + "/arena create - Tworzy nowa arene");
            info.add("");
            info.add(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "(Strona 1 z 3)");

        } else {
            if (args[1].equalsIgnoreCase("2")) {
                info.add(c + "/arena (ID ARENY) set rollback - Tworzy RollBack areny");
                info.add(c + "/arena (ID ARENY) get rollback - Manualnie rollbackuje arene");
                info.add(c + "/arena (ID ARENY) set tbstage (ETAP ANIMACJI) - Tworzy etap animacji TieBreakera z selekcja w WorldEdit. (0 = STAN POCZATKOWY)");
                info.add(c + "/arena (ID ARENY) get tbstage (ETAP ANIMACJI) - Manulanie pokazuje animacje TieBreakera");
                info.add(c + "/arena (ID ARENY) set (STREFA) (TYP TELEPORTA) - Ustawia teleport dla podanego typu na podanej strefie.");
                info.add("");
                info.add(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "(Strona 2 z 3)");
            } else if (args[1].equalsIgnoreCase("3")) {
                info.add(c + "/arena (ID ARENY) get (STREFA) (TYP TELEPORTA) - Teleportuje na podany typ teleporta na podanej strefie.");
                info.add(c + "/arena (ID ARENY) set (GETTER) - Ustawia gettera dla podanej arenie.");
                info.add(c + "/arena (ID ARENY) get (GETTER) - Podaje informacje na temat gettera w podanej arenie.");
                info.add(c + "/arena (ID ARENY) set hologram - Tworzy hologram z informacjami areny.");
                info.add(c + "/arena (ID ARENY) get hologram - Podaje informacje na temat hologramu w tej arenie.");
                info.add("");
                info.add(ChatColor.BOLD + "" + ChatColor.LIGHT_PURPLE + "(Strona 3 z 3)");
            } else {
                player.sendMessage(ProBending.errorPrefix +  "Nie poprawna strona!");
                return;
            }
        }
        info.add(ChatColor.BOLD + "" + ChatColor.BLUE + "~~~~~~~~~~~~~");
        info.forEach(player::sendMessage);
    }
}
