package me.continent.village.service;

import me.continent.village.Village;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ChestService {
    public static void openChest(Player player, Village village) {
        VillageChestHolder holder = new VillageChestHolder(village);
        Inventory inv = Bukkit.createInventory(holder, 27, "Village Chest");
        holder.setInventory(inv);
        inv.setContents(village.getChestContents());
        player.openInventory(inv);
    }

    static class VillageChestHolder implements InventoryHolder {
        private final Village village;
        private Inventory inventory;

        VillageChestHolder(Village village) {
            this.village = village;
        }

        void setInventory(Inventory inv) {
            this.inventory = inv;
        }

        Village getVillage() {
            return village;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
