package me.continent.protection;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
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

        Kingdom owner = KingdomManager.getByChunk(chunk);
        if (owner == null) return; // 야생

        Kingdom playerKingdom = KingdomManager.getByPlayer(player.getUniqueId());

        // 소유자 국가와 다르면 보호
        if (!owner.equals(playerKingdom)) {
            event.setCancelled(true);
            player.sendMessage("§c이 지역은 다른 국가의 보호 구역입니다.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        Kingdom owner = KingdomManager.getByChunk(chunk);
        if (owner == null) return;

        Kingdom playerKingdom = KingdomManager.getByPlayer(player.getUniqueId());

        if (!owner.equals(playerKingdom)) {
            event.setCancelled(true);
            player.sendMessage("§c이 지역은 다른 국가의 보호 구역입니다.");
        }
    }
}