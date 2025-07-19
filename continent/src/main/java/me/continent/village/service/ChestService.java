package me.continent.village.service;

import me.continent.village.Village;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ChestService {
    public static void openChest(Player player, Village village) {
        Inventory inv = Bukkit.createInventory(new VillageChestHolder(village), 27, "Village Chest");
        inv.setContents(village.getChestContents());
        player.openInventory(inv);
    }

    static class VillageChestHolder implements InventoryHolder {
        private final Village village;
        VillageChestHolder(Village village) { this.village = village; }
        Village getVillage() { return village; }
        @Override
        public Inventory getInventory() { return null; }
    }
}
