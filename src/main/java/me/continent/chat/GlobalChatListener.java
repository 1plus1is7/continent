package me.continent.chat;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GlobalChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return; // nation chat may cancel
        Player player = event.getPlayer();
        Nation nation = NationManager.getByPlayer(player.getUniqueId());
        String name = nation != null ? nation.getName() : "없음";
        String prefix = ChatColor.GREEN + "[" + name + "] " + ChatColor.RESET;
        event.setFormat(prefix + "%s: %s");
    }
}
