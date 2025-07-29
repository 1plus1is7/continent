package me.continent.stat;

import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class StatsEffectManager {
    public static void apply(Player player) {
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        PlayerStats stats = data.getStats();
        applyStrength(player, stats.get(StatType.STRENGTH));
        applyAgility(player, stats.get(StatType.AGILITY));
        applyIntelligence(player, stats.get(StatType.INTELLIGENCE));
        applyVitality(player, stats.get(StatType.VITALITY));
    }

    private static void applyStrength(Player player, int val) {
        AttributeInstance dmg = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (dmg != null) {
            double base = dmg.getDefaultValue();
            dmg.setBaseValue(base * (1 + 0.05 * val));
        }
        if (val >= 10) {
            AttributeInstance spd = player.getAttribute(Attribute.ATTACK_SPEED);
            if (spd != null) spd.setBaseValue(1024.0); // effectively no delay
        }
    }

    private static void applyAgility(Player player, int val) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed != null) {
            double base = speed.getDefaultValue();
            speed.setBaseValue(base * (1 + 0.05 * val));
        }
        var gm = player.getGameMode();
        boolean survivalLike = gm == org.bukkit.GameMode.SURVIVAL || gm == org.bukkit.GameMode.ADVENTURE;
        if (val >= 10 && survivalLike) {
            player.setAllowFlight(true);
        } else if (survivalLike) {
            player.setAllowFlight(false);
        }
    }

    private static void applyIntelligence(Player player, int val) {
        AttributeInstance luck = player.getAttribute(Attribute.LUCK);
        if (luck != null) {
            luck.setBaseValue(0.05 * val);
        }
    }

    private static void applyVitality(Player player, int val) {
        AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
        if (health != null) {
            double base = health.getDefaultValue();
            double newMax = base * (1 + 0.05 * val);
            health.setBaseValue(newMax);
            if (player.getHealth() > newMax) player.setHealth(newMax);
        }
    }
}
