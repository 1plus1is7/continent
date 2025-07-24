package me.continent.economy.gui;

import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class GoldPayService {
    public static void openSelect(Player player) {
        SelectHolder holder = new SelectHolder();
        Inventory inv = Bukkit.createInventory(holder, 54, "송금 대상 선택");
        holder.setInventory(inv);
        int idx = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId())) continue;
            if (idx >= inv.getSize()) break;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName(p.getName());
            head.setItemMeta(meta);
            inv.setItem(idx++, head);
        }
        inv.setItem(53, createButton(Material.ARROW, "메인 메뉴"));
        player.openInventory(inv);
    }

    public static void openAmount(Player player, UUID target, int amount) {
        AmountHolder holder = new AmountHolder(target, amount);
        Inventory inv = Bukkit.createInventory(holder, 45, "송금 금액 설정");
        holder.setInventory(inv);
        fill(inv);
        render(inv, amount);
        player.openInventory(inv);
    }

    static void render(Inventory inv, int amount) {
        inv.setItem(20, amountButton(Material.REDSTONE, "-10G", amount - 10));
        inv.setItem(21, amountButton(Material.REDSTONE, "-1G", amount - 1));
        inv.setItem(23, amountButton(Material.LIME_DYE, "+1G", amount + 1));
        inv.setItem(24, amountButton(Material.LIME_DYE, "+10G", amount + 10));
        if (amount < 1) amount = 1;
        ItemStack amt = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = amt.getItemMeta();
        meta.setDisplayName("송금: " + amount + "G");
        amt.setItemMeta(meta);
        inv.setItem(31, amt);
        inv.setItem(38, createButton(Material.BARRIER, "취소"));
        inv.setItem(40, createButton(Material.EMERALD_BLOCK, "송금"));
        inv.setItem(42, createButton(Material.ARROW, "메인 메뉴"));
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

    private static ItemStack amountButton(Material mat, String name, int amount) {
        if (amount < 1) amount = 1;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(java.util.List.of("§7금액: " + amount + "G"));
        item.setItemMeta(meta);
        return item;
    }

    static class SelectHolder implements InventoryHolder {
        private Inventory inv;
        void setInventory(Inventory i) { this.inv = i; }
        @Override public Inventory getInventory() { return inv; }
    }

    static class AmountHolder implements InventoryHolder {
        private final UUID target;
        private int amount;
        private Inventory inv;
        AmountHolder(UUID t, int a) { this.target = t; this.amount = a; }
        void setInventory(Inventory i) { this.inv = i; }
        @Override public Inventory getInventory() { return inv; }
        public UUID getTarget() { return target; }
        public int getAmount() { return amount; }
        public void setAmount(int a) { amount = Math.max(1, a); }
    }

    public static void performPay(Player player, AmountHolder holder) {
        Player target = Bukkit.getPlayer(holder.getTarget());
        if (target == null || !target.isOnline()) {
            player.sendMessage("§c해당 플레이어는 오프라인입니다.");
            return;
        }
        int amount = holder.getAmount();
        PlayerData sender = PlayerDataManager.get(player.getUniqueId());
        if (sender.getGold() < amount) {
            player.sendMessage("§c보유 골드가 부족합니다.");
            return;
        }
        PlayerData receiver = PlayerDataManager.get(target.getUniqueId());
        sender.removeGold(amount);
        receiver.addGold(amount);
        PlayerDataManager.save(player.getUniqueId());
        PlayerDataManager.save(target.getUniqueId());
        player.sendMessage("§e" + target.getName() + "에게 " + amount + "G를 송금했습니다.");
        target.sendMessage("§e" + player.getName() + "로부터 " + amount + "G를 받았습니다.");
    }
}
