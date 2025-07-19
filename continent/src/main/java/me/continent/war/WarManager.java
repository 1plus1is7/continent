package me.continent.war;

import me.continent.kingdom.Kingdom;
import java.util.HashMap;
import java.util.Map;

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

    public static boolean isAtWar(String kingdom1, String kingdom2) {
        War war = wars.get(kingdom1.toLowerCase());
        if (war == null) return false;
        return war.getAttacker().equalsIgnoreCase(kingdom2) || war.getDefender().equalsIgnoreCase(kingdom2);
    }

    public static void endWar(War war) {
        if (war == null) return;
        wars.remove(war.getAttacker().toLowerCase());
        wars.remove(war.getDefender().toLowerCase());
    }
}
