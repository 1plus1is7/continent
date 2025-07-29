package me.continent.listener;

import me.continent.player.PlayerDataManager;
import me.continent.stat.StatType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class DoubleJumpListener implements Listener {
    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        GameMode gm = player.getGameMode();
        if (gm != GameMode.SURVIVAL && gm != GameMode.ADVENTURE) {
            return;
        }
        var data = PlayerDataManager.get(player.getUniqueId());
        if (data.getStats().get(StatType.AGILITY) < 10) return;
        event.setCancelled(true);
        player.setAllowFlight(false);
        Vector vec = player.getLocation().getDirection().multiply(0.5).setY(0.7);
        player.setVelocity(vec);
    }

    @EventHandler
    public void onLand(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.isOnGround()) return;
        GameMode gm = player.getGameMode();
        if (gm != GameMode.SURVIVAL && gm != GameMode.ADVENTURE) return;
        var data = PlayerDataManager.get(player.getUniqueId());
        if (data.getStats().get(StatType.AGILITY) >= 10) {
            player.setAllowFlight(true);
        }
    }
}
