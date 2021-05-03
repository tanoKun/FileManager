package com.github.tanokun.filemanager.utils.anvil;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.tanokun.filemanager.FileManager;

public class AnvilListener extends PacketAdapter {
    public AnvilListener() {
        super(FileManager.getPlugin(), PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Client.WINDOW_CLICK);
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        AnvilGUI anvilGUI = AnvilGUI.ANVIL_GUI_MAP.get(e.getPlayer().getUniqueId());
        if (anvilGUI != null) {
            if (e.getPacketType() == PacketType.Play.Client.WINDOW_CLICK) {
                if (e.getPacket().getIntegers().read(1) == 2) {
                    anvilGUI.setItem(e.getPacket().getItemModifier().read(0));
                    anvilGUI.getDone().accept(anvilGUI);
                    AnvilGUI.ANVIL_GUI_MAP.remove(e.getPlayer().getUniqueId());
                }
            } else if (e.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
                AnvilGUI.ANVIL_GUI_MAP.remove(e.getPlayer().getUniqueId());
            }
        }
    }
}
