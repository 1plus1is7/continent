package me.continent.listener;

import me.continent.village.Village;
import me.continent.village.VillageManager;
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
    private final Map<UUID, Village> currentIntrusion = new HashMap<>();

    private void sendAlert(Village village, Player intruder) {
        String msg = "§c" + intruder.getName() + "님이 영토에 침입했습니다.";
        for (UUID uuid : village.getMembers()) {
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

    private void handleIntrusion(Player player, Village toVillage) {
        UUID uuid = player.getUniqueId();
        Village playerVillage = VillageManager.getByPlayer(uuid);

        if (toVillage != null && (playerVillage == null || !toVillage.getMembers().contains(uuid))) {
            // entering foreign village
            if (!toVillage.equals(currentIntrusion.get(uuid))) {
                cancelAlert(uuid);
                sendAlert(toVillage, player);
                BukkitTask task = Bukkit.getScheduler().runTaskTimer(ContinentPlugin.getInstance(), () -> sendAlert(toVillage, player), 6000L, 6000L);
                alertTasks.put(uuid, task);
                currentIntrusion.put(uuid, toVillage);
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
        Village village = VillageManager.getByChunk(chunk);
        handleIntrusion(player, village);
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

        Village fromVillage = VillageManager.getByChunk(from);
        Village toVillage = VillageManager.getByChunk(to);

        // 같은 마을 or 같은 상태(null → null 포함)면 무시
        if (Objects.equals(fromVillage, toVillage)) return;

        handleIntrusion(player, toVillage);

        if (toVillage != null) {
            player.showTitle(Title.title(
                    Component.text("§a" + toVillage.getName()),
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
