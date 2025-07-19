package me.continent.chat;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.utils.VillageChatLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class VillageChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerData data = PlayerDataManager.get(uuid);

        if (data == null || !data.isVillageChatEnabled()) return;

        Village village = VillageManager.getByPlayer(uuid);
        if (village == null) return;

        event.setCancelled(true); // 전체 채팅 차단

        String message = event.getMessage();
        String formatted = ChatColor.GREEN + "[마을채팅] "
                + ChatColor.YELLOW + player.getName()
                + ChatColor.WHITE + ": " + message;

        for (UUID memberUuid : village.getMembers()) {
            Player member = Bukkit.getPlayer(memberUuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(formatted);
            }
        }

        // 로그 파일 저장
        VillageChatLogger.logMessage(village.getName(), player.getName(), message);
    }
}
