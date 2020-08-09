package me.domirusz24.pk.probending.probending.arena.kit;

import me.domirusz24.pk.probending.probending.ProBending;
import me.domirusz24.pk.probending.probending.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PlayerKit {
    private static final HashMap<ItemStack, PlayerKit> availableKits = new HashMap<>();

    public static void addDefault() {
        FileConfiguration f = ProBending.plugin.getConfig();
        ProBending.plugin.reloadConfig();
        if (!f.isSet("Kits.WaterBottle.DisplayName")) {
            new PlayerKit("WaterBottle", "&4Kit maga wody", "&6Daje butelki z woda!", Material.POTION, (byte) 0, Collections.singletonList("msg %player% u gay lmao")).saveToConfig();
        }
        ProBending.plugin.saveConfig();
    }

    public static void readKits() {
        addDefault();
        Set<String> e = ProBending.plugin.getConfig().getConfigurationSection("Kits").getKeys(false);
        FileConfiguration c = ProBending.plugin.getConfig();
        for (String t : e) {
            String path = "Kits." + t + ".";
            String displayName = c.getString(path + "DisplayName");
            String description = c.getString(path + "Description");
            byte data = (byte) c.getInt(path + "ItemData");
            Material material = Material.getMaterial(c.getString(path + "Material"));
            List<String> commands = c.getStringList(path + "Commands");
            new PlayerKit(t, displayName, description, material, data, commands).saveToList();
        }
    }
    private final String name;
    private final String displayName;
    private final String description;
    private final byte data;
    private final Material material;
    private final List<String> commands;

    public PlayerKit(String name, String displayName, String description, Material material, byte data, List<String> commands) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.data = data;
        this.commands = commands;
    }

    public static void getKits(Player player) {
        for (PlayerKit kit : availableKits.values()) {
            if (player.hasPermission(kit.getPermission())) {
                kit.runCommands(player);
            }
        }
    }

    public static HashMap<ItemStack, PlayerKit> getAvailableKits() {
        return availableKits;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public byte getData() {
        return data;
    }

    public ItemStack getItemStack() {
        ItemStack i = new ItemStack(material, 1, data);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', getDisplayName()));
        m.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', getDescription())));
        i.setItemMeta(m);
        return i;
    }

    public String getPermission() {
        return "ProBending.Kits." + getName();
    }

    public List<String> getCommands() {
        return commands;
    }

    public void togglePlayer(Player player, boolean value) {
        ConfigManager.getDataConfig().getConfig().set(player.getName() + ".Kits." + getName() + ".Toggled", value ? 1 : 0);
    }

    public boolean isEnabled(Player player) {
        return ConfigManager.getDataConfig().getConfig().getInt(player.getName() + ".Kits." + getName() + ".Toggled") == 1;
    }

    public void runCommands(Player player) {
        getCommands().forEach(e -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), e.replace("%player%", player.getName())));
    }

    public void saveToConfig() {
        FileConfiguration f = ProBending.plugin.getConfig();
        String path = "Kits." + getName() + ".";
        f.set(path + "DisplayName", getDisplayName());
        f.set(path + "Description", getDescription());
        f.set(path + "Material", getMaterial().name());
        f.set(path + "ItemData", (int) getData());
        f.set(path + "Commands", getCommands());
    }

    public void saveToList() {
        availableKits.put(getItemStack(), this);
    }
}
