package me.continent.scoreboard;

import me.continent.ContinentPlugin;
import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class ScoreboardService {

    public static void update(Player player) {
        ContinentPlugin plugin = ContinentPlugin.getInstance();
        FileConfiguration config = plugin.getConfig();

        UUID uuid = player.getUniqueId();
        String serverName = config.getString("scoreboard.title", "§aContinent Server");
        String serverAddress = config.getString("server-address", "example.com");
        String centerSymbol = config.getString("symbols.center", "▣");
        String chunkSymbol = config.getString("symbols.chunk", "▩");
        String claimedColor = config.getString("colors.claimed", "§a");
        String unclaimedColor = config.getString("colors.unclaimed", "§f");
        int minimapSize = config.getInt("scoreboard.minimap-size", 7);
        boolean showCoordinates = config.getBoolean("scoreboard.show-coordinates", false);

        int half = minimapSize / 2;

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("info", "dummy", serverName);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<String> lines = new ArrayList<>();

        Kingdom kingdom = KingdomManager.getByPlayer(uuid);
        String kingdomName = (kingdom != null) ? kingdom.getName() : "없음";
        lines.add("국가: " + kingdomName);

        double gold = PlayerDataManager.get(uuid).getGold();
        lines.add("골드: " + String.format("%.2f", gold) + "G");

        if (showCoordinates) {
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();
            lines.add("좌표: " + x + ", " + y + ", " + z);
        }

        lines.add("§a[MiniMap]");

        Chunk center = player.getLocation().getChunk();
        int px = center.getX();
        int pz = center.getZ();
        World world = player.getWorld();

        String[][] grid = new String[minimapSize][minimapSize];
        for (int dz = -half; dz <= half; dz++) {
            for (int dx = -half; dx <= half; dx++) {
                int xIdx = dx + half;
                int zIdx = dz + half;

                Chunk chunk = world.getChunkAt(px + dx, pz + dz);
                boolean claimed = KingdomManager.isChunkClaimed(chunk);

                String color = claimed ? claimedColor : unclaimedColor;
                String symbol = (dx == 0 && dz == 0) ? centerSymbol : chunkSymbol;
                grid[zIdx][xIdx] = color + symbol;
            }
        }

        String[][] rotated = switch (getDirection(player.getLocation().getYaw())) {
            case "EAST" -> rotate90(grid);
            case "SOUTH" -> rotate180(grid);
            case "WEST" -> rotate270(grid);
            default -> grid;
        };

        for (String[] row : rotated) {
            StringBuilder line = new StringBuilder();
            for (String s : row) line.append(s).append(" ");
            lines.add(line.toString().trim());
        }

        lines.add("§7" + serverAddress);

        int score = lines.size();
        for (String line : lines) {
            Score s = obj.getScore(line + " §" + score); // 중복 방지용
            s.setScore(score--);
        }

        player.setScoreboard(board);
    }

    public static void schedule() {
        long intervalTicks = (long) (ContinentPlugin.getInstance().getConfig().getDouble("scoreboard.refresh-interval", 0.5) * 20);
        Bukkit.getScheduler().runTaskTimer(ContinentPlugin.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                update(player);
            }
        }, 0L, intervalTicks);
    }

    private static String getDirection(float yaw) {
        yaw = (yaw % 360 + 360) % 360;
        if (yaw >= 45 && yaw < 135) return "EAST";
        if (yaw >= 135 && yaw < 225) return "NORTH";
        if (yaw >= 225 && yaw < 315) return "WEST";
        return "SOUTH";
    }

    private static String[][] rotate90(String[][] grid) {
        int n = grid.length;
        String[][] result = new String[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                result[j][n - 1 - i] = grid[i][j];
        return result;
    }

    private static String[][] rotate180(String[][] grid) {
        int n = grid.length;
        String[][] result = new String[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                result[n - 1 - i][n - 1 - j] = grid[i][j];
        return result;
    }

    private static String[][] rotate270(String[][] grid) {
        int n = grid.length;
        String[][] result = new String[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                result[n - 1 - j][i] = grid[i][j];
        return result;
    }
}