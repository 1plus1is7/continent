package me.continent.listener;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.nation.service.MaintenanceService;
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

        Nation nation = NationManager.getByPlayer(player.getUniqueId());
        if (nation == null) return;
        if (!nation.getKing().equals(player.getUniqueId())) return;

        int diff = nation.getMaintenanceCount() - data.getKnownMaintenance();
        if (diff > 0) {
            double total = MaintenanceService.getCost() * diff;
            player.sendMessage("§e국가 유지비로 " + diff + "주 동안 총 " + total + "G가 차감되었습니다.");
            data.setKnownMaintenance(nation.getMaintenanceCount());
            PlayerDataManager.save(player.getUniqueId());
        }
    }
}
