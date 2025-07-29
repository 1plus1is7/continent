package me.continent.listener;

import me.continent.stat.StatsEffectManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class StatJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        StatsEffectManager.apply(event.getPlayer());
    }
}
