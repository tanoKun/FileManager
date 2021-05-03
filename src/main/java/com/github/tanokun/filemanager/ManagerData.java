package com.github.tanokun.filemanager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManagerData {
    private final Plugin plugin;

    private boolean showingJarFile;
    private List<String> noEditFiles;
    private List<String> managers;

    public ManagerData(Plugin plugin) {
        this.plugin = plugin;
        this.load();
    }

    public void load() {
        FileConfiguration config = plugin.getConfig();

        showingJarFile = config.getBoolean("options.showingJarFile", true);

        noEditFiles = (List<String>) config.getList("options.noEditFiles", Arrays.asList("FileManager/config.yml"));

        managers = (List<String>) config.getList("managers", new ArrayList<>());
    }

    public boolean isShowingJarFile() {return showingJarFile;}
    public List<String> getNoEditFiles() {return noEditFiles;}
    public List<String> getManagers() {return managers;}
}
