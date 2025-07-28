package me.continent.economy;

import org.bukkit.Bukkit;

/**
 * Central bank handling mineral reserves and exchange rate logic.
 */
public class CentralBank {

    private static int gold = 1000;

    private static int diamond = 300;

    private static double baseRate = 20.0;
    private static double minRate = 10.0;
    private static double maxRate = 50.0;

    private static double diamondBaseRate = baseRate * 10;
    private static double diamondMinRate = minRate * 10;
    private static double diamondMaxRate = maxRate * 10;

    private static double lastRate = 20.0;

    private static double lastDiamondRate = diamondBaseRate;

    private static int goldTradeVolume = 0;
    private static int diamondTradeVolume = 0;

    private static double priceIndex = 1.0;
    private static double growthIndex = 1.0;
    private static double smoothing = 0.2;

    private static double prevRate = lastRate;
    private static double prevDiamondRate = lastDiamondRate;

    private static boolean autoRate = true;

    public static int getGold() {
        return gold;
    }

    public static int getDiamond() {
        return diamond;
    }

    public static void setGold(int amount) {
        gold = Math.max(0, amount);
        updateRate();
    }

    public static void setDiamond(int amount) {
        diamond = Math.max(0, amount);
        updateRate();
    }

    public static void addGold(int amount) {
        gold = Math.max(0, gold + amount);
        goldTradeVolume += Math.abs(amount);
        updateRate();
    }

    public static void addDiamond(int amount) {
        diamond = Math.max(0, diamond + amount);
        diamondTradeVolume += Math.abs(amount);
        updateRate();
    }

    public static boolean withdrawGold(int amount) {
        if (gold < amount) return false;
        gold -= amount;
        goldTradeVolume += Math.abs(amount);
        updateRate();
        return true;
    }

    public static boolean withdrawDiamond(int amount) {
        if (diamond < amount) return false;
        diamond -= amount;
        diamondTradeVolume += Math.abs(amount);
        updateRate();
        return true;
    }

    /**
     * Returns the current exchange rate. When autoRate is enabled the value is
     * recalculated whenever queried or the gold reserve changes.
     */
    public static double getExchangeRate() {
        if (autoRate) {
            updateRate();
        }
        return lastRate;
    }

    public static double getDiamondExchangeRate() {
        if (autoRate) {
            updateRate();
        }
        return lastDiamondRate;
    }

    public static int getGoldTradeVolume() { return goldTradeVolume; }
    public static int getDiamondTradeVolume() { return diamondTradeVolume; }

    public static double getPriceIndex() { return priceIndex; }
    public static void setPriceIndex(double val) { priceIndex = Math.max(0.1, val); updateRate(); }

    public static double getGrowthIndex() { return growthIndex; }
    public static void setGrowthIndex(double val) { growthIndex = Math.max(0.1, val); updateRate(); }

    public static double getSmoothing() { return smoothing; }
    public static void setSmoothing(double val) { smoothing = Math.max(0.0, Math.min(1.0, val)); }

    /**
     * Force set the exchange rate and disable clamping logic if desired.
     */
    public static void setExchangeRate(double rate) {
        lastRate = clamp(rate);
    }

    public static void setDiamondExchangeRate(double rate) {
        lastDiamondRate = clampDiamond(rate);
    }

    public static void setAutoRate(boolean enabled) {
        autoRate = enabled;
        if (autoRate) updateRate();
    }

    public static void applyPolicy(double modifier) {
        baseRate *= modifier;
        diamondBaseRate *= modifier;
        updateRate();
    }

    public static boolean isAutoRate() {
        return autoRate;
    }

    public static double getBaseRate() {
        return baseRate;
    }

    public static double getDiamondBaseRate() {
        return diamondBaseRate;
    }

    public static double getMinRate() {
        return minRate;
    }

    public static double getDiamondMinRate() {
        return diamondMinRate;
    }

    public static double getMaxRate() {
        return maxRate;
    }

    public static double getDiamondMaxRate() {
        return diamondMaxRate;
    }

    public static void setBaseRate(double rate) {
        baseRate = rate;
        updateRate();
    }

    public static void setDiamondBaseRate(double rate) {
        diamondBaseRate = rate;
        updateRate();
    }

    public static void setMinRate(double rate) {
        minRate = rate;
        updateRate();
    }

    public static void setDiamondMinRate(double rate) {
        diamondMinRate = rate;
        updateRate();
    }

    public static void setMaxRate(double rate) {
        maxRate = rate;
        updateRate();
    }

    public static void setDiamondMaxRate(double rate) {
        diamondMaxRate = rate;
        updateRate();
    }

    /**
     * Recalculate the exchange rate based on current reserves using the
     * prescribed formula.
     */
    private static void updateRate() {
        if (!autoRate) return;

        double supply = Math.pow(1000.0 / (gold + 1), 0.1);
        double demand = 1.0 + goldTradeVolume / 1000.0;
        double target = baseRate * priceIndex * growthIndex * supply * demand;
        double newRate = lastRate * (1 - smoothing) + target * smoothing;
        prevRate = lastRate;
        lastRate = clamp(newRate);

        double dSupply = Math.pow(300.0 / (diamond + 1), 0.1);
        double dDemand = 1.0 + diamondTradeVolume / 1000.0;
        double dTarget = diamondBaseRate * priceIndex * growthIndex * dSupply * dDemand;
        double newDRate = lastDiamondRate * (1 - smoothing) + dTarget * smoothing;
        prevDiamondRate = lastDiamondRate;
        lastDiamondRate = clampDiamond(newDRate);

        goldTradeVolume = 0;
        diamondTradeVolume = 0;

        if (Math.abs(prevRate - lastRate) >= 0.01 || Math.abs(prevDiamondRate - lastDiamondRate) >= 0.01) {
            notifyRateChange();
        }
    }

    private static double clamp(double rate) {
        if (rate < minRate) return minRate;
        if (rate > maxRate) return maxRate;
        return rate;
    }

    private static double clampDiamond(double rate) {
        if (rate < diamondMinRate) return diamondMinRate;
        if (rate > diamondMaxRate) return diamondMaxRate;
        return rate;
    }

    private static void notifyRateChange() {
        org.bukkit.Bukkit.broadcastMessage("§6[환율] §f중앙은행 환율이 갱신되었습니다. 금: §e" +
                String.format("%.2f", lastRate) + "C, 다이아: §e" + String.format("%.2f", lastDiamondRate) + "C");
    }

    /** Resets rate and recent state to defaults and enables auto calculation. */
    public static void resetExchangeRate() {
        lastRate = baseRate;
        lastDiamondRate = diamondBaseRate;
        autoRate = true;
        updateRate();
    }
}
