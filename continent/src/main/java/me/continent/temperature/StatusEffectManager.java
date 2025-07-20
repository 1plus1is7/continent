package me.continent.temperature;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatusEffectManager {
    private static final Map<UUID, Integer> tempStages = new HashMap<>();

    public static void updateTemperatureStage(Player player, int stage) {
        Integer current = tempStages.get(player.getUniqueId());
        if (current != null && current == stage) return;
        clearEffects(player);
        tempStages.put(player.getUniqueId(), stage);

        if (stage == 0) return;
        int duration = 120; // ticks
        switch (stage) {
            case 1 -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 0, false, false));
            case 2 -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, false, false));
            }
            case 3 -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, false, false));
            }
            case 4 -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 4, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, false, false));
                player.sendTitle("§c기절", "", 10, 60, 20);
            }
        }
    }

    private static void clearEffects(Player player) {
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.NAUSEA);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }
}
