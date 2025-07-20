package me.continent.season;

import me.continent.ContinentPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.Material;

public class SeasonVisuals {
    private static int taskId = -1;

    static void start() {
        stop();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(ContinentPlugin.getInstance(), SeasonVisuals::tick, 0L, 100L);
    }

    static void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    private static void tick() {
        Season season = SeasonManager.getCurrentSeason();
        int count = SeasonManager.getParticleCount(season);
        for (Player player : Bukkit.getOnlinePlayers()) {
            switch (season) {
                case SPRING -> {
                    player.spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), count);
                    player.playSound(player.getLocation(), Sound.ENTITY_PARROT_AMBIENT, 1f, 1f);
                }
                case SUMMER -> {
                    player.spawnParticle(Particle.END_ROD, player.getLocation(), count);
                    player.playSound(player.getLocation(), Sound.BLOCK_WATER_AMBIENT, 1f, 1f);
                }
                case AUTUMN -> {
                    player.spawnParticle(Particle.BLOCK, player.getLocation(), count, Material.OAK_LEAVES.createBlockData());
                    player.playSound(player.getLocation(), Sound.BLOCK_GRASS_STEP, 1f, 1f);
                }
                case WINTER -> {
                    player.spawnParticle(Particle.SNOWFLAKE, player.getLocation(), count);
                    player.playSound(player.getLocation(), Sound.BLOCK_SNOW_STEP, 1f, 1f);
                }
            }
        }
    }
}
