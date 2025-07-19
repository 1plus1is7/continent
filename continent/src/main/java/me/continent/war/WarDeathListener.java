package me.continent.war;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WarDeathListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Village village = VillageManager.getByPlayer(player.getUniqueId());
        if (village == null || village.getKingdom() == null) return;
        War war = WarManager.getWar(village.getKingdom());
        if (war == null) return;
        if (!war.isVillageDestroyed(village.getName())) return;

        war.banPlayer(player.getUniqueId());
        player.kickPlayer("전쟁 중 코어가 파괴되어 전쟁 종료 시까지 접속할 수 없습니다.");
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (WarManager.isPlayerBanned(uuid)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    "전쟁 중 코어가 파괴되어 전쟁 종료 시까지 접속할 수 없습니다.");
        }
    }
}
