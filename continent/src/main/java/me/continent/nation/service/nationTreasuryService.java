package me.continent.nation.service;

import me.continent.ContinentPlugin;
import me.continent.nation.nation;
import me.continent.nation.nationStorage;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class nationTreasuryService {
    public static void openMenu(Player player, nation kingdom) {
        TreasuryHolder holder = new TreasuryHolder(kingdom);
        Inventory inv = Bukkit.createInventory(holder, 9, "nation Treasury");
        holder.setInventory(inv);
        inv.setItem(2, createItem(Material.EMERALD_BLOCK, "입금"));
        inv.setItem(4, createItem(Material.REDSTONE_BLOCK, "출금"));
        inv.setItem(6, createItem(Material.PAPER, "세율: " + kingdom.getTaxRate() + "%"));
        player.openInventory(inv);
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    static class TreasuryHolder implements InventoryHolder {
        private final nation kingdom;
        private Inventory inv;
        TreasuryHolder(nation k) { this.kingdom = k; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public nation getnation() { return kingdom; }
    }

    // ---- prompts ----
    public static void promptDeposit(Player player, nation kingdom) {
        new ConversationFactory(ContinentPlugin.getInstance())
                .withFirstPrompt(new NumericPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "입금할 금액을 입력하세요";
                    }

                    @Override
                    protected Prompt acceptValidatedInput(ConversationContext context, Number number) {
                        int amount = number.intValue();
                        PlayerData data = PlayerDataManager.get(player.getUniqueId());
                        if (amount <= 0 || data.getGold() < amount) {
                            player.sendMessage("§c입금할 수 없습니다.");
                        } else if (!kingdom.getLeader().equals(player.getUniqueId())) {
                            player.sendMessage("§c국왕만 입금할 수 있습니다.");
                        } else {
                            data.removeGold(amount);
                            kingdom.addGold(amount);
                            PlayerDataManager.save(player.getUniqueId());
                            nationStorage.save(kingdom);
                            player.sendMessage("§a입금 완료: " + amount + "G");
                        }
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }

    public static void promptWithdraw(Player player, nation kingdom) {
        new ConversationFactory(ContinentPlugin.getInstance())
                .withFirstPrompt(new NumericPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "출금할 금액을 입력하세요";
                    }

                    @Override
                    protected Prompt acceptValidatedInput(ConversationContext context, Number number) {
                        int amount = number.intValue();
                        if (amount <= 0 || kingdom.getTreasury() < amount) {
                            player.sendMessage("§c출금할 수 없습니다.");
                        } else if (!kingdom.getLeader().equals(player.getUniqueId())) {
                            player.sendMessage("§c국왕만 출금할 수 있습니다.");
                        } else {
                            kingdom.removeGold(amount);
                            PlayerData data = PlayerDataManager.get(player.getUniqueId());
                            data.addGold(amount);
                            PlayerDataManager.save(player.getUniqueId());
                            nationStorage.save(kingdom);
                            player.sendMessage("§a출금 완료: " + amount + "G");
                        }
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }

    public static void promptTax(Player player, nation kingdom) {
        new ConversationFactory(ContinentPlugin.getInstance())
                .withFirstPrompt(new NumericPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "세율을 입력하세요 (0-100)";
                    }

                    @Override
                    protected Prompt acceptValidatedInput(ConversationContext context, Number number) {
                        double rate = number.doubleValue();
                        if (rate < 0 || rate > 100) {
                            player.sendMessage("§c0에서 100 사이의 값을 입력하세요.");
                        } else {
                            kingdom.setTaxRate(rate);
                            nationStorage.save(kingdom);
                            player.sendMessage("§a세율이 설정되었습니다: " + rate + "%");
                        }
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }
}
