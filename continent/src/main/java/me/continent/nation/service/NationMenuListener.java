package me.continent.nation.service;

import me.continent.nation.Nation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class NationMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof NationMenuService.MenuHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Nation nation = holder.getNation();
            int slot = event.getRawSlot();
            if (slot == 19) {
                NationMemberService.openMenu(player, nation);
            } else if (slot == 21) {
                NationTreasuryService.openMenu(player, nation);
            } else if (slot == 23) {
                var spawn = nation.getSpawnLocation();
                if (spawn != null) {
                    player.teleport(spawn);
                    player.sendMessage("§a국가 스폰으로 이동했습니다.");
                } else {
                    player.sendMessage("§c국가 스폰이 설정되어 있지 않습니다.");
                }
            } else if (slot == 25) {
                ChestService.openChest(player, nation);
            }
        }
    }
}
