package me.continent.enterprise.gui;

import me.continent.enterprise.Enterprise;
import me.continent.enterprise.logistics.DeliveryEntry;
import me.continent.enterprise.logistics.DeliveryState;
import me.continent.enterprise.logistics.LogisticsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/** GUI showing current delivery status for an enterprise. */
public class DeliveryStatusGUI {
    public static void open(Player player, Enterprise enterprise) {
        Holder holder = new Holder(enterprise);
        Inventory inv = Bukkit.createInventory(holder, 27, "배송 상태");
        holder.setInventory(inv);
        fill(inv);
        List<DeliveryEntry> list = LogisticsManager.getShipments(enterprise.getId());
        int idx = 0;
        for (DeliveryEntry e : list) {
            if (idx >= 27) break;
            ItemStack item = e.getItem().clone();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(item.getItemMeta().getDisplayName());
            List<String> lore = new ArrayList<>();
            lore.add("§7대상: " + e.getTarget());
            lore.add("§7수량: " + e.getAmount());
            lore.add("§7상태: " + stateName(e.getState()));
            long remain = e.getFinishTime() - System.currentTimeMillis();
            if (remain < 0) remain = 0;
            lore.add("§7남은 시간: " + (remain / 1000) + "s");
            lore.add("§7보상: " + e.getReward() + "C");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(idx++, item);
        }
        player.openInventory(inv);
    }

    private static String stateName(DeliveryState st) {
        return switch (st) {
            case WAITING -> "대기 중";
            case MOVING -> "이동 중";
            case HALTED -> "중단됨";
            case COMPLETED -> "완료됨";
        };
    }

    private static void fill(Inventory inv) {
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, pane);
        }
    }

    static class Holder implements InventoryHolder {
        private final Enterprise ent;
        private Inventory inv;
        Holder(Enterprise ent) { this.ent = ent; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Enterprise getEnterprise() { return ent; }
    }
}
