package me.continent.kingdom;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import java.util.*;

public class KingdomManager {
    private static final Map<String, Kingdom> kingdomsByName = new HashMap<>();
    private static final Map<String, Kingdom> kingdomsByVillage = new HashMap<>();

    public static Kingdom getByName(String name) {
        return kingdomsByName.get(name.toLowerCase());
    }

    public static Kingdom getByVillage(String village) {
        return kingdomsByVillage.get(village.toLowerCase());
    }

    public static Collection<Kingdom> getAll() {
        return kingdomsByName.values();
    }

    public static void register(Kingdom kingdom) {
        kingdomsByName.put(kingdom.getName().toLowerCase(), kingdom);
        for (String v : kingdom.getVillages()) {
            kingdomsByVillage.put(v.toLowerCase(), kingdom);
            Village vil = VillageManager.getByName(v);
            if (vil != null) vil.setKingdom(kingdom.getName());
        }
    }

    public static void addVillage(Kingdom kingdom, Village village) {
        kingdom.getVillages().add(village.getName());
        kingdomsByVillage.put(village.getName().toLowerCase(), kingdom);
        village.setKingdom(kingdom.getName());
        kingdom.addGold(village.getVault());
        village.setVault(0);
    }

    public static void removeVillage(Kingdom kingdom, Village village) {
        kingdom.getVillages().remove(village.getName());
        kingdomsByVillage.remove(village.getName().toLowerCase());
        village.setKingdom(null);
    }

    public static void unregister(Kingdom kingdom) {
        kingdomsByName.remove(kingdom.getName().toLowerCase());
        for (String v : kingdom.getVillages()) {
            kingdomsByVillage.remove(v.toLowerCase());
            Village vil = VillageManager.getByName(v);
            if (vil != null) vil.setKingdom(null);
        }
    }

    public static Kingdom createKingdom(String name, Village capital) {
        if (getByName(name) != null) return null;
        Kingdom kingdom = new Kingdom(name, capital.getKing(), capital);
        register(kingdom);
        kingdom.addGold(capital.getVault());
        capital.setVault(0);
        return kingdom;
    }

    public static boolean renameKingdom(Kingdom kingdom, String newName) {
        if (kingdomsByName.containsKey(newName.toLowerCase())) return false;
        String old = kingdom.getName();
        kingdomsByName.remove(old.toLowerCase());
        kingdom.setName(newName);
        kingdomsByName.put(newName.toLowerCase(), kingdom);
        for (String vName : kingdom.getVillages()) {
            kingdomsByVillage.put(vName.toLowerCase(), kingdom);
            Village v = VillageManager.getByName(vName);
            if (v != null) {
                v.setKingdom(newName);
                me.continent.storage.VillageStorage.save(v);
            }
        }
        KingdomStorage.rename(old, newName);
        KingdomStorage.save(kingdom);
        return true;
    }
}
