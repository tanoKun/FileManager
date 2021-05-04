package com.github.tanokun.filemanager.guis;

import com.github.tanokun.filemanager.FileManager;
import com.github.tanokun.filemanager.utils.FileUtils;
import com.github.tanokun.filemanager.utils.ItemUtils;
import com.github.tanokun.filemanager.utils.smart_inv.inv.ClickableItem;
import com.github.tanokun.filemanager.utils.smart_inv.inv.SmartInventory;
import com.github.tanokun.filemanager.utils.smart_inv.inv.contents.InventoryContents;
import com.github.tanokun.filemanager.utils.smart_inv.inv.contents.InventoryProvider;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class DeleteFileCheckInventory implements InventoryProvider {
    private final DirectoryInventory previousFolder;
    private final int page;
    private final File file;
    public DeleteFileCheckInventory(File file, DirectoryInventory previousFolder, int page) {
        this.previousFolder = previousFolder;
        this.file = file;
        this.page = page;
    }

    public SmartInventory getDirectory(){
        return SmartInventory.builder()
                .id("DeleteFileCheck")
                .provider(this)
                .size(1, 9)
                .title("§b§l[FileManager] §c最終確認")
                .closeable(true)
                .update(false)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        String path = FileUtils.changeSeparator_Slash(StringUtils.replace(file.getAbsolutePath(),
                FileUtils.getBasePath(), "", 1));

        contents.set(0, 2, ClickableItem.of(ItemUtils.createItem(Material.GREEN_WOOL, "§a削除します", new String[]{"§f・Path -> " + path}, 1, true), e ->{
                player.closeInventory();
            new BukkitRunnable(){
                public void run() {
                    player.sendMessage(FileManager.PX + "§bファイルを削除中...");
                    file.delete();
                    player.sendMessage(FileManager.PX + "§aファイルを「" + path + "」削除しました");
                    new BukkitRunnable(){
                        public void run() {
                            new DirectoryInventory(previousFolder.path, previousFolder.previousFolder).getDirectory().open(player);
                            FileManager.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                        }
                    }.runTask(FileManager.getPlugin());
                }}.runTaskAsynchronously(FileManager.getPlugin());
        }));

        contents.set(0, 6, ClickableItem.of(ItemUtils.createItem(Material.RED_WOOL, "§c削除しません", new String[]{"§f・Path -> " + path}, 1, true), e -> {
            contents.removeProperty("delete_file");
            previousFolder.getDirectory().open(player, page);
        }));
    }
}
