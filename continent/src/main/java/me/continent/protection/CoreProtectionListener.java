package me.continent.protection;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.war.WarManager;
import me.continent.war.WarBossBarManager;
import me.continent.war.War;
import me.continent.village.service.CoreService;
import me.continent.kingdom.nationManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CoreProtectionListener implements Listener {

    private boolean isCoreBlock(Block block) {
        Location loc = block.getLocation();
        for (Village village : VillageManager.getAll()) {
            Location core = village.getCoreLocation();
            if (core == null) continue;
            if (core.getWorld().equals(loc.getWorld())
                    && core.getBlockX() == loc.getBlockX()
                    && core.getBlockY() == loc.getBlockY()
                    && core.getBlockZ() == loc.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isCoreBlock(event.getBlock())) {
            Village owner = VillageManager.getByChunk(event.getBlock().getChunk());
            if (owner == null) {
                event.setCancelled(true);
                return;
            }
            Village attackerVillage = VillageManager.getByPlayer(event.getPlayer().getUniqueId());
            boolean allowed = attackerVillage != null
                    && owner.getnation() != null
                    && attackerVillage.getnation() != null
                    && WarManager.isAtWar(owner.getnation(), attackerVillage.getnation())
                    && !owner.getnation().equalsIgnoreCase(attackerVillage.getnation());
            if (!allowed) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§c코어는 명령어로만 제거할 수 있습니다.");
            } else {
                War war = WarManager.getWar(owner.getnation());
                if (war != null) {
                    int hp = war.getCoreHp(owner.getName());
                    if (hp <= 0) {
                        hp = WarManager.getInitialHp(nationManager.getByName(owner.getnation()), owner.getName());
                    }
                    hp--;
                    war.setCoreHp(owner.getName(), hp);
                    WarBossBarManager.update(war, owner.getName(), hp);
                    if (hp > 0) {
                        event.setCancelled(true);
                        return;
                    } else {
                        WarBossBarManager.remove(war, owner.getName());
                        event.setCancelled(false);
                        event.getBlock().setType(org.bukkit.Material.AIR);
                    }
                }
                WarManager.coreDestroyed(owner, me.continent.kingdom.nationManager.getByName(attackerVillage.getnation()));
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(this::isCoreBlock);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(this::isCoreBlock);
    }
}
