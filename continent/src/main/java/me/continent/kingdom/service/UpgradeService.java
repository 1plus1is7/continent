package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.storage.KingdomStorage;
import org.bukkit.configuration.file.FileConfiguration;

public class UpgradeService {
    private static int requiredMembers;
    private static double requiredTreasury;

    public static void init(FileConfiguration config) {
        requiredMembers = config.getInt("upgrade.required-members", 5);
        requiredTreasury = config.getDouble("upgrade.required-treasury", 100.0);
    }

    public static int getRequiredMembers() { return requiredMembers; }
    public static double getRequiredTreasury() { return requiredTreasury; }

    public static boolean upgrade(Kingdom kingdom) {
        if (kingdom.isNation()) return false;
        if (kingdom.getMembers().size() < requiredMembers) return false;
        if (kingdom.getTreasury() < requiredTreasury) return false;
        kingdom.removeGold(requiredTreasury);
        kingdom.setNation(true);
        KingdomStorage.save(kingdom);
        return true;
    }
}
