package me.continent.listener;

import me.continent.stat.StatsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class StatLevelListener implements Listener {
    @EventHandler
    public void onLevel(PlayerLevelChangeEvent event) {
        StatsManager.checkLevelUp(event.getPlayer());
    }
}
