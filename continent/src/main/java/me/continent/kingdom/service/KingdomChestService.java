package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class KingdomChestService {
    public static void openChest(Player player, Kingdom kingdom) {
        KingdomChestHolder holder = new KingdomChestHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, 27, "Kingdom Chest");
        holder.setInventory(inv);
        inv.setContents(kingdom.getChestContents());
        player.openInventory(inv);
    }

    static class KingdomChestHolder implements InventoryHolder {
        private final Kingdom kingdom;
        private Inventory inventory;

        KingdomChestHolder(Kingdom kingdom) { this.kingdom = kingdom; }
        void setInventory(Inventory inv) { this.inventory = inv; }
        Kingdom getKingdom() { return kingdom; }
        @Override public Inventory getInventory() { return inventory; }
    }
}
