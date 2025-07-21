package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

public class KingdomManageListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof KingdomManageService.ManageHolder holder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();
            Kingdom kingdom = holder.getKingdom();
            if (slot == 10) {
                KingdomManageService.promptRename(player, kingdom);
                player.closeInventory();
            } else if (slot == 12) {
                KingdomManageService.promptDescription(player, kingdom);
                player.closeInventory();
            } else if (slot == 14) {
                KingdomVillageManageService.openMenu(player, kingdom);
            } else if (slot == 16) {
                player.sendMessage("§e방어권 기능은 아직 구현되지 않았습니다.");
            }
        }
    }
}
