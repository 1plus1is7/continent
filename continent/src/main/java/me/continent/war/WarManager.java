package me.continent.war;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.nation.service.CoreService;
import me.continent.storage.NationStorage;
import me.continent.war.CoreSlimeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.*;

public class WarManager {
    public static final int VILLAGE_CORE_HP = 250;
    private static final Map<String, War> wars = new HashMap<>();

    public static War declareWar(Nation attacker, Nation defender) {
        if (attacker == null || defender == null) return null;
        War war = new War(attacker.getName(), defender.getName());
        wars.put(attacker.getName().toLowerCase(), war);
        wars.put(defender.getName().toLowerCase(), war);
        initCoreHp(war, attacker);
        initCoreHp(war, defender);
        WarBossBarManager.createWar(war);
        CoreSlimeManager.createWar(war);
        return war;
    }

    public static War getWar(String nation) {
        return wars.get(nation.toLowerCase());
    }

    public static Collection<War> getWars() {
        return new HashSet<>(wars.values());
    }

    public static boolean isAtWar(String nation1, String nation2) {
        War war = wars.get(nation1.toLowerCase());
        if (war == null) return false;
        return war.getAttacker().equalsIgnoreCase(nation2) || war.getDefender().equalsIgnoreCase(nation2);
    }

    public static void endWar(War war) {
        if (war == null) return;
        wars.remove(war.getAttacker().toLowerCase());
        wars.remove(war.getDefender().toLowerCase());

        // restore core blocks for destroyed nations
        for (String vName : war.getDestroyedNations().keySet()) {
            Nation v = NationManager.getByName(vName);
            if (v != null && v.getCoreLocation() != null
                    && v.getCoreLocation().getBlock().getType() != Material.BEACON) {
                CoreService.placeCore(v, v.getCoreLocation());
            }
            if (v != null) NationStorage.save(v);
        }

        war.getBannedPlayers().clear();
        WarBossBarManager.endWar(war);
        CoreSlimeManager.endWar(war);

        String msg = "§e[전쟁] §f" + war.getAttacker() + " 국가과 "
                + war.getDefender() + " 국가의 전쟁이 종료되었습니다.";
        Bukkit.broadcastMessage(msg);
    }

    public static void surrender(Nation loser) {
        if (loser == null) return;
        War war = getWar(loser.getName());
        if (war == null) return;
        endWar(war);
        String winner = war.getAttacker().equalsIgnoreCase(loser.getName())
                ? war.getDefender() : war.getAttacker();
        Bukkit.broadcastMessage("§e[전쟁] §f" + loser.getName() + " 국가이 항복했습니다. 승자는 " + winner + " 국가입니다.");
    }

    public static void coreDestroyed(Nation nation, Nation attacker) {
        if (nation == null || attacker == null) return;
        War war = getWar(nation.getName());
        if (war == null) return;
        war.addDestroyedNation(nation.getName(), attacker.getName());
        WarBossBarManager.remove(war, nation.getName());
        CoreSlimeManager.remove(war, nation.getName());
        CoreService.removeCore(nation);
        Bukkit.broadcastMessage("§c[전쟁] §f" + nation.getName() + " 국가의 코어가 파괴되었습니다!");
        surrender(nation);
    }

    public static boolean isPlayerBanned(UUID uuid) {
        for (War war : wars.values()) {
            if (war.isPlayerBanned(uuid)) return true;
        }
        return false;
    }

    private static void initCoreHp(War war, Nation nation) {
        if (nation == null) return;
        war.setCoreHp(nation.getName(), getInitialHp(nation));
    }

    public static int getInitialHp(Nation nation) {
        return VILLAGE_CORE_HP;
    }

    public static void damageCore(Nation nation, Nation attacker) {
        if (nation == null || attacker == null) return;
        War war = getWar(nation.getName());
        if (war == null) return;
        int hp = war.getCoreHp(nation.getName());
        if (hp <= 0) {
            hp = getInitialHp(nation);
        }
        hp--;
        war.setCoreHp(nation.getName(), hp);
        WarBossBarManager.update(war, nation.getName(), hp);
        if (hp <= 0) {
            WarBossBarManager.remove(war, nation.getName());
            coreDestroyed(nation, attacker);
        }
    }
}
