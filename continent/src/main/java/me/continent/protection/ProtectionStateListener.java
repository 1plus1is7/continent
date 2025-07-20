package me.continent.protection;

import me.continent.village.Village;
import me.continent.village.VillageManager;
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

    private boolean inProtectedVillage(Block block) {
        Village village = VillageManager.getByChunk(block.getChunk());
        if (village == null) return false;
        if (!village.isUnderProtection()) return false;
        if (village.getKingdom() != null && me.continent.war.WarManager.getWar(village.getKingdom()) != null) {
            return false;
        }
        return true;
    }

    private boolean inProtectedVillage(Entity entity) {
        Chunk chunk = entity.getLocation().getChunk();
        Village village = VillageManager.getByChunk(chunk);
        if (village == null) return false;
        if (!village.isUnderProtection()) return false;
        if (village.getKingdom() != null && me.continent.war.WarManager.getWar(village.getKingdom()) != null) {
            return false;
        }
        return true;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (inProtectedVillage(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;

        Village village = VillageManager.getByChunk(block.getChunk());
        if (village != null && village.isUnderProtection()
                && !village.getMembers().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Village village = VillageManager.getByChunk(block.getChunk());
        if (village == null || !village.isUnderProtection()) return;

        Player player = event.getPlayer();
        if (player != null && village.getMembers().contains(player.getUniqueId()) && village.isMemberIgniteAllowed()) {
            return; // allow allies if enabled
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block from = event.getBlock();
        Block to = event.getToBlock();
        Village dest = VillageManager.getByChunk(to.getChunk());
        if (dest != null) {
            Village src = VillageManager.getByChunk(from.getChunk());
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
        Village rootVillage = VillageManager.getByChunk(root);
        Iterator<BlockState> it = event.getBlocks().iterator();
        while (it.hasNext()) {
            BlockState state = it.next();
            Village dest = VillageManager.getByChunk(state.getLocation().getChunk());
            if (!Objects.equals(rootVillage, dest)) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Village src = VillageManager.getByChunk(event.getBlock().getChunk());
        for (Block block : event.getBlocks()) {
            Chunk destChunk = block.getRelative(event.getDirection()).getChunk();
            Village dest = VillageManager.getByChunk(destChunk);
            if (!Objects.equals(src, dest)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) return;
        Village src = VillageManager.getByChunk(event.getBlock().getChunk());
        for (Block block : event.getBlocks()) {
            Chunk destChunk = block.getRelative(event.getDirection().getOppositeFace()).getChunk();
            Village dest = VillageManager.getByChunk(destChunk);
            if (!Objects.equals(src, dest)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
