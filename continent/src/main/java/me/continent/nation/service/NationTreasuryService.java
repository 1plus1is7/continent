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

    public enum Mode { DEPOSIT, WITHDRAW }

    static class AmountHolder implements InventoryHolder {
        private final Nation nation;
        private final Mode mode;
        private int amount;
        private Inventory inv;
        AmountHolder(Nation n, Mode m, int a) { this.nation = n; this.mode = m; this.amount = a; }
        void setInventory(Inventory inv) { this.inv = inv; }
        @Override public Inventory getInventory() { return inv; }
        public Nation getNation() { return nation; }
        public Mode getMode() { return mode; }
        public int getAmount() { return amount; }
        public void setAmount(int a) { this.amount = Math.max(1, a); }
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

    public static void openAmount(Player player, Nation nation, Mode mode, int amount) {
        AmountHolder holder = new AmountHolder(nation, mode, amount);
        String title = mode == Mode.DEPOSIT ? "금고 입금" : "금고 출금";
        Inventory inv = Bukkit.createInventory(holder, 45, title);
        holder.setInventory(inv);
        fill(inv);
        renderAmount(inv, holder, player);
        player.openInventory(inv);
    }

    static void renderAmount(Inventory inv, AmountHolder holder, Player player) {
        int amt = holder.getAmount();
        inv.setItem(20, amtButton(Material.REDSTONE, "-10G", amt - 10));
        inv.setItem(21, amtButton(Material.REDSTONE, "-1G", amt - 1));
        inv.setItem(23, amtButton(Material.LIME_DYE, "+1G", amt + 1));
        inv.setItem(24, amtButton(Material.LIME_DYE, "+10G", amt + 10));
        ItemStack info = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = info.getItemMeta();
        String prefix = holder.getMode() == Mode.DEPOSIT ? "입금" : "출금";
        meta.setDisplayName(prefix + ": " + amt + "G");
        info.setItemMeta(meta);
        inv.setItem(31, info);
        inv.setItem(41, maxButton(player, holder));
        inv.setItem(38, createItem(Material.BARRIER, "취소"));
        inv.setItem(40, createItem(Material.EMERALD_BLOCK, "확인"));
        inv.setItem(42, createItem(Material.ARROW, "뒤로"));
    }

    private static ItemStack amtButton(Material mat, String name, int amount) {
        if (amount < 1) amount = 1;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(java.util.List.of("§7금액: " + amount + "G"));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack maxButton(Player player, AmountHolder holder) {
        int max = getMaxAmount(player, holder);
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("최대");
        meta.setLore(java.util.List.of("§7가능: " + max));
        item.setItemMeta(meta);
        return item;
    }

    static int getMaxAmount(Player player, AmountHolder holder) {
        if (holder.getMode() == Mode.DEPOSIT) {
            return (int) Math.max(1, PlayerDataManager.get(player.getUniqueId()).getGold());
        } else {
            return (int) Math.max(1, holder.getNation().getVault());
        }
    }

    public static void perform(Player player, AmountHolder holder) {
        int amount = holder.getAmount();
        Nation nation = holder.getNation();
        if (!nation.getKing().equals(player.getUniqueId())) {
            player.sendMessage("§c촌장만 수행할 수 있습니다.");
            return;
        }
        PlayerData data = PlayerDataManager.get(player.getUniqueId());
        if (holder.getMode() == Mode.DEPOSIT) {
            if (data.getGold() < amount) {
                player.sendMessage("§c보유 골드가 부족합니다.");
                return;
            }
            data.removeGold(amount);
            nation.addGold(amount);
            player.sendMessage("§a입금 완료: " + amount + "G");
        } else {
            if (nation.getVault() < amount) {
                player.sendMessage("§c금고가 부족합니다.");
                return;
            }
            nation.removeGold(amount);
            data.addGold(amount);
            player.sendMessage("§a출금 완료: " + amount + "G");
        }
        PlayerDataManager.save(player.getUniqueId());
        NationStorage.save(nation);
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
