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
import java.util.Random;

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

    private static final Color[] MULTI_COLORS = new Color[] {
            Color.fromRGB(0xFFD700),
            Color.fromRGB(0xFFFF00),
            Color.fromRGB(0x800080)
    };
    private static final Random RANDOM = new Random();

    private static void launchFireworks(Player player, boolean multiple) {
        if (multiple) {
            int total = 3 + RANDOM.nextInt(3); // 3~5
            new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    Color color = MULTI_COLORS[RANDOM.nextInt(MULTI_COLORS.length)];
                    spawnFirework(player, color, true);
                    count++;
                    if (count >= total) cancel();
                }
            }.runTaskTimer(ContinentPlugin.getInstance(), 0L, 4L);
        } else {
            spawnFirework(player, Color.fromRGB(0xFFDB4D), false);
        }
    }

    private static void spawnFirework(Player player, Color color, boolean around) {
        var loc = player.getLocation().clone();
        if (around) {
            double dx = (RANDOM.nextDouble() - 0.5) * 4.0;
            double dz = (RANDOM.nextDouble() - 0.5) * 4.0;
            loc.add(dx, 0, dz);
        }
        Firework fw = (Firework) player.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().withColor(color).with(FireworkEffect.Type.STAR).build());
        meta.setPower(0);
        fw.setFireworkMeta(meta);
        fw.detonate();
    }
}
