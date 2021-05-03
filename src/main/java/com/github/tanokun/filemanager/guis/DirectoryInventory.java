package com.github.tanokun.filemanager.guis;

import com.github.tanokun.filemanager.FileManager;
import com.github.tanokun.filemanager.utils.FileUtils;
import com.github.tanokun.filemanager.utils.ItemUtils;
import com.github.tanokun.filemanager.utils.anvil.AnvilGUI;
import com.github.tanokun.filemanager.utils.smart_inv.inv.ClickableItem;
import com.github.tanokun.filemanager.utils.smart_inv.inv.SmartInventory;
import com.github.tanokun.filemanager.utils.smart_inv.inv.contents.InventoryContents;
import com.github.tanokun.filemanager.utils.smart_inv.inv.contents.InventoryProvider;
import com.github.tanokun.filemanager.utils.smart_inv.inv.contents.Pagination;
import com.github.tanokun.filemanager.utils.smart_inv.inv.contents.SlotIterator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DirectoryInventory implements InventoryProvider {
    public List<File> files = new ArrayList<>();
    public final Path path;
    public final DirectoryInventory previousFolder;

    public DirectoryInventory(Path path, DirectoryInventory previousFolder) {
        if (!FileManager.getManagerData().isShowingJarFile()) {
            Arrays.stream(path.toFile().listFiles())
                    .filter(file -> !file.getName().endsWith(".jar"))
                    .forEach(file -> files.add(file));
        } else {
            this.files = Arrays.asList(path.toFile().listFiles());
        }
        this.path = path;
        this.previousFolder = previousFolder == null ? this : previousFolder;
    }

    public SmartInventory getDirectory(){
        File file = path.toFile();

        String showPath = StringUtils.replace(file.getPath(), FileUtils.getBasePath(), "", 1);

        return SmartInventory.builder()
                .id("Directory")
                .provider(this)
                .size(6, 9)
                .title("§b§l[FileManager] §8" + FileUtils.changeSeparator_Slash(showPath))
                .closeable(true)
                .update(false)
                .build();
    }



    @Override
    public void init(Player player, InventoryContents contents) {

        Pagination pagination = contents.pagination();
        ClickableItem[] items = new ClickableItem[files.size()];
        contents.fillRect(4, 0, 4, 8,
                ClickableItem.empty(ItemUtils.createItem(Material.STAINED_GLASS_PANE, "  ", 1, false, DyeColor.BLUE)));

        contents.set(5, 3, ClickableItem.of(ItemUtils.createItem(Material.ANVIL,
                "§aファイル作成", 1, true), no ->
                new AnvilGUI("§aファイル作成", ItemUtils.createItem(Material.PAPER, "ファイル名の入力", 1, true), e -> {
                    String path = e.getItem().getItemMeta().getDisplayName();
                    if (StringUtils.containsAny(path, new char[]{'\\', '/', ':', '*', '?', '\"', '<', '|'})){
                        player.sendMessage(FileManager.PX + "§c使用できない文字が含まれています");
                        FileManager.playSound(player, Sound.BLOCK_NOTE_BASS, 10, 1);
                    }

                    player.closeInventory();

                    if (FilenameUtils.getExtension(path).equals("")){
                        File file = new File(this.path.toFile().getPath() + File.separator + path);
                        player.sendMessage(FileManager.PX + "§bフォルダを作成中...");
                        file.mkdirs();
                        player.sendMessage(FileManager.PX + "§bフォルダ「" + file.getName() + "」を作成しました");
                        new DirectoryInventory(this.path, previousFolder).getDirectory().open(player);
                        FileManager.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);

                    } else {
                        File file = new File(this.path.toFile().getPath() + File.separator + path);
                        player.sendMessage(FileManager.PX + "§bファイルを作成中...");
                        try {file.createNewFile();} catch (IOException ioException) {ioException.printStackTrace();}
                        player.sendMessage(FileManager.PX + "§aファイル「" + file.getName() + "」を作成しました");
                        new DirectoryInventory(this.path, previousFolder).getDirectory().open(player);
                        FileManager.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                    }

                }).open(player)));

        contents.set(5, 4, ClickableItem.of(ItemUtils.createItem(Material.FEATHER,
                "§bファイル名変更", new String[]{"§fクリックしたあと、", "§f変更したいファイルを選択します"}, 1, true), no -> {
            player.sendMessage(FileManager.PX + "§bファイルもしくはフォルダーを選択してください");
            contents.setProperty("edit_name", true);
            contents.inventory().open(player, contents.pagination().getPage());
        }));

        contents.set(5, 5, ClickableItem.of(ItemUtils.createItem(Material.REDSTONE_BLOCK,
                "§cファイル削除", new String[]{"§fクリックしたあと、", "§f削除したいファイルを選択します"}, 1, true), no -> {
            player.sendMessage(FileManager.PX + "§bファイルもしくはフォルダーを選択してください");
            contents.setProperty("delete_file", true);
            contents.inventory().open(player, contents.pagination().getPage());
        }));

        for (int i = 0; i < items.length; i++) {
            File file = files.get(i);
            switch (FilenameUtils.getExtension(file.getName())) {
                case "yml": case "yaml":
                    items[i] = paperContents(player, contents, file, Material.PAPER,true);
                    break;

                case "csv": case "json":
                    items[i] = paperContents(player, contents, file, Material.MAP,true);
                    break;

                case "sk":
                    items[i] = paperContents(player, contents, file, Material.DIAMOND,true);
                    break;

                case "jar":
                    items[i] = paperContents(player, contents, file, Material.NETHER_BRICK_ITEM,true);
                    break;

                default:
                    if (!file.isFile()) {
                        items[i] = bookContents(player, contents, file, Material.BOOK, false);
                    } else {
                        items[i] = paperContents(player, contents, file, Material.PAPER,false);
                    }
                    break;
            }
        }
        if (!path.normalize().toFile().getName().equals("plugins")) {
            contents.set(5, 2, ClickableItem.of(ItemUtils.createItem(Material.ARROW, "§bPrevious Folder",
                    new String[]{"§f・Path -> " + FileUtils.changeSeparator_Slash(StringUtils.replace(path.toFile().getPath(), FileUtils.getBasePath(), "", 1))}, 1, true),
                    e -> {
                new DirectoryInventory(this.previousFolder.path, this.previousFolder.previousFolder).getDirectory().open(player);
                        FileManager.playSound(player, Sound.ENTITY_SHULKER_OPEN, 10, 1);
            }));
        }
        pagination.setItems(items);
        pagination.setItemsPerPage(36);
        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        arrow(player, contents);
    }

    private void arrow(Player player, InventoryContents contents){
        Pagination pagination = contents.pagination();

        if (pagination.getPage() == 0)
            contents.set(5, 0, ClickableItem.empty(new ItemStack(Material.AIR)));
        else
            contents.set(5, 0, ClickableItem.of(ItemUtils.createItem(Material.SPECTRAL_ARROW,
                    "§aPrevious Page §b-> " + (pagination.getPage()), 1, true), e -> {
                contents.inventory().open(player, pagination.getPage() - 1);
                FileManager.playSound(player, Sound.ENTITY_SHULKER_OPEN, 10, 1);
            }));

        if (pagination.isLast())
            contents.set(5, 8, ClickableItem.empty(new ItemStack(Material.AIR)));
        else
            contents.set(5, 8, ClickableItem.of(ItemUtils.createItem(Material.SPECTRAL_ARROW,
                    "§aNext Page §b-> " + (pagination.getPage() + 2), 1, true), e -> {
                contents.inventory().open(player, pagination.getPage() + 1);
                FileManager.playSound(player, Sound.ENTITY_SHULKER_OPEN, 10, 1);
            }));
    }

    private ClickableItem paperContents(Player player, InventoryContents contents, File file, Material material, boolean glowing){
        ItemStack item = ItemUtils.createItem(material, "§f" + file.getName(),
                new String[]{"§bクリックで編集", "  ", "§f鉄床にはfile.ioなどのURL", "§fを入力してください"}, 1, glowing);
        if (contents.property("edit_name", false) || contents.property("delete_file", false))
            item = ItemUtils.createItem(material, "§f" + file.getName(), new String[]{"§bクリックで選択"}, 1, glowing);

        return ClickableItem.of(item, no -> {
            String name2 = FileUtils.changeSeparator_Slash(StringUtils.replace(file.getAbsolutePath(),
                    FileUtils.getBasePath() + "plugins" + File.separator, "", 1));
            if (FileManager.getManagerData().getNoEditFiles().contains(name2)){
                player.sendMessage(FileManager.PX + "§cそのファイルは変更できません。");
                FileManager.playSound(player, Sound.BLOCK_NOTE_BASS, 10, 1);
                return;
            }

            if (contents.property("edit_name", false)) {
                new AnvilGUI("§aファイル名変更", ItemUtils.createItem(Material.PAPER, file.getName(), 1, glowing), e -> {
                    String name = e.getItem().getItemMeta().getDisplayName();
                    if (StringUtils.containsAny(name, new char[]{'\\', '/', ':', '*', '?', '\"', '<', '|'})) {
                        player.sendMessage(FileManager.PX + "§c使用できない文字が含まれています");
                        FileManager.playSound(player, Sound.BLOCK_NOTE_BASS, 10, 1);
                    }

                    player.closeInventory();

                    new BukkitRunnable() {
                        public void run() {
                            player.sendMessage(FileManager.PX + "§bファイル名を変更中...");
                            file.renameTo(new File(path.toFile().getPath() + File.separator + name));
                            player.sendMessage(FileManager.PX + "§aファイル名を「" + name + "」に変更しました");
                            new DirectoryInventory(path, previousFolder).getDirectory().open(player);
                            FileManager.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                        }
                    }.runTaskAsynchronously(FileManager.getPlugin());
                }).open(player);
                return;
            }
            else if (contents.property("delete_file", false)) {
                new DeleteFileCheckInventory(file, this, contents.pagination().getPage()).getDirectory().open(player);
            }
            else {
                new AnvilGUI("§aURL", ItemUtils.createItem(Material.PAPER, file.getName() + " (URL入力)", 1, glowing), e -> {
                    String url = e.getItem().getItemMeta().getDisplayName();

                    player.closeInventory();

                    new BukkitRunnable() {
                        public void run() {
                            player.sendMessage(FileManager.PX + "§bファイル内容を変更中...");
                            try {
                                FileUtils.DownloadFile(url, file.toPath());
                            } catch (Exception exception) {
                                if (exception.getMessage().contains("no protocol")){
                                    player.sendMessage(FileManager.PX + "§cエラーが発生しました。§7(URL: " + url + "は存在しません)");
                                } else{
                                    player.sendMessage(FileManager.PX + "§c不明なエラーが発生しました。" +
                                            "§7(" + exception.getClass().getName() + ": " + exception.getMessage() + ")");
                                    exception.printStackTrace();
                                }
                                return;
                            }
                            player.sendMessage(FileManager.PX + "§aファイル内容をURL「" + url + "」に変更しました");
                            new DirectoryInventory(path, previousFolder).getDirectory().open(player);
                            FileManager.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                        }
                    }.runTaskAsynchronously(FileManager.getPlugin());
                }).open(player);
            }
        });
    }

    private ClickableItem bookContents(Player player, InventoryContents contents, File file, Material material, boolean glowing){
        ItemStack item = ItemUtils.createItem(material, "§f" + file.getName(), 1, glowing);
        if (contents.property("edit_name", false) || contents.property("delete_file", false))
            item = ItemUtils.createItem(material, "§f" + file.getName(), new String[]{"§bクリックで選択"}, 1, glowing);

        return ClickableItem.of(item, no -> {
            if (contents.property("edit_name", false)){
                new AnvilGUI("§aファイル名変更", ItemUtils.createItem(Material.PAPER, file.getName(), 1, glowing), e -> {
                    String name = e.getItem().getItemMeta().getDisplayName();
                    if (StringUtils.containsAny(name, new char[]{'\\', '/', ':', '*', '?', '\"', '<', '|'})) {
                        player.sendMessage(FileManager.PX + "§c使用できない文字が含まれています");
                        FileManager.playSound(player, Sound.BLOCK_NOTE_BASS, 10, 1);
                    }

                    player.closeInventory();

                    new BukkitRunnable(){
                        public void run() {
                            player.sendMessage(FileManager.PX + "§bファイル名を変更中...");
                            file.renameTo(new File(path.toFile().getPath() + File.separator + name));
                            player.sendMessage(FileManager.PX + "§aファイル名を「" + name + "」に変更しました");
                            new DirectoryInventory(path, previousFolder).getDirectory().open(player);
                            FileManager.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                        }}.runTaskAsynchronously(FileManager.getPlugin());
                }).open(player);
                return;
            } else if (contents.property("delete_file", false)){
                new DeleteFileCheckInventory(file, this, contents.pagination().getPage()).getDirectory().open(player);
            } else {
                new DirectoryInventory(Paths.get(FileUtils.getDefaultPath(file.getPath())), this).getDirectory().open(player);
            }
            FileManager.playSound(player, Sound.ENTITY_SHULKER_OPEN, 10, 1);
        });
    }

}
