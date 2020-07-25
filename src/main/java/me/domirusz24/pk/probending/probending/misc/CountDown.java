package me.domirusz24.pk.probending.probending.misc;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.arena.Arena;
import me.domirusz24.pk.probending.probending.arena.misc.NumberChatColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class CountDown {
    private final boolean title;

    private final int number;

    private final Runnable runnable;

    private final String prefix;

    private final Arena arena;

    private final int delay;

    public CountDown(int amount, String prefix, Runnable afterCountDown, int delay, boolean title, Arena arena) {
        this.number = amount;
        this.prefix = prefix;
        this.runnable = afterCountDown;
        this.title = title;
        this.arena = arena;
        this.delay = delay;
    }
    public void run(ArrayList<Player> players) {
        new BukkitRunnable() {
            int e = number;
            final int r = arena.getRoundNumber();
            public void run() {
                if (!arena.isInGame() || r != arena.getRoundNumber()) {
                    cancel();
                    return;
                }
                if (e != 0) {
                    if (!title) {
                        players.forEach(player -> player.sendTitle("", ChatColor.BOLD + "" + NumberChatColor.getFromValue(e).getChatColor() + prefix + e, 0, 20, 0));
                    } else {
                        players.forEach(player -> player.sendTitle(ChatColor.BOLD + "" + NumberChatColor.getFromValue(e).getChatColor() + prefix + e, "", 0, 20, 0));
                    }
                } else {
                    if (runnable != null) {
                        runnable.run();
                    }
                    this.cancel();
                    return;
                }
                e--;
            }
        }.runTaskTimer(ProBending.plugin, delay, 20L);
    }
}
