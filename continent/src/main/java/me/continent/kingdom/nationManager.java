package me.continent.kingdom;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import java.util.*;

public class nationManager {
    private static final Map<String, nation> kingdomsByName = new HashMap<>();
    private static final Map<String, nation> kingdomsByVillage = new HashMap<>();

    public static nation getByName(String name) {
        return kingdomsByName.get(name.toLowerCase());
    }

    public static nation getByVillage(String village) {
        return kingdomsByVillage.get(village.toLowerCase());
    }

    public static Collection<nation> getAll() {
        return kingdomsByName.values();
    }

    public static void register(nation kingdom) {
        kingdomsByName.put(kingdom.getName().toLowerCase(), kingdom);
        for (String v : kingdom.getVillages()) {
            kingdomsByVillage.put(v.toLowerCase(), kingdom);
            Village vil = VillageManager.getByName(v);
            if (vil != null) vil.setnation(kingdom.getName());
        }
    }

    public static void addVillage(nation kingdom, Village village) {
        kingdom.getVillages().add(village.getName());
        kingdomsByVillage.put(village.getName().toLowerCase(), kingdom);
        village.setnation(kingdom.getName());
        kingdom.addGold(village.getVault());
        village.setVault(0);
    }

    public static void removeVillage(nation kingdom, Village village) {
        kingdom.getVillages().remove(village.getName());
        kingdomsByVillage.remove(village.getName().toLowerCase());
        village.setnation(null);
    }

    public static void unregister(nation kingdom) {
        kingdomsByName.remove(kingdom.getName().toLowerCase());
        for (String v : kingdom.getVillages()) {
            kingdomsByVillage.remove(v.toLowerCase());
            Village vil = VillageManager.getByName(v);
            if (vil != null) vil.setnation(null);
        }
    }

    public static nation createnation(String name, Village capital) {
        if (getByName(name) != null) return null;
        nation kingdom = new nation(name, capital.getKing(), capital);
        register(kingdom);
        kingdom.addGold(capital.getVault());
        capital.setVault(0);
        return kingdom;
    }

    public static boolean renamenation(nation kingdom, String newName) {
        if (kingdomsByName.containsKey(newName.toLowerCase())) return false;
        String old = kingdom.getName();
        kingdomsByName.remove(old.toLowerCase());
        kingdom.setName(newName);
        kingdomsByName.put(newName.toLowerCase(), kingdom);
        for (String vName : kingdom.getVillages()) {
            kingdomsByVillage.put(vName.toLowerCase(), kingdom);
            Village v = VillageManager.getByName(vName);
            if (v != null) {
                v.setnation(newName);
                me.continent.storage.VillageStorage.save(v);
            }
        }
        nationStorage.rename(old, newName);
        nationStorage.save(kingdom);
        return true;
    }
}
