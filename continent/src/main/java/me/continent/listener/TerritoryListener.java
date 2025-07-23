package me.continent.listener;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.ContinentPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TerritoryListener implements Listener {
    private final Map<UUID, BukkitTask> alertTasks = new HashMap<>();
    private final Map<UUID, Nation> currentIntrusion = new HashMap<>();

    private void sendAlert(Nation nation, Player intruder) {
        String msg = "§c" + intruder.getName() + "님이 영토에 침입했습니다.";
        for (UUID uuid : nation.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(msg);
            }
        }
    }

    private void cancelAlert(UUID uuid) {
        BukkitTask task = alertTasks.remove(uuid);
        if (task != null) task.cancel();
    }

    private void handleIntrusion(Player player, Nation toNation) {
        UUID uuid = player.getUniqueId();
        Nation playerNation = NationManager.getByPlayer(uuid);

        if (toNation != null && (playerNation == null || !toNation.getMembers().contains(uuid))) {
            // entering foreign nation
            if (!toNation.equals(currentIntrusion.get(uuid))) {
                cancelAlert(uuid);
                sendAlert(toNation, player);
                BukkitTask task = Bukkit.getScheduler().runTaskTimer(ContinentPlugin.getInstance(), () -> sendAlert(toNation, player), 6000L, 6000L);
                alertTasks.put(uuid, task);
                currentIntrusion.put(uuid, toNation);
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
        } else {
            player.removePotionEffect(PotionEffectType.GLOWING);
            cancelAlert(uuid);
            currentIntrusion.remove(uuid);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();
        Nation nation = NationManager.getByChunk(chunk);
        handleIntrusion(player, nation);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        cancelAlert(uuid);
        currentIntrusion.remove(uuid);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Chunk from = event.getFrom().getChunk();
        Chunk to = event.getTo().getChunk();

        // 같은 청크라면 무시
        if (from.equals(to)) return;

        Player player = event.getPlayer();

        Nation fromNation = NationManager.getByChunk(from);
        Nation toNation = NationManager.getByChunk(to);

        // 같은 국가 or 같은 상태(null → null 포함)면 무시
        if (Objects.equals(fromNation, toNation)) return;

        handleIntrusion(player, toNation);

        if (toNation != null) {
            player.showTitle(Title.title(
                    Component.text("§a" + toNation.getName()),
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
