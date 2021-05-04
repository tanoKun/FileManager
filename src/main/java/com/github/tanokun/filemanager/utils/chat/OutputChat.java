package com.github.tanokun.filemanager.utils.chat;

import com.github.tanokun.filemanager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class OutputChat {
    private static HashMap<UUID, OutputChat> chats = new HashMap<>();

    private Consumer<AsyncPlayerChatEvent> onChat;

    public OutputChat(Player player, Consumer<AsyncPlayerChatEvent> onChat) {
        this.onChat = onChat;
        chats.put(player.getUniqueId(), this);
    }

    public static class ChatListener implements Listener {
        @EventHandler
        public void onChat(AsyncPlayerChatEvent e) {
            if (!chats.containsKey(e.getPlayer().getUniqueId())) return;
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(FileManager.getPlugin(), () -> {
                chats.get(e.getPlayer().getUniqueId()).onChat.accept(e);
                chats.remove(e.getPlayer().getUniqueId());
            });
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            if (!chats.containsKey(e.getPlayer().getUniqueId())) return;
            chats.remove(e.getPlayer().getUniqueId());
        }

    }
}
