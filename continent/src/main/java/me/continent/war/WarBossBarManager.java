package me.continent.war;

import me.continent.kingdom.nation;
import me.continent.kingdom.nationManager;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarBossBarManager {
    private static final Map<String, BossBar> bars = new HashMap<>();

    private static String key(War war, String village) {
        return war.hashCode() + ":" + village.toLowerCase();
    }

    public static void createWar(War war) {
        nation atk = nationManager.getByName(war.getAttacker());
        nation def = nationManager.getByName(war.getDefender());
        if (atk != null && def != null) {
            createBarsFornation(war, atk, def);
            createBarsFornation(war, def, atk);
        } else {
            if (atk != null) createBarsFornation(war, atk, null);
            if (def != null) createBarsFornation(war, def, null);
        }
    }

    private static void createBarsFornation(War war, nation viewer, nation enemy) {
        for (String vName : viewer.getVillages()) {
            createBarForVillage(war, vName, viewer);
        }
        if (enemy != null && enemy.getCapital() != null) {
            createBarForVillage(war, enemy.getCapital(), viewer);
        }
    }

    private static void createBarForVillage(War war, String village, nation viewer) {
        String k = key(war, village);
        BossBar bar = bars.get(k);
        if (bar == null) {
            bar = Bukkit.createBossBar(village + " 코어 HP", BarColor.RED, BarStyle.SEGMENTED_10);
            bar.setProgress(1.0);
            bars.put(k, bar);
        }
        addPlayers(bar, viewer);
    }

    private static void addPlayers(BossBar bar, nation kingdom) {
        for (String vName : kingdom.getVillages()) {
            Village v = VillageManager.getByName(vName);
            if (v == null) continue;
            for (UUID uuid : v.getMembers()) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null && p.isOnline()) {
                    bar.addPlayer(p);
                }
            }
        }
    }

    public static void update(War war, String villageName, int hp) {
        String k = key(war, villageName);
        BossBar bar = bars.get(k);
        if (bar == null) return;
        nation kingdom = nationManager.getByVillage(villageName);
        if (kingdom == null) return;
        int maxHp = WarManager.getInitialHp(kingdom, villageName);
        bar.setTitle(villageName + " 코어 HP: " + Math.max(hp, 0) + "/" + maxHp);
        bar.setProgress(Math.max(0.0, Math.min(1.0, hp / (double) maxHp)));
    }

    public static void remove(War war, String villageName) {
        String k = key(war, villageName);
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
    }
}
