package me.continent.listener;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.Objects;

public class TerritoryListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Chunk from = event.getFrom().getChunk();
        Chunk to = event.getTo().getChunk();

        // 같은 청크라면 무시
        if (from.equals(to)) return;

        Player player = event.getPlayer();

        Kingdom fromKingdom = KingdomManager.getByChunk(from);
        Kingdom toKingdom = KingdomManager.getByChunk(to);

        // 같은 국가 or 같은 상태(null → null 포함)면 무시
        if (Objects.equals(fromKingdom, toKingdom)) return;

        if (toKingdom != null) {
            player.showTitle(Title.title(
                    Component.text("§a" + toKingdom.getName()),
                    Component.text("§7점령된 영토"),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
            ));
        } else {
            player.showTitle(Title.title(
                    Component.text("§7야생"),
                    Component.empty(),
                    Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(2000), Duration.ofMillis(500))
            ));
        }
    }
}
