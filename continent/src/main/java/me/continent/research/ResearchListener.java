package me.continent.research;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

public class ResearchListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof ResearchManager.TreeHolder holder) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getItemMeta() == null) return;
            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            String tree = name.contains(" ") ? name.substring(name.indexOf(' ') + 1) : name;
            if (event.getClick() == ClickType.DOUBLE_CLICK) {
                ResearchManager.toggleTreeSelect((org.bukkit.entity.Player) event.getWhoClicked(), tree);
            } else {
                ResearchManager.openNodeMenu((org.bukkit.entity.Player) event.getWhoClicked(), tree);
            }
        } else if (inv.getHolder() instanceof ResearchManager.NodeHolder holder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot == 53) {
                ResearchManager.openTreeSelect((org.bukkit.entity.Player) event.getWhoClicked());
                return;
            }
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getItemMeta() == null) return;
            String id = ChatColor.stripColor(item.getItemMeta().getDisplayName());
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
        if (playerVillage == null || !playerVillage.equals(village)) {
            return;
        }

        event.setCancelled(true);
        ResearchManager.openTreeSelect(event.getPlayer());
    }
}
