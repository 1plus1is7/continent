package me.continent.listener;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.village.service.MaintenanceService;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class MaintenanceJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        if (data == null) return;

        Village village = VillageManager.getByPlayer(player.getUniqueId());
        if (village == null) return;
        if (!village.getKing().equals(player.getUniqueId())) return;

        int diff = village.getMaintenanceCount() - data.getKnownMaintenance();
        if (diff > 0) {
            double total = MaintenanceService.getCost() * diff;
            player.sendMessage("§e마을 유지비로 " + diff + "주 동안 총 " + total + "G가 차감되었습니다.");
            data.setKnownMaintenance(village.getMaintenanceCount());
            PlayerDataManager.save(player.getUniqueId());
        }
    }
}
