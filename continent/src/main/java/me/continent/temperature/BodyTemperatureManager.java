package me.continent.temperature;

import me.continent.ContinentPlugin;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.continent.utils.ShelterLevel;

public class BodyTemperatureManager {
    private static int taskId = -1;

    public static void start() {
        stop();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                ContinentPlugin.getInstance(),
                BodyTemperatureManager::tick,
                100L, 100L);
    }

    public static void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    private static void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTemperature(player);
        }
    }

    private static void updateTemperature(Player player) {
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        double temp = data.getBodyTemperature();
        double change = calculateChange(player);
        temp = clamp(25.0, 45.0, temp + change);
        data.setBodyTemperature(temp);
        applyDebuff(player, temp);
    }

    private static double calculateChange(Player player) {
        double change = 0.0;
        if (player.isInWater()) {
            change -= 3.0;
        } else {
            ShelterLevel level = ShelterLevel.getShelterLevel(player);
            if (level == ShelterLevel.INDOOR) {
                change -= 3.0;
            }
        }
        if (wearingIronArmor(player)) {
            change += 2.5;
        }

        double env = TemperatureManager.getCurrentTemperature(player);
        change += (env - 36.5) * 0.02;

        return change;
    }

    private static boolean wearingIronArmor(Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) continue;
            Material mat = item.getType();
            if (mat.name().contains("IRON")) {
                return true;
            }
        }
        return false;
    }

    private static double clamp(double min, double max, double val) {
        return Math.max(min, Math.min(max, val));
    }

    private static void applyDebuff(Player player, double temp) {
        double diff = Math.abs(temp - 36.5);
        int stage = 0;
        if (diff > 6.0) stage = 4;
        else if (diff > 4.5) stage = 3;
        else if (diff > 3.0) stage = 2;
        else if (diff > 1.5) stage = 1;
        StatusEffectManager.updateTemperatureStage(player, stage);
    }
}
