package com.github.tanokun.filemanager;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.tanokun.filemanager.commands.FilesCommand;
import com.github.tanokun.filemanager.utils.Glowing;
import com.github.tanokun.filemanager.utils.anvil.AnvilListener;
import com.github.tanokun.filemanager.utils.smart_inv.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class FileManager extends JavaPlugin {

    public final static String PX = "§b[FiM] ";

    private FileConfiguration config;

    private static Plugin plugin;

    private static InventoryManager inventoryManager;


    public void onEnable() {
        saveDefaultConfig(); config = getConfig();
        plugin = this;

        inventoryManager = new InventoryManager(this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new AnvilListener());

        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glowing glow = new Glowing();
            Enchantment.registerEnchantment(glow);
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getPluginCommand("files").setExecutor(new FilesCommand(getHasPermissionPlayers()));
    }

    private List<String> getHasPermissionPlayers(){
        List<String> players = new ArrayList<>();
        players = (List<String>) config.getList("managers", players);
        return players;
    }

    public static void playSound(Player player, Sound sound, int volume, double v2){
        player.playSound(player.getLocation(), sound, volume, (float) v2);
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static InventoryManager getInventoryManager() {
        return inventoryManager;
    }

}
