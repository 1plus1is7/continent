package me.continent.chat;

import me.continent.kingdom.nation;
import me.continent.kingdom.nationManager;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class nationChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        if (data == null || !data.isnationChatEnabled()) return;

        Village village = VillageManager.getByPlayer(player.getUniqueId());
        if (village == null || village.getnation() == null) return;
        nation kingdom = nationManager.getByName(village.getnation());
        if (kingdom == null) return;

        event.setCancelled(true);
        String msg = event.getMessage();
        String formatted = ChatColor.AQUA + "[국가채팅] " + ChatColor.YELLOW + player.getName() + ChatColor.WHITE + ": " + msg;
        for (String vName : kingdom.getVillages()) {
            Village v = VillageManager.getByName(vName);
            if (v == null) continue;
            for (UUID memberId : v.getMembers()) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    member.sendMessage(formatted);
                }
            }
        }
    }
}
