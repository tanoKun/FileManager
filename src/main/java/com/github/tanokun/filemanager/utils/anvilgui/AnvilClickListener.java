package com.github.tanokun.filemanager.utils.anvilgui;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!AnvilGUI.activeGUIs.containsKey(event.getWhoClicked().getUniqueId())) return;
        event.setCancelled(true);
        if (!(event.getRawSlot() < 3)) return;
        ItemStack item = event.getCurrentItem();
        AnvilGUI anvilGUI = AnvilGUI.activeGUIs.get(event.getWhoClicked().getUniqueId());
        if (event.getRawSlot() == 2 && item != null && item.getType() != Material.AIR) {
            event.getWhoClicked().closeInventory();
            anvilGUI.getComplete().accept(item.getItemMeta().getDisplayName());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if (!AnvilGUI.activeGUIs.containsKey(event.getPlayer().getUniqueId())) return;
        AnvilGUI.activeGUIs.remove(event.getPlayer().getUniqueId());
    }
}
