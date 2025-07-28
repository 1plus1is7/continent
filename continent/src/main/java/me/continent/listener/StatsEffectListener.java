package me.continent.listener;

import me.continent.player.PlayerDataManager;
import me.continent.stat.PlayerStats;
import me.continent.stat.StatType;
import me.continent.stat.StatsManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class StatsEffectListener implements Listener {
    private final Map<java.util.UUID, Boolean> jumped = new HashMap<>();
    private final Map<java.util.UUID, Long> lastJumpTime = new HashMap<>();
    private final Map<java.util.UUID, Long> lastSprint = new HashMap<>();
    private final Map<java.util.UUID, Long> dashCooldown = new HashMap<>();
    private final Map<java.util.UUID, Long> dodgeCooldown = new HashMap<>();
    private final Map<java.util.UUID, Long> healCooldown = new HashMap<>();
    private final Map<java.util.UUID, Long> smashCooldown = new HashMap<>();

    public StatsEffectListener() {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                    PlayerStats stats = PlayerDataManager.get(p.getUniqueId()).getStats();
                    int vit = stats.get(StatType.VITALITY);
                    if (vit >= 5 && p.getFoodLevel() > 6 && p.getHealth() < p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()) {
                        p.setHealth(Math.min(p.getHealth() + 0.5, p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));
                    }

                    if (vit >= 15) {
                        double max = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                        if (p.getHealth() <= max * 0.2) {
                            long last = healCooldown.getOrDefault(p.getUniqueId(), 0L);
                            if (System.currentTimeMillis() - last > 30000) {
                                healCooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                p.setHealth(Math.min(max, p.getHealth() + max * 0.3));
                                p.playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(me.continent.ContinentPlugin.getInstance(), 20L, 20L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        StatsManager.applyStats(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        org.bukkit.Bukkit.getScheduler().runTask(me.continent.ContinentPlugin.getInstance(), () -> StatsManager.applyStats(event.getPlayer()));
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
        if (stats.get(StatType.STRENGTH) >= 5) {
            applyHaste(player, player.getInventory().getItem(event.getNewSlot()));
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
            if (stats.get(StatType.AGILITY) >= 5) {
                event.getProjectile().setVelocity(event.getProjectile().getVelocity().normalize().multiply(3));
            }
        }
    }

    private void applyHaste(Player player, ItemStack item) {
        if (item != null && (item.getType().name().endsWith("PICKAXE") || item.getType().name().endsWith("AXE"))) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.HASTE, Integer.MAX_VALUE, 0, false, false));
        } else {
            player.removePotionEffect(org.bukkit.potion.PotionEffectType.HASTE);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
        if (stats.get(StatType.AGILITY) >= 10 && player.getGameMode() == GameMode.SURVIVAL) {
            if (player.isOnGround()) {
                player.setAllowFlight(true);
                jumped.put(player.getUniqueId(), false);
            }
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
        if (stats.get(StatType.AGILITY) >= 10 && player.getGameMode() == GameMode.SURVIVAL) {
            if (!jumped.getOrDefault(player.getUniqueId(), false)) {
                long now = System.currentTimeMillis();
                long last = lastJumpTime.getOrDefault(player.getUniqueId(), 0L);
                long cd = 2000;
                int agi = stats.get(StatType.AGILITY);
                if (agi >= 11) cd = 1000;
                if (agi >= 12) cd = 750;
                if (agi >= 13) cd = 500;
                if (now - last < cd) return;

                event.setCancelled(true);
                player.setAllowFlight(false);
                player.setVelocity(player.getLocation().getDirection().multiply(0.1).setY(0.6));
                jumped.put(player.getUniqueId(), true);
                lastJumpTime.put(player.getUniqueId(), now);
            }
        }
    }

    @EventHandler
    public void onToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
        if (stats.get(StatType.AGILITY) >= 10 && player.getGameMode() == GameMode.SURVIVAL && event.isSprinting()) {
            long now = System.currentTimeMillis();
            long lastTap = lastSprint.getOrDefault(player.getUniqueId(), 0L);
            if (now - lastTap < 300) {
                long cd = dashCooldown.getOrDefault(player.getUniqueId(), 0L);
                if (now - cd > 2000) {
                    dashCooldown.put(player.getUniqueId(), now);
                    double speed = 0.8;
                    int agi = stats.get(StatType.AGILITY);
                    if (agi >= 11) speed = 1.0;
                    if (agi >= 12) speed = 1.2;
                    if (agi >= 13) speed = 1.4;
                    player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(speed));
                }
            }
            lastSprint.put(player.getUniqueId(), now);
        }
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
        if (!event.isSneaking() || player.isOnGround()) return;
        if (stats.get(StatType.AGILITY) >= 15 && player.getGameMode() == GameMode.SURVIVAL) {
            long now = System.currentTimeMillis();
            long last = dodgeCooldown.getOrDefault(player.getUniqueId(), 0L);
            if (now - last > 5000) {
                dodgeCooldown.put(player.getUniqueId(), now);
                Vector dir = player.getLocation().getDirection().setY(0).normalize().multiply(-0.8);
                player.setVelocity(dir);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
            int vit = stats.get(StatType.VITALITY);
            int agi = stats.get(StatType.AGILITY);
            if (agi >= 14 && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }
            if (vit >= 14) {
                switch (event.getCause()) {
                    case FIRE, FIRE_TICK, LAVA, POISON, WITHER -> event.setDamage(event.getDamage() * 0.5);
                    default -> {}
                }
            }
            if (vit >= 10 && Math.random() < 0.3) {
                event.setCancelled(true);
                player.sendActionBar("§e[Stat] 피해가 무시되었습니다!");
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
            if (stats.get(StatType.STRENGTH) >= 15) {
                // handled in special ability
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            Material m = event.getItem().getItemStack().getType();
            if (m == Material.DIAMOND || m == Material.EMERALD || m == Material.NETHERITE_SCRAP || m == Material.NETHERITE_INGOT || m == Material.ANCIENT_DEBRIS || m == Material.TOTEM_OF_UNDYING) {
                PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
                if (stats.get(StatType.LUCK) < 10 && Math.random() < 0.1) {
                    stats.set(StatType.LUCK, stats.get(StatType.LUCK) + 1);
                    PlayerDataManager.save(player.getUniqueId());
                    player.sendMessage("§e행운 스탯 +1!");
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.IRON_INGOT) {
            PlayerStats stats = PlayerDataManager.get(player.getUniqueId()).getStats();
            if (stats.get(StatType.STRENGTH) >= 15) {
                long last = smashCooldown.getOrDefault(player.getUniqueId(), 0L);
                if (System.currentTimeMillis() - last > 10000) {
                    smashCooldown.put(player.getUniqueId(), System.currentTimeMillis());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f, 1f);
                    for (Entity e : player.getNearbyEntities(3, 3, 3)) {
                        if (e instanceof LivingEntity le && e != player) {
                            le.damage(6, player);
                            le.setVelocity(le.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.5).setY(0.4));
                        }
                    }
                }
            }
        }
    }
}
