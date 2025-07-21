package me.continent.protection;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.war.WarManager;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public class TerritoryProtectionListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        Village owner = VillageManager.getByChunk(chunk);
        if (owner == null) return; // 야생

        Village playerVillage = VillageManager.getByPlayer(player.getUniqueId());

        boolean allowed = false;
        if (playerVillage != null && owner.getnation() != null && playerVillage.getnation() != null) {
            if (owner.getnation().equalsIgnoreCase(playerVillage.getnation()) ||
                WarManager.isAtWar(owner.getnation(), playerVillage.getnation())) {
                allowed = true;
            }
        }

        if (!owner.equals(playerVillage) && !allowed) {
            event.setCancelled(true);
            player.sendMessage("§c이 지역은 다른 마을의 보호 구역입니다.");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();

        Village owner = VillageManager.getByChunk(chunk);
        if (owner == null) return;

        Village playerVillage = VillageManager.getByPlayer(player.getUniqueId());

        boolean allowed = false;
        if (playerVillage != null && owner.getnation() != null && playerVillage.getnation() != null) {
            if (owner.getnation().equalsIgnoreCase(playerVillage.getnation()) ||
                WarManager.isAtWar(owner.getnation(), playerVillage.getnation())) {
                allowed = true;
            }
        }

        if (!owner.equals(playerVillage) && !allowed) {
            event.setCancelled(true);
            player.sendMessage("§c이 지역은 다른 마을의 보호 구역입니다.");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null) {
            // farmland trampling
            if (event.getClickedBlock().getType() == Material.FARMLAND) {
                Village owner = VillageManager.getByChunk(event.getClickedBlock().getChunk());
                if (owner != null && !owner.getMembers().contains(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Village owner = VillageManager.getByChunk(event.getClickedBlock().getChunk());
            if (owner == null) return;

            Village playerVillage = VillageManager.getByPlayer(event.getPlayer().getUniqueId());
            boolean allowed = false;
            if (playerVillage != null && owner.getnation() != null && playerVillage.getnation() != null) {
                if (owner.getnation().equalsIgnoreCase(playerVillage.getnation()) ||
                    WarManager.isAtWar(owner.getnation(), playerVillage.getnation())) {
                    allowed = true;
                }
            }

            if (!owner.equals(playerVillage) && !allowed) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§c이 지역은 다른 마을의 보호 구역입니다.");
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        Village owner = VillageManager.getByChunk(chunk);
        if (owner == null) return;

        Village playerVillage = VillageManager.getByPlayer(event.getPlayer().getUniqueId());
        boolean allowed = false;
        if (playerVillage != null && owner.getnation() != null && playerVillage.getnation() != null) {
            if (owner.getnation().equalsIgnoreCase(playerVillage.getnation()) ||
                WarManager.isAtWar(owner.getnation(), playerVillage.getnation())) {
                allowed = true;
            }
        }

        if (!owner.equals(playerVillage) && !allowed) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c이 지역은 다른 마을의 보호 구역입니다.");
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        Village owner = VillageManager.getByChunk(chunk);
        if (owner == null) return;

        Village playerVillage = VillageManager.getByPlayer(event.getPlayer().getUniqueId());
        boolean allowed = false;
        if (playerVillage != null && owner.getnation() != null && playerVillage.getnation() != null) {
            if (owner.getnation().equalsIgnoreCase(playerVillage.getnation()) ||
                WarManager.isAtWar(owner.getnation(), playerVillage.getnation())) {
                allowed = true;
            }
        }

        if (!owner.equals(playerVillage) && !allowed) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c이 지역은 다른 마을의 보호 구역입니다.");
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player player)) return;

        Village owner = VillageManager.getByChunk(event.getEntity().getLocation().getChunk());
        if (owner == null) return;

        Village playerVillage = VillageManager.getByPlayer(player.getUniqueId());
        boolean allowed = false;
        if (playerVillage != null && owner.getnation() != null && playerVillage.getnation() != null) {
            if (owner.getnation().equalsIgnoreCase(playerVillage.getnation()) ||
                WarManager.isAtWar(owner.getnation(), playerVillage.getnation())) {
                allowed = true;
            }
        }

        if (!owner.equals(playerVillage) && !allowed) {
            event.setCancelled(true);
            player.sendMessage("§c이 지역은 다른 마을의 보호 구역입니다.");
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> VillageManager.getByChunk(block.getChunk()) != null);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> VillageManager.getByChunk(block.getChunk()) != null);
    }
}