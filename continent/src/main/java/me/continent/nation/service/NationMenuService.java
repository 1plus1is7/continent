package me.continent.nation.service;

import me.continent.nation.Nation;
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

public class NationMenuService {
    public static void openMenu(Player player, Nation nation) {
        MenuHolder holder = new MenuHolder(nation);
        Inventory inv = Bukkit.createInventory(holder, 36, "Nation Menu");
        holder.setInventory(inv);

        ItemStack symbol = nation.getSymbol() == null ? new ItemStack(Material.PAPER) : nation.getSymbol().clone();
        ItemMeta meta = symbol.getItemMeta();
        meta.setDisplayName("§a국가 정보");
        List<String> lore = new ArrayList<>();
        lore.add("§f이름: §e" + nation.getName());
        OfflinePlayer king = Bukkit.getOfflinePlayer(nation.getKing());
        lore.add("§f촌장: §e" + (king.getName() != null ? king.getName() : king.getUniqueId()));
        lore.add("§f구성원: §e" + nation.getMembers().size());
        lore.add("§f금고: §e" + nation.getVault() + "G");
        meta.setLore(lore);
        symbol.setItemMeta(meta);
        inv.setItem(13, symbol);

        inv.setItem(19, createItem(Material.PLAYER_HEAD, "구성원"));
        inv.setItem(21, createItem(Material.GOLD_INGOT, "금고 관리"));
        inv.setItem(23, createItem(Material.COMPASS, "국가 스폰 이동"));
        inv.setItem(25, createItem(Material.CHEST, "국가 창고"));

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
        private final Nation nation;
        private Inventory inv;
        MenuHolder(Nation nation) { this.nation = nation; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Nation getNation() { return nation; }
    }
}
