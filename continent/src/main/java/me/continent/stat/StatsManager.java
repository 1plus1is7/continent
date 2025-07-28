package me.continent.stat;

import me.continent.ContinentPlugin;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Color;

public class StatsManager {

    public static void checkLevelUp(Player player) {
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        PlayerStats stats = data.getStats();
        int level = player.getLevel();
        int last = stats.getLastLevelGiven();
        if (level < 100) return;
        int next = ((level - 100) / 25) * 25 + 100;
        if (level < next || next <= last) return;
        stats.setLastLevelGiven(next);
        stats.addPoints(1);
        PlayerDataManager.save(player.getUniqueId());
        if (next % 100 == 0) {
            launchFireworks(player, true);
            player.sendTitle("§6★ 마스터 레벨 도달 ★", "§e스탯 포인트 +1!", 10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        } else {
            launchFireworks(player, false);
            player.sendActionBar("§e[Stat] §e스탯 포인트 +1!");
        }
    }

    private static void launchFireworks(Player player, boolean multiple) {
        if (multiple) {
            new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    spawnFirework(player, Color.fromRGB(0xFFDB4D));
                    count++;
                    if (count >= 5) cancel();
                }
            }.runTaskTimer(ContinentPlugin.getInstance(), 0L, 4L);
        } else {
            spawnFirework(player, Color.fromRGB(0xFFDB4D));
        }
    }

    private static void spawnFirework(Player player, Color color) {
        Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().withColor(color).with(FireworkEffect.Type.STAR).build());
        meta.setPower(0);
        fw.setFireworkMeta(meta);
        fw.detonate();
    }
}
