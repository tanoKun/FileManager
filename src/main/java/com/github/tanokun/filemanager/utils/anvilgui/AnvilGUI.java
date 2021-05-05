package com.github.tanokun.filemanager.utils.anvilgui;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class AnvilGUI {

    public final static HashMap<UUID, AnvilGUI> activeGUIs = new HashMap<>();

    private final String inventoryTitle;
    private final ItemStack item;
    private final Consumer<String> complete;
    private Inventory inventory;
    private boolean open;

    public AnvilGUI(String inventoryTitle, ItemStack item, Consumer<String> complete) {
        this.inventoryTitle = inventoryTitle;
        this.item = item;
        this.complete = complete;
    }

    public void openInventory(Player player) {
        if (open) return;
        this.open = true;
        player.closeInventory();
        AnvilContainer anvilContainer =
                new AnvilContainer(player, toPlayerHandle(player).nextContainerCounter(), inventoryTitle, item);

        toPlayerHandle(player).playerConnection.sendPacket(
                new PacketPlayOutOpenWindow(anvilContainer.getContainerId(), Containers.ANVIL, new ChatMessage(inventoryTitle)));
        toPlayerHandle(player).activeContainer = anvilContainer;

        inventory = anvilContainer.getBukkitView().getTopInventory();
        inventory.setItem(0, item);
        anvilContainer.addSlotListener(((CraftPlayer) player).getHandle());
        activeGUIs.put(player.getUniqueId(), this);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
    public String getInventoryTitle() {return inventoryTitle;}

    public EntityPlayer toPlayerHandle(Player player) {return ((CraftPlayer)player).getHandle();}

    public Consumer<String> getComplete() {return complete;}

    public static class AnvilContainer extends ContainerAnvil {

        public AnvilContainer(Player player, int containerId, String guiTitle, ItemStack item) {
            super(containerId, ((CraftPlayer) player).getHandle().inventory,
                    ContainerAccess.at(((CraftWorld) player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
            this.checkReachable = false;
            setTitle(new ChatMessage(guiTitle));

        }

        @Override
        public void e() {
            super.e();
            this.levelCost.set(0);
        }

        @Override
        public void b(EntityHuman entityhuman) {
        }

        @Override
        protected void a(EntityHuman entityhuman, World world, IInventory iinventory) {
        }

        public int getContainerId() {
            return windowId;
        }

    }
}
