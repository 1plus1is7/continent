package me.continent.listener;

import me.continent.season.SeasonManager;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class RainListener implements Listener {

    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        if (!SeasonManager.isRainySeason()) return;
        if (event.getNewState().getBlockData() instanceof Ageable ageable) {
            if (Math.random() < 0.2) {
                int next = Math.min(ageable.getAge() + 1, ageable.getMaximumAge());
                ageable.setAge(next);
                event.getNewState().setBlockData(ageable);
            }
        }
    }
}
