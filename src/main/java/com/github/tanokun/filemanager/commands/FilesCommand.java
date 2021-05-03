package com.github.tanokun.filemanager.commands;

import com.github.tanokun.filemanager.guis.DirectoryInventory;
import com.github.tanokun.filemanager.utils.FileUtils;
import com.github.tanokun.filemanager.utils.ItemUtils;
import com.github.tanokun.filemanager.utils.anvil.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Paths;
import java.util.List;

public class FilesCommand implements CommandExecutor {
    private final List<String> managers;

    public FilesCommand(List<String> playerNames){
        this.managers = playerNames;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!managers.contains(sender.getName())) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }
        new DirectoryInventory(Paths.get(FileUtils.getDefaultPath("")), null).getDirectory().open((Player) sender);
        return true;
    }
}
