package me.continent.protection;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class TerritoryProtectionListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        Village owner = VillageManager.getByChunk(chunk);
        if (owner == null) return; // 야생

        Village playerVillage = VillageManager.getByPlayer(player.getUniqueId());

        // 소유자 마을와 다르면 보호
        if (!owner.equals(playerVillage)) {
            event.setCancelled(true);
            player.sendMessage("§c이 지역은 다른 마을의 보호 구역입니다.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        Village owner = VillageManager.getByChunk(chunk);
        if (owner == null) return;

        Village playerVillage = VillageManager.getByPlayer(player.getUniqueId());

        if (!owner.equals(playerVillage)) {
            event.setCancelled(true);
            player.sendMessage("§c이 지역은 다른 마을의 보호 구역입니다.");
        }
    }
}