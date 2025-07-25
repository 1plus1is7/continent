package me.continent.war;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Slime;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages invisible slime entities used to track core damage during war.
 */
public class CoreSlimeManager {
    private static final Map<String, Slime> slimes = new HashMap<>();

    private static String key(War war, String nation) {
        return war.hashCode() + ":" + nation.toLowerCase();
    }

    public static void createWar(War war) {
        spawnSlime(war, war.getAttacker());
        spawnSlime(war, war.getDefender());
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
        Slime slime = (Slime) world.spawnEntity(loc.clone().add(0.5, 1, 0.5), EntityType.SLIME);
        slime.setAI(false);
        slime.setInvisible(true);
        slime.setCollidable(false);
        slime.setGravity(false);
        slime.setSize(1);
        slime.setSilent(true);
        slime.setPersistent(true);
        slime.setRemoveWhenFarAway(false);
        slime.addScoreboardTag("core_slime:" + nationName.toLowerCase());
        slimes.put(k, slime);
    }

    public static void remove(War war, String nationName) {
        Slime slime = slimes.remove(key(war, nationName));
        if (slime != null && !slime.isDead()) {
            slime.remove();
        }
    }

    public static void endWar(War war) {
        remove(war, war.getAttacker());
        remove(war, war.getDefender());
    }
}
