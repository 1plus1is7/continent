package me.continent.scoreboard;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class MapRenderer {

    public static String renderMiniMap(Player player) {
        Chunk center = player.getLocation().getChunk();
        int centerX = center.getX();
        int centerZ = center.getZ();

        StringBuilder builder = new StringBuilder("§f[§eMiniMap§f] ");
        for (int dz = -2; dz <= 2; dz++) {
            for (int dx = -2; dx <= 2; dx++) {
                int x = centerX + dx;
                int z = centerZ + dz;
                Chunk target = player.getWorld().getChunkAt(x, z);

                boolean claimed = VillageManager.isChunkClaimed(target);
                boolean isCurrent = dx == 0 && dz == 0;

                if (isCurrent && claimed) {
                    builder.append("⬤"); // 속찬 원
                } else if (isCurrent) {
                    builder.append("◯"); // 속빈 원
                } else if (claimed) {
                    builder.append("■"); // 속찬 네모
                } else {
                    builder.append("□"); // 속빈 네모
                }
            }
            if (dz < 2) builder.append("\n                §7"); // 줄바꿈 정렬
        }

        return builder.toString();
    }
}