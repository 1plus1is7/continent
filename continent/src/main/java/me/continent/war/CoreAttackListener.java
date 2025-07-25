package me.continent.war;

import me.continent.ContinentPlugin;
import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Slime;
import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoreAttackListener implements Listener {
    private final Map<String, BukkitTask> alertTasks = new HashMap<>();
    private final Map<String, Long> lastAttack = new HashMap<>();

    private void startAlert(Nation nation) {
        String key = nation.getName().toLowerCase();
        lastAttack.put(key, System.currentTimeMillis());
        if (alertTasks.containsKey(key)) return;

        Runnable alert = () -> {
            War war = WarManager.getWar(nation.getName());
            if (war == null) {
                cancel(key);
                return;
            }
            if (nation.getCoreLocation() == null || nation.getCoreLocation().getBlock().getType() != Material.BEACON) {
                cancel(key);
                return;
            }
            Long last = lastAttack.get(key);
            if (last == null || System.currentTimeMillis() - last > 5000) {
                cancel(key);
                return;
            }
            sendAlert(nation.getName());
        };

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(ContinentPlugin.getInstance(), alert, 0L, 100L);
        alertTasks.put(key, task);
    }

    private void cancel(String key) {
        BukkitTask task = alertTasks.remove(key);
        if (task != null) task.cancel();
        lastAttack.remove(key);
    }

    private void sendAlert(String nationName) {
        Nation nation = NationManager.getByName(nationName);
        if (nation == null) return;
        for (UUID uuid : nation.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);
            if (member == null || !member.isOnline()) continue;
            member.showTitle(Title.title(
                    Component.text("§c경고"),
                    Component.text(nationName + " 코어가 공격받고 있습니다!"),
                    Title.Times.times(Duration.ofMillis(250), Duration.ofMillis(1000), Duration.ofMillis(250))
            ));
            member.playSound(member.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
        }
    }


    @EventHandler
    public void onSlimeDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Slime slime)) return;
        String tag = slime.getScoreboardTags().stream()
                .filter(t -> t.startsWith("core_slime:"))
                .findFirst().orElse(null);
        if (tag == null) return;
        event.setCancelled(true);
        Nation nation = NationManager.getByName(tag.substring("core_slime:".length()));
        if (nation == null) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        Nation attackerNation = NationManager.getByPlayer(attacker.getUniqueId());
        if (attackerNation == null) return;
        if (!WarManager.isAtWar(attackerNation.getName(), nation.getName())) return;
        lastAttack.put(nation.getName().toLowerCase(), System.currentTimeMillis());
        startAlert(nation);
        int dmg = (int) Math.round(event.getFinalDamage());
        if (dmg < 1) dmg = 1;
        for (int i = 0; i < dmg; i++) {
            WarManager.damageCore(nation, attackerNation);
        }
    }
}
