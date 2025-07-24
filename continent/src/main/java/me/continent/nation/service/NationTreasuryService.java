package me.continent.nation.service;

import me.continent.ContinentPlugin;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.storage.NationStorage;
import me.continent.nation.Nation;
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

public class NationTreasuryService {
    public static void openMenu(Player player, Nation nation) {
        TreasuryHolder holder = new TreasuryHolder(nation);
        Inventory inv = Bukkit.createInventory(holder, 27, "Nation Treasury");
        holder.setInventory(inv);

        ItemStack deposit = createItem(Material.EMERALD_BLOCK, "입금하기");
        ItemMeta dMeta = deposit.getItemMeta();
        dMeta.setLore(java.util.List.of("§7국가 금고에 골드를 입금합니다."));
        deposit.setItemMeta(dMeta);
        inv.setItem(11, deposit);

        ItemStack withdraw = createItem(Material.REDSTONE_BLOCK, "출금하기");
        ItemMeta wMeta = withdraw.getItemMeta();
        wMeta.setLore(java.util.List.of("§7국가 금고에서 골드를 출금합니다."));
        withdraw.setItemMeta(wMeta);
        inv.setItem(15, withdraw);

        ItemStack bal = createItem(Material.GOLD_INGOT, "잔액: " + nation.getVault() + "G");
        inv.setItem(13, bal);

        inv.setItem(22, createItem(Material.ARROW, "메인 메뉴"));
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
        private final Nation nation;
        private Inventory inv;
        TreasuryHolder(Nation v) { this.nation = v; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Nation getNation() { return nation; }
    }

    public static void promptDeposit(Player player, Nation nation) {
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
                        } else if (!nation.getKing().equals(player.getUniqueId())) {
                            player.sendMessage("§c촌장만 입금할 수 있습니다.");
                        } else {
                            data.removeGold(amount);
                            nation.addGold(amount);
                            PlayerDataManager.save(player.getUniqueId());
                            NationStorage.save(nation);
                            player.sendMessage("§a입금 완료: " + amount + "G");
                        }
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }

    public static void promptWithdraw(Player player, Nation nation) {
        new ConversationFactory(ContinentPlugin.getInstance())
                .withFirstPrompt(new NumericPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "출금할 금액을 입력하세요";
                    }

                    @Override
                    protected Prompt acceptValidatedInput(ConversationContext context, Number number) {
                        int amount = number.intValue();
                        if (amount <= 0 || nation.getVault() < amount) {
                            player.sendMessage("§c출금할 수 없습니다.");
                        } else if (!nation.getKing().equals(player.getUniqueId())) {
                            player.sendMessage("§c촌장만 출금할 수 있습니다.");
                        } else {
                            nation.removeGold(amount);
                            PlayerData data = PlayerDataManager.get(player.getUniqueId());
                            data.addGold(amount);
                            PlayerDataManager.save(player.getUniqueId());
                            NationStorage.save(nation);
                            player.sendMessage("§a출금 완료: " + amount + "G");
                        }
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }
}
