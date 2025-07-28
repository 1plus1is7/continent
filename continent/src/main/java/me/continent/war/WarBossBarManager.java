package me.continent.war;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Location;

import me.continent.ContinentPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarBossBarManager {
    private static final Map<String, BossBar> bars = new HashMap<>();
    private static BukkitTask task;

    private static String key(War war, String nation) {
        return war.hashCode() + ":" + nation.toLowerCase();
    }

    public static void createWar(War war) {
        createBarForNation(war, war.getAttacker());
        createBarForNation(war, war.getDefender());
        startTask();
    }

    private static void createBarForNation(War war, String nationName) {
        String k = key(war, nationName);
        BossBar bar = bars.get(k);
        if (bar == null) {
            bar = Bukkit.createBossBar(nationName + " 코어 HP", BarColor.RED, BarStyle.SEGMENTED_10);
            bar.setProgress(1.0);
            bars.put(k, bar);
        }
        Nation nation = NationManager.getByName(nationName);
        if (nation != null) addPlayers(bar, nation);
    }

    private static void startTask() {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskTimer(ContinentPlugin.getInstance(), WarBossBarManager::tick, 0L, 20L);
    }

    private static void stopTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private static void tick() {
        for (Map.Entry<String, BossBar> entry : bars.entrySet()) {
            String nationName = entry.getKey().substring(entry.getKey().indexOf(":") + 1);
            Nation nation = NationManager.getByName(nationName);
            if (nation == null) continue;
            Location core = nation.getCoreLocation();
            if (core == null) continue;
            BossBar bar = entry.getValue();
            for (Player p : Bukkit.getOnlinePlayers()) {
                bar.addPlayer(p);
            }
        }
    }

    private static void addPlayers(BossBar bar, Nation nation) {
        for (UUID uuid : nation.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                bar.addPlayer(p);
            }
        }
    }

    public static void update(War war, String nationName, int hp) {
        String k = key(war, nationName);
        BossBar bar = bars.get(k);
        if (bar == null) return;
        Nation nation = NationManager.getByName(nationName);
        if (nation == null) return;
        int maxHp = WarManager.getInitialHp(nation);
        bar.setTitle(nationName + " 코어 HP: " + Math.max(hp, 0) + "/" + maxHp);
        bar.setProgress(Math.max(0.0, Math.min(1.0, hp / (double) maxHp)));
    }

    public static void remove(War war, String nationName) {
        String k = key(war, nationName);
        BossBar bar = bars.remove(k);
        if (bar != null) {
            bar.removeAll();
        }
    }

    public static void endWar(War war) {
        String prefix = war.hashCode() + ":";
        bars.entrySet().removeIf(e -> {
            if (e.getKey().startsWith(prefix)) {
                e.getValue().removeAll();
                return true;
            }
            return false;
        });
        if (bars.isEmpty()) {
            stopTask();
        }
    }
}
