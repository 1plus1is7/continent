package me.continent.nation.service;

import me.continent.nation.nation;
import me.continent.nation.nationManager;
import me.continent.nation.nationStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class nationManageService {
    public static void openMenu(Player player, nation kingdom) {
        ManageHolder holder = new ManageHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, 27, "nation Manage");
        holder.setInventory(inv);
        inv.setItem(10, createItem(Material.NAME_TAG, "이름 변경"));
        inv.setItem(12, createItem(Material.BOOK, "설명 변경"));
        inv.setItem(14, createItem(Material.MAP, "마을 관리"));
        String state = kingdom.isTerritoryProtectionEnabled() ? "ON" : "OFF";
        Material mat = kingdom.isTerritoryProtectionEnabled() ? Material.SHIELD : Material.BARRIER;
        inv.setItem(16, createItem(mat, "방어권: " + state));
        player.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    static class ManageHolder implements InventoryHolder {
        private final nation kingdom;
        private Inventory inv;
        ManageHolder(nation kingdom) { this.kingdom = kingdom; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public nation getnation() { return kingdom; }
    }

    // ---- Prompts ----
    public static void promptRename(Player player, nation kingdom) {
        new ConversationFactory(Bukkit.getPluginManager().getPlugin("continent"))
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "새 이름을 입력하세요";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        if (input == null || input.isEmpty()) return END_OF_CONVERSATION;
                        if (!nationManager.renamenation(kingdom, input)) {
                            player.sendMessage("§c이미 존재하는 이름입니다.");
                        } else {
                            player.sendMessage("§a이름이 변경되었습니다.");
                        }
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }

    public static void promptDescription(Player player, nation kingdom) {
        new ConversationFactory(Bukkit.getPluginManager().getPlugin("continent"))
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "새 설명을 입력하세요";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        kingdom.setDescription(input);
                        nationStorage.save(kingdom);
                        player.sendMessage("§a설명이 변경되었습니다.");
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }
}
