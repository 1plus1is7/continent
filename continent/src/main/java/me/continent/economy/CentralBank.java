package me.continent.economy;

public class CentralBank {

    private static double exchangeRate = 20.0; // 초기 환율: 금괴 1개 = 20G
    private static final double minRate = 10.0;
    private static final double maxRate = 40.0;

    private static int recentTrades = 0;
    private static final int tradesPerAdjustment = 10;

    public static double getExchangeRate() {
        return exchangeRate;
    }

    public static void recordExchange() {
        recentTrades++;

        if (recentTrades >= tradesPerAdjustment) {
            adjustExchangeRate();
            recentTrades = 0;
        }
    }

    private static void adjustExchangeRate() {
        double change = (Math.random() * 2) - 1; // -1.0 ~ +1.0
        exchangeRate += change;
        exchangeRate = Math.max(minRate, Math.min(maxRate, exchangeRate));
    }

    public static void setExchangeRate(double rate) {
        exchangeRate = Math.max(minRate, Math.min(maxRate, rate));
    }

    public static void resetExchangeRate() {
        exchangeRate = 20.0;
        recentTrades = 0;
    }
}
