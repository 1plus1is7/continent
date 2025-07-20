package me.continent.season;

import me.continent.ContinentPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;

public class SeasonTreeController {
    static void onSpring() {
        SeasonLeafManager.spawnLeavesFromPiles();
    }

    static void onSummer() {
        Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> {
            for (Location loc : SeasonLeafManager.getLeaves()) {
                Block b = loc.getBlock();
                if (b.getType() == Material.OAK_LEAVES) {
                    Leaves data = (Leaves) Material.OAK_LEAVES.createBlockData();
                    data.setPersistent(true);
                    b.setBlockData(data, false);
                }
            }
        });
    }
}
