package me.continent.kingdom.service;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.kingdom.KingdomStorage;
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

public class KingdomManageService {
    public static void openMenu(Player player, Kingdom kingdom) {
        ManageHolder holder = new ManageHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, 27, "Kingdom Manage");
        holder.setInventory(inv);
        inv.setItem(10, createItem(Material.NAME_TAG, "이름 변경"));
        inv.setItem(12, createItem(Material.BOOK, "설명 변경"));
        inv.setItem(14, createItem(Material.MAP, "마을 관리"));
        inv.setItem(16, createItem(Material.SHIELD, "방어권"));
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
        private final Kingdom kingdom;
        private Inventory inv;
        ManageHolder(Kingdom kingdom) { this.kingdom = kingdom; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Kingdom getKingdom() { return kingdom; }
    }

    // ---- Prompts ----
    public static void promptRename(Player player, Kingdom kingdom) {
        new ConversationFactory(Bukkit.getPluginManager().getPlugin("continent"))
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "새 이름을 입력하세요";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        if (input == null || input.isEmpty()) return END_OF_CONVERSATION;
                        if (!KingdomManager.renameKingdom(kingdom, input)) {
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

    public static void promptDescription(Player player, Kingdom kingdom) {
        new ConversationFactory(Bukkit.getPluginManager().getPlugin("continent"))
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "새 설명을 입력하세요";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        kingdom.setDescription(input);
                        KingdomStorage.save(kingdom);
                        player.sendMessage("§a설명이 변경되었습니다.");
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }
}
