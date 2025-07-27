package me.continent.economy;

import me.continent.ContinentPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CentralBankDataManager {

    private static File file;
    private static YamlConfiguration config;

    public static void load() {
        file = new File(ContinentPlugin.getInstance().getDataFolder(), "centralbank.yml");
        if (!file.exists()) {
            ContinentPlugin.getInstance().saveResource("centralbank.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
        CentralBank.setGold(config.getInt("gold", 0));
        CentralBank.setDiamond(config.getInt("diamond", 300));
        CentralBank.setBaseRate(config.getDouble("baseRate", 20.0));
        CentralBank.setDiamondBaseRate(config.getDouble("diamondBaseRate", 200.0));
        CentralBank.setMinRate(config.getDouble("minRate", 10.0));
        CentralBank.setDiamondMinRate(config.getDouble("diamondMinRate", 100.0));
        CentralBank.setMaxRate(config.getDouble("maxRate", 50.0));
        CentralBank.setDiamondMaxRate(config.getDouble("diamondMaxRate", 500.0));
        CentralBank.setExchangeRate(config.getDouble("lastRate", 20.0));
        CentralBank.setDiamondExchangeRate(config.getDouble("diamondLastRate", 200.0));
        CentralBank.setPriceIndex(config.getDouble("priceIndex", 1.0));
        CentralBank.setGrowthIndex(config.getDouble("growthIndex", 1.0));
        CentralBank.setSmoothing(config.getDouble("smoothing", 0.2));
        CentralBank.setAutoRate(config.getBoolean("autoRate", true));
    }

    public static void save() {
        config.set("gold", CentralBank.getGold());
        config.set("diamond", CentralBank.getDiamond());
        config.set("baseRate", CentralBank.getBaseRate());
        config.set("diamondBaseRate", CentralBank.getDiamondBaseRate());
        config.set("minRate", CentralBank.getMinRate());
        config.set("diamondMinRate", CentralBank.getDiamondMinRate());
        config.set("maxRate", CentralBank.getMaxRate());
        config.set("diamondMaxRate", CentralBank.getDiamondMaxRate());
        config.set("lastRate", CentralBank.getExchangeRate());
        config.set("diamondLastRate", CentralBank.getDiamondExchangeRate());
        config.set("priceIndex", CentralBank.getPriceIndex());
        config.set("growthIndex", CentralBank.getGrowthIndex());
        config.set("smoothing", CentralBank.getSmoothing());
        config.set("autoRate", CentralBank.isAutoRate());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
