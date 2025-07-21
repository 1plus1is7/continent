package me.continent.nation.service;

import me.continent.nation.nation;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class nationMenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof nationMenuService.nationMenuHolder holder) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();
            nation kingdom = holder.getnation();
            if (slot == 13) {
                // management menu placeholder
                nationManageService.openMenu(player, kingdom);
            } else if (slot == 19) {
                nationMemberService.openMenu(player, kingdom);
            } else if (slot == 21) {
                nationTreasuryService.openMenu(player, kingdom);
            } else if (slot == 23) {
                Village capital = VillageManager.getByName(kingdom.getCapital());
                if (capital != null && capital.getSpawnLocation() != null) {
                    player.teleport(capital.getSpawnLocation());
                    player.sendMessage("§a수도로 이동했습니다.");
                } else {
                    player.sendMessage("§c수도 스폰이 설정되어 있지 않습니다.");
                }
            } else if (slot == 25) {
                nationChestService.openChest(player, kingdom);
            }
        }
    }
}
