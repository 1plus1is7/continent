package me.continent.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Utility to classify player's shelter level based on surroundings.
 */
public enum ShelterLevel {
    OUTDOOR,
    SEMI_OUTDOOR,
    INDOOR;

    /**
     * Calculates the shelter level for the given player.
     */
    public static ShelterLevel getShelterLevel(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();

        int overhead = 0;
        for (int i = 1; i <= 5; i++) {
            Block b = world.getBlockAt(loc.getBlockX(), loc.getBlockY() + i, loc.getBlockZ());
            if (b.getType() != Material.AIR && b.getType().isOccluding()) {
                overhead++;
            }
        }

        int enclosure = 0;
        if (isBlocking(world.getBlockAt(loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ()))) enclosure++;
        if (isBlocking(world.getBlockAt(loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ()))) enclosure++;
        if (isBlocking(world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1))) enclosure++;
        if (isBlocking(world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1))) enclosure++;
        if (isBlocking(world.getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()))) enclosure++;
        if (isBlocking(world.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()))) enclosure++;

        int surfaceDiff = world.getHighestBlockYAt(loc) - loc.getBlockY();

        if (overhead >= 4 && enclosure >= 4) {
            return INDOOR;
        }
        if ((overhead >= 2 && enclosure >= 2) || surfaceDiff >= 5) {
            return SEMI_OUTDOOR;
        }
        return OUTDOOR;
    }

    private static boolean isBlocking(Block block) {
        Material type = block.getType();
        return type != Material.AIR && type.isOccluding();
    }
}
