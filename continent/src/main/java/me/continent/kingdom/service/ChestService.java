package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ChestService {
    public static void openChest(Player player, Kingdom kingdom) {
        Inventory inv = Bukkit.createInventory(new KingdomChestHolder(kingdom), 27, "Kingdom Chest");
        inv.setContents(kingdom.getChestContents());
        player.openInventory(inv);
    }

    static class KingdomChestHolder implements InventoryHolder {
        private final Kingdom kingdom;
        KingdomChestHolder(Kingdom kingdom) { this.kingdom = kingdom; }
        Kingdom getKingdom() { return kingdom; }
        @Override
        public Inventory getInventory() { return null; }
    }
}
