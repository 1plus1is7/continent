package me.continent.protection;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
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

    private boolean inProtectedKingdom(Block block) {
        Kingdom kingdom = KingdomManager.getByChunk(block.getChunk());
        return kingdom != null && kingdom.isUnderProtection();
    }

    private boolean inProtectedKingdom(Entity entity) {
        Chunk chunk = entity.getLocation().getChunk();
        Kingdom kingdom = KingdomManager.getByChunk(chunk);
        return kingdom != null && kingdom.isUnderProtection();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (inProtectedKingdom(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && inProtectedKingdom(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Kingdom kingdom = KingdomManager.getByChunk(block.getChunk());
        if (kingdom == null || !kingdom.isUnderProtection()) return;

        Player player = event.getPlayer();
        if (player != null && kingdom.getMembers().contains(player.getUniqueId()) && kingdom.isMemberIgniteAllowed()) {
            return; // allow allies if enabled
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block from = event.getBlock();
        Block to = event.getToBlock();
        Kingdom dest = KingdomManager.getByChunk(to.getChunk());
        if (dest != null) {
            Kingdom src = KingdomManager.getByChunk(from.getChunk());
            if (!Objects.equals(dest, src)) {
                if (from.getType() == Material.WATER || from.getType() == Material.LAVA) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onStructureGrow(StructureGrowEvent event) {
        Chunk root = event.getLocation().getChunk();
        Kingdom rootKingdom = KingdomManager.getByChunk(root);
        Iterator<BlockState> it = event.getBlocks().iterator();
        while (it.hasNext()) {
            BlockState state = it.next();
            Kingdom dest = KingdomManager.getByChunk(state.getLocation().getChunk());
            if (!Objects.equals(rootKingdom, dest)) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Kingdom src = KingdomManager.getByChunk(event.getBlock().getChunk());
        for (Block block : event.getBlocks()) {
            Chunk destChunk = block.getRelative(event.getDirection()).getChunk();
            Kingdom dest = KingdomManager.getByChunk(destChunk);
            if (!Objects.equals(src, dest)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) return;
        Kingdom src = KingdomManager.getByChunk(event.getBlock().getChunk());
        for (Block block : event.getBlocks()) {
            Chunk destChunk = block.getRelative(event.getDirection().getOppositeFace()).getChunk();
            Kingdom dest = KingdomManager.getByChunk(destChunk);
            if (!Objects.equals(src, dest)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
