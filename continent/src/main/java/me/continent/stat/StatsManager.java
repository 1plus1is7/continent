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

    private static final java.util.UUID STR_DAMAGE_UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final java.util.UUID AGI_SPEED_UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final java.util.UUID VIT_HEALTH_UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final java.util.UUID STR_ATTACK_SPEED_UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000004");
    private static final java.util.UUID STR_KNOCKBACK_UUID = java.util.UUID.fromString("00000000-0000-0000-0000-000000000005");

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

        applyStats(player);
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

    public static void applyStats(Player player) {
        PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();

        int str = stats.get(StatType.STRENGTH);
        int agi = stats.get(StatType.AGILITY);
        int vit = stats.get(StatType.VITALITY);

        var damageAttr = player.getAttribute(org.bukkit.attribute.Attribute.ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.getModifiers().stream().filter(m -> m.getUniqueId().equals(STR_DAMAGE_UUID)).forEach(damageAttr::removeModifier);
            double bonus = 0.05 * str + Math.min(3, Math.max(0, str - 10));
            if (bonus != 0) {
                damageAttr.addTransientModifier(new org.bukkit.attribute.AttributeModifier(STR_DAMAGE_UUID, "stat_strength", bonus, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
            }
        }

        var knockAttr = player.getAttribute(org.bukkit.attribute.Attribute.ATTACK_KNOCKBACK);
        if (knockAttr != null) {
            knockAttr.getModifiers().stream().filter(m -> m.getUniqueId().equals(STR_KNOCKBACK_UUID)).forEach(knockAttr::removeModifier);
            if (str >= 14) {
                knockAttr.addTransientModifier(new org.bukkit.attribute.AttributeModifier(STR_KNOCKBACK_UUID, "stat_knockback", 1.0, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
            }
        }

        var attackSpeedAttr = player.getAttribute(org.bukkit.attribute.Attribute.ATTACK_SPEED);
        if (attackSpeedAttr != null) {
            attackSpeedAttr.getModifiers().stream().filter(m -> m.getUniqueId().equals(STR_ATTACK_SPEED_UUID)).forEach(attackSpeedAttr::removeModifier);
            if (str >= 10) {
                // High value effectively removes cooldown
                attackSpeedAttr.addTransientModifier(new org.bukkit.attribute.AttributeModifier(STR_ATTACK_SPEED_UUID, "stat_attack_speed", 4.0, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
            }
        }

        var moveAttr = player.getAttribute(org.bukkit.attribute.Attribute.MOVEMENT_SPEED);
        if (moveAttr != null) {
            moveAttr.getModifiers().stream().filter(m -> m.getUniqueId().equals(AGI_SPEED_UUID)).forEach(moveAttr::removeModifier);
            double bonus = 0.05 * agi;
            if (bonus != 0) {
                moveAttr.addTransientModifier(new org.bukkit.attribute.AttributeModifier(AGI_SPEED_UUID, "stat_agility_speed", bonus, org.bukkit.attribute.AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            }
        }

        var healthAttr = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.getModifiers().stream().filter(m -> m.getUniqueId().equals(VIT_HEALTH_UUID)).forEach(healthAttr::removeModifier);
            double bonus = vit + Math.min(3, Math.max(0, vit - 10));
            if (bonus != 0) {
                healthAttr.addTransientModifier(new org.bukkit.attribute.AttributeModifier(VIT_HEALTH_UUID, "stat_vitality_health", bonus, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
            }
            if (player.getHealth() > healthAttr.getValue()) {
                player.setHealth(healthAttr.getValue());
            }
        }
    }
}
