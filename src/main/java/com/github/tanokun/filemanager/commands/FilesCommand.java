package com.github.tanokun.filemanager.commands;

import com.github.tanokun.filemanager.FileManager;
import com.github.tanokun.filemanager.guis.DirectoryInventory;
import com.github.tanokun.filemanager.utils.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Paths;

public class FilesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!FileManager.getManagerData().getManagers().contains(sender.getName())) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        new DirectoryInventory(Paths.get(FileUtils.getDefaultPath("")), null).getDirectory().open((Player) sender);
        return true;
    }

}
