package me.continent.war;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.village.service.CoreService;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.*;

public class WarManager {
    private static final Map<String, War> wars = new HashMap<>();

    public static War declareWar(Kingdom attacker, Kingdom defender) {
        if (attacker == null || defender == null) return null;
        War war = new War(attacker.getName(), defender.getName());
        wars.put(attacker.getName().toLowerCase(), war);
        wars.put(defender.getName().toLowerCase(), war);
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
            Kingdom capturer = KingdomManager.getByName(entry.getValue());
            if (village == null || capturer == null) continue;
            if (village.getCoreLocation() != null && village.getCoreLocation().getBlock().getType() != Material.BEACON) {
                CoreService.placeCore(village, village.getCoreLocation());
            }
            if (!capturer.getVillages().contains(village.getName())) {
                KingdomManager.addVillage(capturer, village);
            }
        }

        war.getBannedPlayers().clear();
    }

    public static void coreDestroyed(Village village, Kingdom attacker) {
        if (village == null || attacker == null) return;
        War war = getWar(village.getKingdom());
        if (war == null) return;
        war.addDestroyedVillage(village.getName(), attacker.getName());
        Bukkit.broadcastMessage("§c[전쟁] §f" + village.getName() + " 마을의 코어가 파괴되었습니다!");
    }

    public static boolean isPlayerBanned(UUID uuid) {
        for (War war : wars.values()) {
            if (war.isPlayerBanned(uuid)) return true;
        }
        return false;
    }
}
