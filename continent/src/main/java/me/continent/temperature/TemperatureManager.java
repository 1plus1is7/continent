package me.continent.temperature;

import me.continent.season.Season;
import me.continent.season.SeasonManager;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Calculates real-time temperature based on the current season and time of day.
 */
public class TemperatureManager {

    /**
     * Returns the current temperature for the given world in degrees Celsius.
     */
    public static double getCurrentTemperature(World world) {
        Season season = SeasonManager.getCurrentSeason();
        long time = world.getTime();
        boolean isDay = isDaytime(time);
        double base = getBaseTemperature(season, isDay);
        double weight = getTimeWeight(time);
        return base + weight;
    }

    /**
     * Convenience overload for player based queries.
     */
    public static double getCurrentTemperature(Player player) {
        return getCurrentTemperature(player.getWorld());
    }

    /**
     * Returns the average temperature for the given season and time of day.
     */
    public static double getBaseTemperature(Season season, boolean isDaytime) {
        return switch (season) {
            case SPRING -> avg(isDaytime ? 10 : 4, isDaytime ? 18 : 10);
            case SUMMER -> avg(isDaytime ? 24 : 20, isDaytime ? 35 : 27);
            case AUTUMN -> avg(isDaytime ? 12 : 5, isDaytime ? 22 : 12);
            case WINTER -> avg(isDaytime ? -5 : -15, isDaytime ? 5 : -3);
        };
    }

    /**
     * Returns the temperature weight based on the world's current time.
     */
    public static double getTimeWeight(World world) {
        return getTimeWeight(world.getTime());
    }

    private static double getTimeWeight(long time) {
        long t = (time + 6000) % 24000; // align 0 to midnight
        int hour = (int) (t / 1000); // 0-23
        if (hour < 5) return -4.0;
        if (hour < 9) return -2.0;
        if (hour < 12) return 0.0;
        if (hour < 16) return 2.0;
        if (hour < 19) return 0.0;
        if (hour < 22) return -2.0;
        return -3.0;
    }

    private static boolean isDaytime(long time) {
        long t = time % 24000;
        return t < 12000;
    }

    private static double avg(double min, double max) {
        return (min + max) / 2.0;
    }
}
