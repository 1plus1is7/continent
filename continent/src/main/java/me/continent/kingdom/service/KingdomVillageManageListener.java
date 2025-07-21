package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.kingdom.KingdomStorage;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.storage.VillageStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

public class KingdomVillageManageListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof KingdomVillageManageService.VillageHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            Kingdom kingdom = holder.getKingdom();
            int slot = event.getRawSlot();
            if (slot >= inv.getSize()) return;
            var item = inv.getItem(slot);
            if (item == null || !item.hasItemMeta()) return;
            String name = item.getItemMeta().getDisplayName();
            Village v = VillageManager.getByName(name);
            if (v == null) return;
            if (event.isLeftClick()) {
                kingdom.setCapital(name);
                KingdomStorage.save(kingdom);
                player.sendMessage("§a수도가 변경되었습니다.");
                KingdomVillageManageService.openMenu(player, kingdom);
            } else if (event.isRightClick()) {
                if (name.equalsIgnoreCase(kingdom.getCapital())) {
                    player.sendMessage("§c수도는 제외할 수 없습니다.");
                    return;
                }
                KingdomManager.removeVillage(kingdom, v);
                KingdomStorage.save(kingdom);
                VillageStorage.save(v);
                player.sendMessage("§e" + name + " 마을이 제외되었습니다.");
                KingdomVillageManageService.openMenu(player, kingdom);
            }
        }
    }
}
