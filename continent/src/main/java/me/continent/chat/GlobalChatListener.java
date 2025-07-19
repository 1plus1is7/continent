package me.continent.chat;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GlobalChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return; // kingdom chat may cancel
        Player player = event.getPlayer();
        Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
        String name = kingdom != null ? kingdom.getName() : "없음";
        String prefix = ChatColor.GREEN + "[" + name + "] " + ChatColor.RESET;
        event.setFormat(prefix + "%s: %s");
    }
}
