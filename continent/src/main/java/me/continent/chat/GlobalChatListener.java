package me.continent.chat;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GlobalChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return; // village chat may cancel
        Player player = event.getPlayer();
        Village village = VillageManager.getByPlayer(player.getUniqueId());
        String name = village != null ? village.getName() : "없음";
        String prefix = ChatColor.GREEN + "[" + name + "] " + ChatColor.RESET;
        event.setFormat(prefix + "%s: %s");
    }
}
