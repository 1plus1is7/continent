package me.continent.war;

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
        createBarForVillage(war, war.getAttacker());
        createBarForVillage(war, war.getDefender());
    }

    private static void createBarForVillage(War war, String villageName) {
        String k = key(war, villageName);
        BossBar bar = bars.get(k);
        if (bar == null) {
            bar = Bukkit.createBossBar(villageName + " 코어 HP", BarColor.RED, BarStyle.SEGMENTED_10);
            bar.setProgress(1.0);
            bars.put(k, bar);
        }
        Village village = VillageManager.getByName(villageName);
        if (village != null) addPlayers(bar, village);
    }

    private static void addPlayers(BossBar bar, Village village) {
        for (UUID uuid : village.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                bar.addPlayer(p);
            }
        }
    }

    public static void update(War war, String villageName, int hp) {
        String k = key(war, villageName);
        BossBar bar = bars.get(k);
        if (bar == null) return;
        Village village = VillageManager.getByName(villageName);
        if (village == null) return;
        int maxHp = WarManager.getInitialHp(village);
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
