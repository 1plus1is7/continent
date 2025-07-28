package me.continent.war;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.ContinentPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Slime;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages invisible slime entities used to track core damage during war.
 */
public class CoreSlimeManager {
    private static final Map<String, Slime> slimes = new HashMap<>();
    private static BukkitTask task;

    private static String key(War war, String nation) {
        return war.hashCode() + ":" + nation.toLowerCase();
    }

    public static void createWar(War war) {
        spawnSlime(war, war.getAttacker());
        spawnSlime(war, war.getDefender());
        startTask();
    }

    private static void spawnSlime(War war, String nationName) {
        String k = key(war, nationName);
        if (slimes.containsKey(k)) return;
        Nation nation = NationManager.getByName(nationName);
        if (nation == null) return;
        Location loc = nation.getCoreLocation();
        if (loc == null) return;
        World world = loc.getWorld();
        if (world == null) return;
        // Spawn the slime directly above the core center
        Slime slime = (Slime) world.spawnEntity(loc.clone().add(0, 0, 0), EntityType.SLIME);
        slime.setAI(false);
        slime.setInvisible(true);
        slime.setGlowing(true);
        slime.setCollidable(false);
        slime.setGravity(false);
        slime.setSize(2);
        slime.setSilent(true);
        slime.setPersistent(true);
        slime.setRemoveWhenFarAway(false);
        slime.setInvulnerable(false);
        slime.addScoreboardTag("core_slime:" + nationName.toLowerCase());
        slimes.put(k, slime);
    }

    public static void remove(War war, String nationName) {
        Slime slime = slimes.remove(key(war, nationName));
        if (slime != null && !slime.isDead()) {
            slime.remove();
        }
        if (slimes.isEmpty()) stopTask();
    }

    public static void endWar(War war) {
        remove(war, war.getAttacker());
        remove(war, war.getDefender());
        if (slimes.isEmpty()) stopTask();
    }

    private static void startTask() {
        if (task != null) return;
        task = org.bukkit.Bukkit.getScheduler().runTaskTimer(
                ContinentPlugin.getInstance(),
                CoreSlimeManager::tick, 20L, 20L);
    }

    private static void stopTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private static void tick() {
        slimes.forEach((k, slime) -> {
            if (slime == null || slime.isDead()) return;
            String nationName = k.substring(k.indexOf(":") + 1);
            Nation nation = NationManager.getByName(nationName);
            if (nation == null) return;
            Location loc = nation.getCoreLocation();
            if (loc == null) return;
            slime.teleport(loc.clone().add(0, 0, 0));
        });
    }
}
