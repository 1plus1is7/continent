package me.continent.nation.service;

import me.continent.nation.Nation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NationUpkeepService {
    public static void openMenu(Player player, Nation nation) {
        UpkeepHolder holder = new UpkeepHolder(nation);
        Inventory inv = Bukkit.createInventory(holder, 27, "Nation Upkeep");
        holder.setInventory(inv);
        render(inv, nation);
        player.openInventory(inv);
    }

    static void render(Inventory inv, Nation nation) {
        inv.clear();
        ItemStack cost = item(Material.PAPER, "이번 주 유지비");
        ItemMeta cMeta = cost.getItemMeta();
        double amount = MaintenanceService.getWeeklyCost(nation);
        cMeta.setLore(List.of("§7금액: " + amount + "C", "§7미납 주: " + nation.getUnpaidWeeks()));
        cost.setItemMeta(cMeta);
        inv.setItem(11, cost);

        ItemStack last = item(Material.CLOCK, "마지막 납부");
        ItemMeta lMeta = last.getItemMeta();
        String date = nation.getLastMaintenance() == 0 ? "없음" :
                DateTimeFormatter.ofPattern("yyyy/MM/dd").withZone(ZoneId.systemDefault())
                        .format(Instant.ofEpochMilli(nation.getLastMaintenance()));
        lMeta.setLore(List.of("§7일자: " + date));
        last.setItemMeta(lMeta);
        inv.setItem(15, last);

        inv.setItem(22, item(Material.ARROW, "뒤로"));
    }

    private static ItemStack item(Material mat, String name) {
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        is.setItemMeta(meta);
        return is;
    }

    static class UpkeepHolder implements InventoryHolder {
        private final Nation nation;
        private Inventory inv;
        UpkeepHolder(Nation n) { this.nation = n; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Nation getNation() { return nation; }
    }
}
