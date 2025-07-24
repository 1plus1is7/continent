package me.continent.crop;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class CropListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        CropType type = CropType.fromMaterial(event.getBlockPlaced().getType());
        if (type != null) {
            CropGrowthManager.registerCrop(event.getBlockPlaced(), type);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        CropType type = CropType.fromMaterial(event.getBlock().getType());
        if (type != null) {
            CropGrowthManager.unregisterCrop(event.getBlock());
        }
    }

    @EventHandler
    public void onGrow(BlockGrowEvent event) {
        CropType type = CropType.fromMaterial(event.getBlock().getType());
        if (type != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFertilize(BlockFertilizeEvent event) {
        for (var state : event.getBlocks()) {
            if (CropType.fromMaterial(state.getType()) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
