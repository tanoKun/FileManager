package com.github.tanokun.filemanager.utils;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemUtils implements Listener {
    public static ItemStack createItem(Material material, String name, int count, boolean glowing){
        ItemStack is = new ItemStack(material);
        is.setAmount(count);

        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        if (glowing == true) {im.addEnchant(new Glowing(), 1, true);}
        is.setItemMeta(im);
        return is;
    }
    public static ItemStack createItem(Material material, String name, String[] lore, int count, boolean glowing){
        ItemStack is = new ItemStack(material);
        is.setAmount(count);

        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        if (glowing == true) {im.addEnchant(new Glowing(), 1, true);}
        is.setItemMeta(im);
        return is;
    }
}
