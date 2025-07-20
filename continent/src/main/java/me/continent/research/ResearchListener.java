package me.continent.research;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ResearchListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof ResearchManager.ResearchHolder holder) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            String id = item.getItemMeta() != null ? item.getItemMeta().getDisplayName() : null;
            if (id == null) return;
            ResearchNode node = ResearchManager.getAllNodes().stream()
                    .filter(n -> n.getId().equals(id))
                    .findFirst().orElse(null);
            if (node != null) {
                ResearchManager.startResearch((org.bukkit.entity.Player) event.getWhoClicked(), node);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCoreInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.BEACON) return;

        Village village = VillageManager.getByChunk(block.getChunk());
        if (village == null || village.getCoreLocation() == null
                || !village.getCoreLocation().getBlock().equals(block)) {
            return;
        }

        Village playerVillage = VillageManager.getByPlayer(event.getPlayer().getUniqueId());
        if (playerVillage == null || village.getKingdom() == null
                || !village.getKingdom().equalsIgnoreCase(playerVillage.getKingdom())) {
            return;
        }

        event.setCancelled(true);
        ResearchManager.openMenu(event.getPlayer());
    }
}
