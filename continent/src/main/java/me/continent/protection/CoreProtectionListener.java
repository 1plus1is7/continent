package me.continent.protection;

import me.continent.village.Village;
import me.continent.village.VillageManager;
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
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c코어는 명령어로만 제거할 수 있습니다.");
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(this::isCoreBlock);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(this::isCoreBlock);
    }
}
