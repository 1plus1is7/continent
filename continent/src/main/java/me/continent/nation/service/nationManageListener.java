package me.continent.nation.service;

import me.continent.nation.nation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

public class nationManageListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof nationManageService.ManageHolder holder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();
            nation kingdom = holder.getnation();
            if (slot == 10) {
                nationManageService.promptRename(player, kingdom);
                player.closeInventory();
            } else if (slot == 12) {
                nationManageService.promptDescription(player, kingdom);
                player.closeInventory();
            } else if (slot == 14) {
                nationVillageManageService.openMenu(player, kingdom);
            } else if (slot == 16) {
                player.sendMessage("§e방어권 기능은 아직 구현되지 않았습니다.");
            }
        }
    }
}
