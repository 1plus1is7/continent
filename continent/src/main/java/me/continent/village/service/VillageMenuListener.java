package me.continent.village.service;

import me.continent.village.Village;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class VillageMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof VillageMenuService.MenuHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Village village = holder.getVillage();
            int slot = event.getRawSlot();
            if (slot == 19) {
                VillageMemberService.openMenu(player, village);
            } else if (slot == 21) {
                VillageTreasuryService.openMenu(player, village);
            } else if (slot == 23) {
                var spawn = village.getSpawnLocation();
                if (spawn != null) {
                    player.teleport(spawn);
                    player.sendMessage("§a마을 스폰으로 이동했습니다.");
                } else {
                    player.sendMessage("§c마을 스폰이 설정되어 있지 않습니다.");
                }
            } else if (slot == 25) {
                ChestService.openChest(player, village);
            }
        }
    }
}
