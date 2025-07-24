package me.continent.protection;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.war.WarManager;
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
        for (Nation nation : NationManager.getAll()) {
            Location core = nation.getCoreLocation();
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
            Nation owner = NationManager.getByChunk(event.getBlock().getChunk());
            if (owner == null) {
                event.setCancelled(true);
                return;
            }
            Nation attackerNation = NationManager.getByPlayer(event.getPlayer().getUniqueId());
            boolean allowed = attackerNation != null
                    && WarManager.isAtWar(owner.getName(), attackerNation.getName())
                    && !owner.getName().equalsIgnoreCase(attackerNation.getName());
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
            Nation v = NationManager.getByChunk(block.getChunk());
            if (v == null) return false;
            return true;
        });
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            if (!isCoreBlock(block)) return false;
            Nation v = NationManager.getByChunk(block.getChunk());
            if (v == null) return false;
            return true;
        });
    }
}
