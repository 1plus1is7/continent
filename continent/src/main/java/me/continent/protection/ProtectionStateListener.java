package me.continent.protection;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.Iterator;
import java.util.Objects;

public class ProtectionStateListener implements Listener {

    private boolean inProtectedNation(Block block) {
        Nation nation = NationManager.getByChunk(block.getChunk());
        if (nation == null) return false;
        if (!nation.isUnderProtection()) return false;
        // 전쟁 중이라면 보호 효과를 무시한다
        if (me.continent.war.WarManager.getWar(nation.getName()) != null) return false;
        return true;
    }

    private boolean inProtectedNation(Entity entity) {
        Chunk chunk = entity.getLocation().getChunk();
        Nation nation = NationManager.getByChunk(chunk);
        if (nation == null) return false;
        if (!nation.isUnderProtection()) return false;
        if (me.continent.war.WarManager.getWar(nation.getName()) != null) return false;
        return true;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!inProtectedNation(event.getEntity())) return;

        Entity damager = event.getDamager();
        if (damager instanceof Player player) {
            Nation nation = NationManager.getByChunk(event.getEntity().getLocation().getChunk());
            if (nation != null && nation.getMembers().contains(player.getUniqueId())) {
                return; // allow members to attack
            }
            Nation playerNation = NationManager.getByPlayer(player.getUniqueId());
            if (playerNation != null && nation != null &&
                    me.continent.war.WarManager.isAtWar(playerNation.getName(), nation.getName())) {
                return; // allow enemies during war
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;

        Nation nation = NationManager.getByChunk(block.getChunk());
        if (inProtectedNation(block) &&
                !nation.getMembers().contains(event.getPlayer().getUniqueId())) {
            Nation playerNation = NationManager.getByPlayer(event.getPlayer().getUniqueId());
            if (playerNation == null ||
                    !me.continent.war.WarManager.isAtWar(playerNation.getName(), nation.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Nation nation = NationManager.getByChunk(block.getChunk());
        if (!inProtectedNation(block)) return;

        Player player = event.getPlayer();
        if (player != null) {
            if (nation.getMembers().contains(player.getUniqueId()) && nation.isMemberIgniteAllowed()) {
                return; // allow allies if enabled
            }
            Nation playerNation = NationManager.getByPlayer(player.getUniqueId());
            if (playerNation != null && me.continent.war.WarManager.isAtWar(playerNation.getName(), nation.getName())) {
                return; // allow enemies during war
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block from = event.getBlock();
        Block to = event.getToBlock();
        Nation dest = NationManager.getByChunk(to.getChunk());
        if (dest != null) {
            Nation src = NationManager.getByChunk(from.getChunk());
            if (!Objects.equals(dest, src)) {
                if (src != null && me.continent.war.WarManager.isAtWar(src.getName(), dest.getName())) {
                    return; // allow during war
                }
                if (from.getType() == Material.WATER || from.getType() == Material.LAVA) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        Chunk root = event.getLocation().getChunk();
        Nation rootNation = NationManager.getByChunk(root);
        Iterator<BlockState> it = event.getBlocks().iterator();
        while (it.hasNext()) {
            BlockState state = it.next();
            Nation dest = NationManager.getByChunk(state.getLocation().getChunk());
            if (!Objects.equals(rootNation, dest)) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Nation src = NationManager.getByChunk(event.getBlock().getChunk());
        for (Block block : event.getBlocks()) {
            Chunk destChunk = block.getRelative(event.getDirection()).getChunk();
            Nation dest = NationManager.getByChunk(destChunk);
            if (!Objects.equals(src, dest)) {
                if (src != null && dest != null && me.continent.war.WarManager.isAtWar(src.getName(), dest.getName())) {
                    continue; // allow during war
                }
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) return;
        Nation src = NationManager.getByChunk(event.getBlock().getChunk());
        for (Block block : event.getBlocks()) {
            Chunk destChunk = block.getRelative(event.getDirection().getOppositeFace()).getChunk();
            Nation dest = NationManager.getByChunk(destChunk);
            if (!Objects.equals(src, dest)) {
                if (src != null && dest != null && me.continent.war.WarManager.isAtWar(src.getName(), dest.getName())) {
                    continue; // allow during war
                }
                event.setCancelled(true);
                return;
            }
        }
    }
}
