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
                boolean newState = !kingdom.isTerritoryProtectionEnabled();
                kingdom.setTerritoryProtectionEnabled(newState);
                me.continent.nation.nationStorage.save(kingdom);
                player.sendMessage("§a방어권이 " + (newState ? "활성화" : "비활성화") + "되었습니다.");
                nationManageService.openMenu(player, kingdom);
            }
        }
    }
}
