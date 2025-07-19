package me.continent.war;

import me.continent.ContinentPlugin;
import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoreAttackListener implements Listener {
    private final Map<String, BukkitTask> alertTasks = new HashMap<>();

    private boolean isCoreBlock(Block block) {
        Village village = VillageManager.getByChunk(block.getChunk());
        if (village == null) return false;
        if (village.getCoreLocation() == null) return false;
        return village.getCoreLocation().getBlock().equals(block);
    }

    private Village getVillageByCore(Block block) {
        Village village = VillageManager.getByChunk(block.getChunk());
        if (village == null) return null;
        if (village.getCoreLocation() == null) return null;
        if (village.getCoreLocation().getBlock().equals(block)) {
            return village;
        }
        return null;
    }

    private void startAlert(Village village) {
        String kingdomName = village.getKingdom();
        if (kingdomName == null) return;
        String key = kingdomName.toLowerCase();
        if (alertTasks.containsKey(key)) return;

        Runnable alert = () -> {
            War war = WarManager.getWar(kingdomName);
            if (war == null) {
                cancel(key);
                return;
            }
            if (village.getCoreLocation() == null || village.getCoreLocation().getBlock().getType() != Material.BEACON) {
                cancel(key);
                return;
            }
            sendAlert(kingdomName, village.getName());
        };

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(ContinentPlugin.getInstance(), alert, 0L, 300L);
        alertTasks.put(key, task);
    }

    private void cancel(String key) {
        BukkitTask task = alertTasks.remove(key);
        if (task != null) task.cancel();
    }

    private void sendAlert(String kingdomName, String villageName) {
        Kingdom kingdom = KingdomManager.getByName(kingdomName);
        if (kingdom == null) return;
        for (String vName : kingdom.getVillages()) {
            Village v = VillageManager.getByName(vName);
            if (v == null) continue;
            for (UUID uuid : v.getMembers()) {
                Player member = Bukkit.getPlayer(uuid);
                if (member == null || !member.isOnline()) continue;
                member.showTitle(Title.title(
                        Component.text("§c경고"),
                        Component.text(villageName + " 코어가 공격받고 있습니다!"),
                        Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1000), Duration.ofMillis(250))
                ));
                member.playSound(member.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
            }
        }
    }

    private void handle(Block block, Player attacker) {
        Village village = getVillageByCore(block);
        if (village == null || village.getKingdom() == null) return;
        Village attackerVillage = VillageManager.getByPlayer(attacker.getUniqueId());
        if (attackerVillage == null || attackerVillage.getKingdom() == null) return;
        if (!WarManager.isAtWar(attackerVillage.getKingdom(), village.getKingdom())) return;
        startAlert(village);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        if (isCoreBlock(event.getBlock())) {
            handle(event.getBlock(), event.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isCoreBlock(event.getBlock())) {
            handle(event.getBlock(), event.getPlayer());
        }
    }
}
