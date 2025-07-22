package me.continent.village.service;

import me.continent.ContinentPlugin;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.storage.VillageStorage;
import me.continent.village.Village;
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

public class VillageTreasuryService {
    public static void openMenu(Player player, Village village) {
        TreasuryHolder holder = new TreasuryHolder(village);
        Inventory inv = Bukkit.createInventory(holder, 9, "Village Treasury");
        holder.setInventory(inv);
        inv.setItem(2, createItem(Material.EMERALD_BLOCK, "입금"));
        inv.setItem(4, createItem(Material.REDSTONE_BLOCK, "출금"));
        inv.setItem(6, createItem(Material.GOLD_INGOT, "잔액: " + village.getVault() + "G"));
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
        private final Village village;
        private Inventory inv;
        TreasuryHolder(Village v) { this.village = v; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Village getVillage() { return village; }
    }

    public static void promptDeposit(Player player, Village village) {
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
                        } else if (!village.getKing().equals(player.getUniqueId())) {
                            player.sendMessage("§c촌장만 입금할 수 있습니다.");
                        } else {
                            data.removeGold(amount);
                            village.addGold(amount);
                            PlayerDataManager.save(player.getUniqueId());
                            VillageStorage.save(village);
                            player.sendMessage("§a입금 완료: " + amount + "G");
                        }
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }

    public static void promptWithdraw(Player player, Village village) {
        new ConversationFactory(ContinentPlugin.getInstance())
                .withFirstPrompt(new NumericPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return "출금할 금액을 입력하세요";
                    }

                    @Override
                    protected Prompt acceptValidatedInput(ConversationContext context, Number number) {
                        int amount = number.intValue();
                        if (amount <= 0 || village.getVault() < amount) {
                            player.sendMessage("§c출금할 수 없습니다.");
                        } else if (!village.getKing().equals(player.getUniqueId())) {
                            player.sendMessage("§c촌장만 출금할 수 있습니다.");
                        } else {
                            village.removeGold(amount);
                            PlayerData data = PlayerDataManager.get(player.getUniqueId());
                            data.addGold(amount);
                            PlayerDataManager.save(player.getUniqueId());
                            VillageStorage.save(village);
                            player.sendMessage("§a출금 완료: " + amount + "G");
                        }
                        return END_OF_CONVERSATION;
                    }
                })
                .withLocalEcho(false)
                .buildConversation(player).begin();
    }
}
