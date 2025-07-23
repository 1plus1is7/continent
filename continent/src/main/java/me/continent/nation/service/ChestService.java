package me.continent.nation.service;

import me.continent.nation.Nation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ChestService {
    public static void openChest(Player player, Nation nation) {
        NationChestHolder holder = new NationChestHolder(nation);
        Inventory inv = Bukkit.createInventory(holder, 27, "Nation Chest");
        holder.setInventory(inv);
        inv.setContents(nation.getChestContents());
        player.openInventory(inv);
    }

    static class NationChestHolder implements InventoryHolder {
        private final Nation nation;
        private Inventory inventory;

        NationChestHolder(Nation nation) {
            this.nation = nation;
        }

        void setInventory(Inventory inv) {
            this.inventory = inv;
        }

        Nation getNation() {
            return nation;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
