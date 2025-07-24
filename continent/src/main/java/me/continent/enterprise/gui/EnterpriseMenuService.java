package me.continent.enterprise.gui;

import me.continent.enterprise.*;
import me.continent.player.PlayerDataManager;
import me.continent.player.PlayerData;
import me.continent.ContinentPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/** GUI service for enterprise registration and management. */
public class EnterpriseMenuService {
    /** Holder for the registration GUI. */
    public static class RegisterHolder implements InventoryHolder {
        private final UUID playerId;
        private String name;
        private EnterpriseType type;
        private Inventory inv;
        RegisterHolder(UUID playerId) { this.playerId = playerId; }
        void setInventory(Inventory i) { this.inv = i; }
        @Override public Inventory getInventory() { return inv; }
        public UUID getPlayerId() { return playerId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public EnterpriseType getType() { return type; }
        public void setType(EnterpriseType type) { this.type = type; }
    }

    /** Holder for enterprise main menu. */
    public static class MainHolder implements InventoryHolder {
        private final Enterprise enterprise;
        private Inventory inv;
        MainHolder(Enterprise e) { this.enterprise = e; }
        void setInventory(Inventory i) { this.inv = i; }
        @Override public Inventory getInventory() { return inv; }
        public Enterprise getEnterprise() { return enterprise; }
    }

    /** Holder for type selection. */
    public static class TypeHolder implements InventoryHolder {
        private final RegisterHolder parent;
        private Inventory inv;
        TypeHolder(RegisterHolder parent) { this.parent = parent; }
        void setInventory(Inventory i) { this.inv = i; }
        @Override public Inventory getInventory() { return inv; }
        public RegisterHolder getParent() { return parent; }
    }

    private static final Map<UUID, RegisterHolder> pendingName = new HashMap<>();

    /** Request chat input for enterprise name. */
    static void requestName(Player player, RegisterHolder holder) {
        pendingName.put(player.getUniqueId(), holder);
        player.closeInventory();
        player.sendMessage("§e기업 이름을 채팅으로 입력하세요.");
    }

    /** Called from chat listener when player provides name. */
    public static void provideName(Player player, String name) {
        RegisterHolder holder = pendingName.remove(player.getUniqueId());
        if (holder == null) return;
        holder.setName(name);
        Bukkit.getScheduler().runTask(ContinentPlugin.getInstance(), () -> openRegister(player, holder));
    }

    /** Open the register GUI. */
    public static void openRegister(Player player) {
        RegisterHolder holder = new RegisterHolder(player.getUniqueId());
        openRegister(player, holder);
    }

    static void openRegister(Player player, RegisterHolder holder) {
        Inventory inv = Bukkit.createInventory(holder, 27, "기업 설립");
        holder.setInventory(inv);
        renderRegister(inv, holder);
        player.openInventory(inv);
    }

    private static ItemStack button(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    static void renderRegister(Inventory inv, RegisterHolder holder) {
        fill(inv);
        String name = holder.getName() == null ? "§c미정" : holder.getName();
        inv.setItem(11, button(Material.PAPER, "이름 설정", Collections.singletonList(name)));
        String type = holder.getType() == null ? "§c미정" : holder.getType().name();
        inv.setItem(13, button(Material.CHEST, "업종 선택", Collections.singletonList(type)));
        inv.setItem(15, button(Material.EMERALD_BLOCK, "설립", null));
    }

    /** Open type selection GUI. */
    static void openTypeSelect(Player player, RegisterHolder holder) {
        TypeHolder th = new TypeHolder(holder);
        Inventory inv = Bukkit.createInventory(th, 27, "업종 선택");
        th.setInventory(inv);
        fill(inv);
        int idx = 0;
        for (EnterpriseType t : EnterpriseType.values()) {
            inv.setItem(idx++, button(Material.BOOK, t.name(), null));
        }
        inv.setItem(26, button(Material.ARROW, "뒤로", null));
        player.openInventory(inv);
    }

    /** Attempt to create enterprise from holder data. */
    static void tryCreate(Player player, RegisterHolder holder) {
        if (holder.getName() == null || holder.getType() == null) {
            player.sendMessage("§c이름과 업종을 모두 설정해주세요.");
            return;
        }
        if (EnterpriseManager.nameExists(holder.getName())) {
            player.sendMessage("§c이미 존재하는 기업 이름입니다.");
            return;
        }
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        double cost = 100; // flat cost
        if (data.getGold() < cost) {
            player.sendMessage("§c골드가 부족합니다. 비용: " + cost + "G");
            return;
        }
        data.removeGold(cost);
        String id = UUID.randomUUID().toString();
        Enterprise ent = new Enterprise(id, holder.getName(), holder.getType(), player.getUniqueId(), System.currentTimeMillis());
        EnterpriseManager.register(ent);
        EnterpriseStorage.save(ent);
        PlayerDataManager.save(player.getUniqueId());
        player.sendMessage("§a기업이 설립되었습니다: " + ent.getName());
        openMain(player, ent);
    }

    /** Open main menu for an enterprise. */
    public static void openMain(Player player, Enterprise ent) {
        MainHolder holder = new MainHolder(ent);
        Inventory inv = Bukkit.createInventory(holder, 54, "§f[내 기업 관리]");
        holder.setInventory(inv);
        fill(inv);
        inv.setItem(10, button(Material.NAME_TAG, "기업 정보", null));
        inv.setItem(49, button(Material.BARRIER, "닫기", null));
        player.openInventory(inv);
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

    static RegisterHolder getPendingName(UUID uuid) {
        return pendingName.get(uuid);
    }
}
