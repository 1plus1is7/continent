package me.continent.war;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.village.service.CoreService;
import me.continent.storage.VillageStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.*;

public class WarManager {
    public static final int VILLAGE_CORE_HP = 20;
    private static final Map<String, War> wars = new HashMap<>();

    public static War declareWar(Village attacker, Village defender) {
        if (attacker == null || defender == null) return null;
        War war = new War(attacker.getName(), defender.getName());
        wars.put(attacker.getName().toLowerCase(), war);
        wars.put(defender.getName().toLowerCase(), war);
        initCoreHp(war, attacker);
        initCoreHp(war, defender);
        WarBossBarManager.createWar(war);
        return war;
    }

    public static War getWar(String village) {
        return wars.get(village.toLowerCase());
    }

    public static Collection<War> getWars() {
        return new HashSet<>(wars.values());
    }

    public static boolean isAtWar(String village1, String village2) {
        War war = wars.get(village1.toLowerCase());
        if (war == null) return false;
        return war.getAttacker().equalsIgnoreCase(village2) || war.getDefender().equalsIgnoreCase(village2);
    }

    public static void endWar(War war) {
        if (war == null) return;
        wars.remove(war.getAttacker().toLowerCase());
        wars.remove(war.getDefender().toLowerCase());

        // restore core blocks for destroyed villages
        for (String vName : war.getDestroyedVillages().keySet()) {
            Village v = VillageManager.getByName(vName);
            if (v != null && v.getCoreLocation() != null
                    && v.getCoreLocation().getBlock().getType() != Material.BEACON) {
                CoreService.placeCore(v, v.getCoreLocation());
            }
            if (v != null) VillageStorage.save(v);
        }

        war.getBannedPlayers().clear();
        WarBossBarManager.endWar(war);

        String msg = "§e[전쟁] §f" + war.getAttacker() + " 마을과 "
                + war.getDefender() + " 마을의 전쟁이 종료되었습니다.";
        Bukkit.broadcastMessage(msg);
    }

    public static void surrender(Village loser) {
        if (loser == null) return;
        War war = getWar(loser.getName());
        if (war == null) return;
        endWar(war);
        String winner = war.getAttacker().equalsIgnoreCase(loser.getName())
                ? war.getDefender() : war.getAttacker();
        Bukkit.broadcastMessage("§e[전쟁] §f" + loser.getName() + " 마을이 항복했습니다. 승자는 " + winner + " 마을입니다.");
    }

    public static void coreDestroyed(Village village, Village attacker) {
        if (village == null || attacker == null) return;
        War war = getWar(village.getName());
        if (war == null) return;
        war.addDestroyedVillage(village.getName(), attacker.getName());
        WarBossBarManager.remove(war, village.getName());
        CoreService.removeCore(village);
        Bukkit.broadcastMessage("§c[전쟁] §f" + village.getName() + " 마을의 코어가 파괴되었습니다!");
        surrender(village);
    }

    public static boolean isPlayerBanned(UUID uuid) {
        for (War war : wars.values()) {
            if (war.isPlayerBanned(uuid)) return true;
        }
        return false;
    }

    private static void initCoreHp(War war, Village village) {
        if (village == null) return;
        war.setCoreHp(village.getName(), getInitialHp(village));
    }

    public static int getInitialHp(Village village) {
        return VILLAGE_CORE_HP;
    }

    public static void damageCore(Village village, Village attacker) {
        if (village == null || attacker == null) return;
        War war = getWar(village.getName());
        if (war == null) return;
        int hp = war.getCoreHp(village.getName());
        if (hp <= 0) {
            hp = getInitialHp(village);
        }
        hp--;
        war.setCoreHp(village.getName(), hp);
        WarBossBarManager.update(war, village.getName(), hp);
        if (hp <= 0) {
            WarBossBarManager.remove(war, village.getName());
            coreDestroyed(village, attacker);
        }
    }
}
