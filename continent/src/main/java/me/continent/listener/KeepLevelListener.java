package me.continent.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Keeps player levels on death while still dropping inventory and items.
 */
public class KeepLevelListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setKeepLevel(true);
        event.setDroppedExp(0);
        event.setKeepInventory(false);
    }
}
