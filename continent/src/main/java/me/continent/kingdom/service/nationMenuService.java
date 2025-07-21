package me.continent.kingdom.service;

import me.continent.kingdom.nation;
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

public class nationMenuService {
    public static void openMenu(Player player, nation kingdom) {
        nationMenuHolder holder = new nationMenuHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, 36, "nation Menu");
        holder.setInventory(inv);

        // banner
        ItemStack banner = kingdom.getFlag() == null ? new ItemStack(Material.WHITE_BANNER) : kingdom.getFlag().clone();
        ItemMeta meta = banner.getItemMeta();
        meta.setDisplayName("§a국가 관리");
        List<String> lore = new ArrayList<>();
        lore.add("§f이름: §e" + kingdom.getName());
        lore.add("§f설명: §e" + kingdom.getDescription());
        lore.add("§f수도: §e" + kingdom.getCapital());
        OfflinePlayer king = Bukkit.getOfflinePlayer(kingdom.getLeader());
        lore.add("§f국왕: §e" + (king.getName() != null ? king.getName() : king.getUniqueId()));
        lore.add("§f마을: §e" + String.join(", ", kingdom.getVillages()));
        lore.add("§f국고: §e" + kingdom.getTreasury() + "G");
        meta.setLore(lore);
        banner.setItemMeta(meta);
        inv.setItem(13, banner);

        inv.setItem(19, createItem(Material.PLAYER_HEAD, "구성원"));
        inv.setItem(21, createItem(Material.GOLD_INGOT, "국고 관리"));
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

    static class nationMenuHolder implements InventoryHolder {
        private final nation kingdom;
        private Inventory inv;
        nationMenuHolder(nation kingdom) { this.kingdom = kingdom; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public nation getnation() { return kingdom; }
    }
}
