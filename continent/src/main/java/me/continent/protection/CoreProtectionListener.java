package me.continent.protection;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.war.WarManager;
import me.continent.nation.nationManager;
import me.continent.nation.nation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CoreProtectionListener implements Listener {

    private boolean isCoreBlock(Block block) {
        Location loc = block.getLocation();
        for (Village village : VillageManager.getAll()) {
            Location core = village.getCoreLocation();
            if (core == null) continue;
            if (core.getWorld().equals(loc.getWorld())
                    && core.getBlockX() == loc.getBlockX()
                    && core.getBlockY() == loc.getBlockY()
                    && core.getBlockZ() == loc.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isCoreBlock(event.getBlock())) {
            Village owner = VillageManager.getByChunk(event.getBlock().getChunk());
            if (owner == null) {
                event.setCancelled(true);
                return;
            }
            if (owner.getnation() != null) {
                nation k = nationManager.getByName(owner.getnation());
                if (k != null && !k.isTerritoryProtectionEnabled()) return;
            }
            Village attackerVillage = VillageManager.getByPlayer(event.getPlayer().getUniqueId());
            boolean allowed = attackerVillage != null
                    && owner.getnation() != null
                    && attackerVillage.getnation() != null
                    && WarManager.isAtWar(owner.getnation(), attackerVillage.getnation())
                    && !owner.getnation().equalsIgnoreCase(attackerVillage.getnation());
            if (!allowed) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§c코어는 명령어로만 제거할 수 있습니다.");
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> {
            if (!isCoreBlock(block)) return false;
            Village v = VillageManager.getByChunk(block.getChunk());
            if (v != null && v.getnation() != null) {
                nation k = nationManager.getByName(v.getnation());
                if (k != null && !k.isTerritoryProtectionEnabled()) return false;
            }
            return true;
        });
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            if (!isCoreBlock(block)) return false;
            Village v = VillageManager.getByChunk(block.getChunk());
            if (v != null && v.getnation() != null) {
                nation k = nationManager.getByName(v.getnation());
                if (k != null && !k.isTerritoryProtectionEnabled()) return false;
            }
            return true;
        });
    }
}
