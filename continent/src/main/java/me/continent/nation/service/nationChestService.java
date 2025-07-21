package me.continent.nation.service;

import me.continent.nation.nation;
import me.continent.nation.nationStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class nationChestService {
    public static void openChest(Player player, nation kingdom) {
        nationChestHolder holder = new nationChestHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, 27, "nation Chest");
        holder.setInventory(inv);
        inv.setContents(kingdom.getChestContents());
        player.openInventory(inv);
    }

    static class nationChestHolder implements InventoryHolder {
        private final nation kingdom;
        private Inventory inventory;

        nationChestHolder(nation kingdom) { this.kingdom = kingdom; }
        void setInventory(Inventory inv) { this.inventory = inv; }
        nation getnation() { return kingdom; }
        @Override public Inventory getInventory() { return inventory; }
    }
}
