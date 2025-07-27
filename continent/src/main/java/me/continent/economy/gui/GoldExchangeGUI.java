package me.continent.economy.gui;

import me.continent.economy.CentralBank;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GoldExchangeGUI {
    public enum Mode { CONVERT, EXCHANGE }

    public static void open(Player player, Mode mode, int qty) {
        Holder holder = new Holder(mode, qty);
        Inventory inv = Bukkit.createInventory(holder, 45, mode == Mode.CONVERT ? "Gold -> Ingot" : "Ingot -> Gold");
        holder.setInventory(inv);
        fill(inv);
        renderButtons(inv, player, mode, qty);
        player.openInventory(inv);
    }

    static void renderButtons(Inventory inv, Player player, Mode mode, int qty) {
        double rate = CentralBank.getExchangeRate();
        if (qty < 1) qty = 1;
        int total = (int) Math.round(rate * qty);

        ItemStack bundle = new ItemStack(Material.BUNDLE);
        ItemMeta bMeta = bundle.getItemMeta();
        bMeta.setDisplayName("§e거래 설정");
        List<String> bLore = new ArrayList<>();
        bLore.add("§7수량: " + qty);
        bLore.add((mode == Mode.CONVERT ? "§7비용: " : "§7수익: ") + total + "C");
        bMeta.setLore(bLore);
        bundle.setItemMeta(bMeta);
        inv.setItem(4, bundle);

        inv.setItem(20, qtyButton(Material.REDSTONE, "-10", qty - 10));
        inv.setItem(21, qtyButton(Material.REDSTONE, "-1", qty - 1));
        inv.setItem(23, qtyButton(Material.LIME_DYE, "+1", qty + 1));
        inv.setItem(24, qtyButton(Material.LIME_DYE, "+10", qty + 10));
        inv.setItem(41, maxButton(player, mode));
        String name = mode == Mode.CONVERT ? "비용: " + total + "C" : "수익: " + total + "C";
        ItemStack price = new ItemStack(Material.GOLD_INGOT);
        ItemMeta pm = price.getItemMeta();
        pm.setDisplayName(name);
        price.setItemMeta(pm);
        inv.setItem(22, price);
        inv.setItem(38, createButton(Material.BARRIER, "취소"));
        inv.setItem(40, createButton(Material.EMERALD_BLOCK, "확인"));
        inv.setItem(42, createButton(Material.ARROW, "메인 메뉴"));
    }

    private static ItemStack maxButton(Player player, Mode mode) {
        int max = getMaxQty(player, mode);
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("최대");
        List<String> lore = new ArrayList<>();
        lore.add("§7가능: " + max);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createButton(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
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

    private static ItemStack qtyButton(Material mat, String name, int qty) {
        if (qty < 1) qty = 1;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add("§7수량: " + qty);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static class Holder implements InventoryHolder {
        private final Mode mode;
        private int qty;
        private Inventory inv;
        Holder(Mode m, int q) { this.mode = m; this.qty = q; }
        void setInventory(Inventory i) { this.inv = i; }
        @Override public Inventory getInventory() { return inv; }
        public Mode getMode() { return mode; }
        public int getQty() { return qty; }
        public void setQty(int q) { qty = Math.max(1, q); }
    }

    public static void perform(Player player, Holder holder) {
        int qty = holder.getQty();
        if (qty < 1) qty = 1;
        double rate = CentralBank.getExchangeRate();
        int total = (int) Math.round(rate * qty);
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        if (holder.getMode() == Mode.CONVERT) {
            if (data.getGold() < total) {
                player.sendMessage("§c크라운이 부족합니다. (필요: " + total + "C)");
                return;
            }
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage("§c인벤토리가 부족합니다.");
                return;
            }
            if (!CentralBank.withdrawGold(qty)) {
                player.sendMessage("§c중앙은행 금고에 금이 부족합니다.");
                return;
            }
            data.removeGold(total);
            ItemStack ingot = new ItemStack(Material.GOLD_INGOT, qty);
            player.getInventory().addItem(ingot);
            player.sendMessage("§e" + total + "G을 사용해 금괴 " + qty + "개를 구매했습니다.");
        } else {
            ItemStack ingot = new ItemStack(Material.GOLD_INGOT);
            if (!player.getInventory().containsAtLeast(ingot, qty)) {
                player.sendMessage("§c금괴가 부족합니다.");
                return;
            }
            player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, qty));
            CentralBank.addGold(qty);
            data.addGold(total);
            player.sendMessage("§e금괴 " + qty + "개를 환전해 " + total + "G을 받았습니다.");
        }
        PlayerDataManager.save(player.getUniqueId());
    }

    public static int getMaxQty(Player player, Mode mode) {
        if (mode == Mode.CONVERT) {
            double rate = CentralBank.getExchangeRate();
            int byGold = (int) Math.floor(PlayerDataManager.get(player.getUniqueId()).getGold() / rate);
            int byReserve = CentralBank.getGold();
            int max = Math.min(byGold, byReserve);
            return Math.max(1, max);
        } else {
            int count = 0;
            for (ItemStack is : player.getInventory().getContents()) {
                if (is != null && is.getType() == Material.GOLD_INGOT) count += is.getAmount();
            }
            return Math.max(1, count);
        }
    }
}
