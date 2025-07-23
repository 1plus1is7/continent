package me.continent.chat;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.utils.NationChatLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class NationChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        PlayerData data = PlayerDataManager.get(uuid);

        if (data == null || !data.isNationChatEnabled()) return;

        Nation nation = NationManager.getByPlayer(uuid);
        if (nation == null) return;

        event.setCancelled(true); // 전체 채팅 차단

        String message = event.getMessage();
        String formatted = ChatColor.GREEN + "[국가채팅] "
                + ChatColor.YELLOW + player.getName()
                + ChatColor.WHITE + ": " + message;

        for (UUID memberUuid : nation.getMembers()) {
            Player member = Bukkit.getPlayer(memberUuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(formatted);
            }
        }

        // 로그 파일 저장
        NationChatLogger.logMessage(nation.getName(), player.getName(), message);
    }
}
