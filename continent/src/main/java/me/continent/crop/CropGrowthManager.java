package me.continent.crop;

import me.continent.ContinentPlugin;
import me.continent.season.Season;
import me.continent.season.SeasonManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CropGrowthManager {
    private static final Map<Location, CropData> crops = new ConcurrentHashMap<>();
    private static int taskId = -1;
    private static int displayTask = -1;

    public static void init(ContinentPlugin plugin) {
        startTasks();
    }

    public static void shutdown() {
        stopTasks();
        crops.clear();
    }

    private static void startTasks() {
        stopTasks();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                ContinentPlugin.getInstance(), CropGrowthManager::tick, 20L, 20L);
        displayTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                ContinentPlugin.getInstance(), CropGrowthManager::displayTick, 20L, 20L);
    }

    private static void stopTasks() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        if (displayTask != -1) {
            Bukkit.getScheduler().cancelTask(displayTask);
            displayTask = -1;
        }
    }

    static void registerCrop(Block block, CropType type) {
        crops.put(block.getLocation(), new CropData(block.getLocation(), type, System.currentTimeMillis()));
    }

    static void unregisterCrop(Block block) {
        crops.remove(block.getLocation());
    }

    private static void tick() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Location, CropData>> it = crops.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Location, CropData> entry = it.next();
            CropData data = entry.getValue();
            double days = requiredDays(data.type);
            long totalMs = (long) (days * 1200_000L); // 1 day = 20min
            if (now - data.start >= totalMs) {
                Block block = data.location.getBlock();
                if (block.getType() == data.type.getMaterial()) {
                    Ageable age = (Ageable) block.getBlockData();
                    age.setAge(age.getMaximumAge());
                    block.setBlockData(age, false);
                }
                it.remove();
            }
        }
    }

    private static void displayTick() {
        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Block target = player.getTargetBlockExact(5);
            if (target == null) continue;
            CropData data = crops.get(target.getLocation());
            if (data == null) continue;
            double days = requiredDays(data.type);
            long totalMs = (long) (days * 1200_000L);
            long remain = totalMs - (now - data.start);
            if (remain < 0) remain = 0;
            String timeStr = formatTime(remain);
            player.sendActionBar(data.type.name() + " (" + timeStr + ")");
        }
    }

    private static double requiredDays(CropType type) {
        boolean rainy = SeasonManager.isRainySeason();
        Season season = SeasonManager.getCurrentSeason();
        double days = type.getBaseDays();
        if (rainy) days = type.getRainyDays();
        if (type == CropType.BEET && season == Season.WINTER) days = 6.0;
        return days;
    }

    private static String formatTime(long ms) {
        long totalSec = ms / 1000;
        long hours = totalSec / 3600;
        long minutes = (totalSec % 3600) / 60;
        long seconds = totalSec % 60;
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        }
        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        }
        return seconds + "s";
    }

    private record CropData(Location location, CropType type, long start) {}
}
