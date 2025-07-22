package me.continent.village.service;

import me.continent.village.Village;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class VillageMenuService {
    public static void openMenu(Player player, Village village) {
        MenuHolder holder = new MenuHolder(village);
        Inventory inv = Bukkit.createInventory(holder, 36, "Village Menu");
        holder.setInventory(inv);

        ItemStack symbol = village.getSymbol() == null ? new ItemStack(Material.PAPER) : village.getSymbol().clone();
        ItemMeta meta = symbol.getItemMeta();
        meta.setDisplayName("§a마을 정보");
        List<String> lore = new ArrayList<>();
        lore.add("§f이름: §e" + village.getName());
        OfflinePlayer king = Bukkit.getOfflinePlayer(village.getKing());
        lore.add("§f촌장: §e" + (king.getName() != null ? king.getName() : king.getUniqueId()));
        lore.add("§f구성원: §e" + village.getMembers().size());
        lore.add("§f금고: §e" + village.getVault() + "G");
        meta.setLore(lore);
        symbol.setItemMeta(meta);
        inv.setItem(13, symbol);

        inv.setItem(19, createItem(Material.PLAYER_HEAD, "구성원"));
        inv.setItem(21, createItem(Material.GOLD_INGOT, "금고 관리"));
        inv.setItem(23, createItem(Material.COMPASS, "마을 스폰 이동"));
        inv.setItem(25, createItem(Material.CHEST, "마을 창고"));

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    static class MenuHolder implements InventoryHolder {
        private final Village village;
        private Inventory inv;
        MenuHolder(Village village) { this.village = village; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Village getVillage() { return village; }
    }
}
