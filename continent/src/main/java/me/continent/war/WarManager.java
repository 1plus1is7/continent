package me.continent.war;

import me.continent.kingdom.nation;
import me.continent.kingdom.nationManager;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.village.service.CoreService;
import me.continent.war.WarBossBarManager;
import me.continent.kingdom.nationStorage;
import me.continent.storage.VillageStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.*;

public class WarManager {
    public static final int CAPITAL_CORE_HP = 30;
    public static final int VILLAGE_CORE_HP = 20;
    private static final Map<String, War> wars = new HashMap<>();

    public static War declareWar(nation attacker, nation defender) {
        if (attacker == null || defender == null) return null;
        War war = new War(attacker.getName(), defender.getName());
        wars.put(attacker.getName().toLowerCase(), war);
        wars.put(defender.getName().toLowerCase(), war);
        initCoreHp(war, attacker);
        initCoreHp(war, defender);
        WarBossBarManager.createWar(war);
        return war;
    }

    public static War getWar(String kingdom) {
        return wars.get(kingdom.toLowerCase());
    }

    public static Collection<War> getWars() {
        return new HashSet<>(wars.values());
    }

    public static boolean isAtWar(String kingdom1, String kingdom2) {
        War war = wars.get(kingdom1.toLowerCase());
        if (war == null) return false;
        return war.getAttacker().equalsIgnoreCase(kingdom2) || war.getDefender().equalsIgnoreCase(kingdom2);
    }

    public static void endWar(War war) {
        if (war == null) return;
        wars.remove(war.getAttacker().toLowerCase());
        wars.remove(war.getDefender().toLowerCase());

        // restore captured villages and transfer ownership
        for (Map.Entry<String, String> entry : war.getDestroyedVillages().entrySet()) {
            Village village = VillageManager.getByName(entry.getKey());
            nation capturer = nationManager.getByName(entry.getValue());
            if (village == null || capturer == null) continue;
            if (village.getCoreLocation() != null && village.getCoreLocation().getBlock().getType() != Material.BEACON) {
                CoreService.placeCore(village, village.getCoreLocation());
            }
            if (!capturer.getVillages().contains(village.getName())) {
                nationManager.addVillage(capturer, village);
            }
        }

        war.getBannedPlayers().clear();
        WarBossBarManager.endWar(war);
    }

    public static void surrender(nation loser) {
        if (loser == null) return;
        War war = getWar(loser.getName());
        if (war == null) return;
        String winnerName = war.getAttacker().equalsIgnoreCase(loser.getName())
                ? war.getDefender() : war.getAttacker();
        nation winner = nationManager.getByName(winnerName);

        Set<String> loserVillages = new HashSet<>(loser.getVillages());

        endWar(war);

        nationManager.unregister(loser);

        if (winner != null) {
            for (String vName : loserVillages) {
                Village v = VillageManager.getByName(vName);
                if (v != null && !winner.getVillages().contains(vName)) {
                    nationManager.addVillage(winner, v);
                }
            }
            nationStorage.delete(loser);
            nationStorage.save(winner);
            for (String vName : winner.getVillages()) {
                Village vv = VillageManager.getByName(vName);
                if (vv != null) VillageStorage.save(vv);
            }
        } else {
            nationStorage.delete(loser);
        }
    }

    public static void coreDestroyed(Village village, nation attacker) {
        if (village == null || attacker == null) return;
        War war = getWar(village.getnation());
        if (war == null) return;
        war.addDestroyedVillage(village.getName(), attacker.getName());
        WarBossBarManager.remove(war, village.getName());
        CoreService.removeCore(village);
        Bukkit.broadcastMessage("§c[전쟁] §f" + village.getName() + " 마을의 코어가 파괴되었습니다!");

        nation victim = nationManager.getByName(village.getnation());
        if (victim != null && victim.getCapital() != null
                && victim.getCapital().equalsIgnoreCase(village.getName())) {
            Bukkit.broadcastMessage("§e[전쟁] §f" + victim.getName()
                    + " 국가의 수도가 파괴되어 전쟁이 종료됩니다.");
            surrender(victim);
            return;
        }
    }

    public static boolean isPlayerBanned(UUID uuid) {
        for (War war : wars.values()) {
            if (war.isPlayerBanned(uuid)) return true;
        }
        return false;
    }

    private static void initCoreHp(War war, nation kingdom) {
        if (kingdom == null) return;
        for (String vName : kingdom.getVillages()) {
            war.setCoreHp(vName, getInitialHp(kingdom, vName));
        }
    }

    public static int getInitialHp(nation kingdom, String villageName) {
        if (kingdom == null || villageName == null) return VILLAGE_CORE_HP;
        return villageName.equalsIgnoreCase(kingdom.getCapital()) ? CAPITAL_CORE_HP : VILLAGE_CORE_HP;
    }
}
