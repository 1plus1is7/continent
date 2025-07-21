package me.continent.temperature;

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
        long time = world.getTime();
        boolean isDay = isDaytime(time);
        double base = isDay ? 20.0 : 10.0;
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
}
