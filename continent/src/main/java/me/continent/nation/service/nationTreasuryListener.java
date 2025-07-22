package me.continent.nation.service;

import me.continent.nation.nation;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class nationTreasuryListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof nationTreasuryService.TreasuryHolder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            nation kingdom = holder.getnation();
            if (slot == 2) {
                DepositGUI.open(player, kingdom);
            } else if (slot == 4) {
                WithdrawGUI.open(player, kingdom);
            } else if (slot == 6) {
                TaxGUI.open(player, kingdom);
            }
        } else if (inv.getHolder() instanceof DepositGUI.Holder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            nation kingdom = holder.getnation();
            int slot = event.getRawSlot();
            switch (slot) {
                case 20 -> { holder.setAmount(holder.getAmount() - 10); DepositGUI.render(inv, holder.getAmount()); }
                case 21 -> { holder.setAmount(holder.getAmount() - 1); DepositGUI.render(inv, holder.getAmount()); }
                case 23 -> { holder.setAmount(holder.getAmount() + 1); DepositGUI.render(inv, holder.getAmount()); }
                case 24 -> { holder.setAmount(holder.getAmount() + 10); DepositGUI.render(inv, holder.getAmount()); }
                case 38 -> player.closeInventory();
                case 42 -> nationTreasuryService.openMenu(player, kingdom);
                case 40 -> {
                    int amt = holder.getAmount();
                    PlayerData data = PlayerDataManager.get(player.getUniqueId());
                    if (!kingdom.getLeader().equals(player.getUniqueId())) {
                        player.sendMessage("§c국왕만 입금할 수 있습니다.");
                        return;
                    }
                    if (amt <= 0 || data.getGold() < amt) {
                        player.sendMessage("§c입금할 수 없습니다.");
                        return;
                    }
                    data.removeGold(amt);
                    kingdom.addGold(amt);
                    PlayerDataManager.save(player.getUniqueId());
                    me.continent.nation.nationStorage.save(kingdom);
                    player.sendMessage("§a입금 완료: " + amt + "G");
                    player.closeInventory();
                }
            }
        } else if (inv.getHolder() instanceof WithdrawGUI.Holder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            nation kingdom = holder.getnation();
            int slot = event.getRawSlot();
            switch (slot) {
                case 20 -> { holder.setAmount(holder.getAmount() - 10); WithdrawGUI.render(inv, holder.getAmount()); }
                case 21 -> { holder.setAmount(holder.getAmount() - 1); WithdrawGUI.render(inv, holder.getAmount()); }
                case 23 -> { holder.setAmount(holder.getAmount() + 1); WithdrawGUI.render(inv, holder.getAmount()); }
                case 24 -> { holder.setAmount(holder.getAmount() + 10); WithdrawGUI.render(inv, holder.getAmount()); }
                case 38 -> player.closeInventory();
                case 42 -> nationTreasuryService.openMenu(player, kingdom);
                case 40 -> {
                    int amt = holder.getAmount();
                    if (!kingdom.getLeader().equals(player.getUniqueId())) {
                        player.sendMessage("§c국왕만 출금할 수 있습니다.");
                        return;
                    }
                    if (amt <= 0 || kingdom.getTreasury() < amt) {
                        player.sendMessage("§c출금할 수 없습니다.");
                        return;
                    }
                    kingdom.removeGold(amt);
                    PlayerData data = PlayerDataManager.get(player.getUniqueId());
                    data.addGold(amt);
                    PlayerDataManager.save(player.getUniqueId());
                    me.continent.nation.nationStorage.save(kingdom);
                    player.sendMessage("§a출금 완료: " + amt + "G");
                    player.closeInventory();
                }
            }
        } else if (inv.getHolder() instanceof TaxGUI.Holder holder) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            nation kingdom = holder.getnation();
            int slot = event.getRawSlot();
            switch (slot) {
                case 20 -> { holder.setRate(holder.getRate() - 10); TaxGUI.render(inv, holder.getRate()); }
                case 21 -> { holder.setRate(holder.getRate() - 1); TaxGUI.render(inv, holder.getRate()); }
                case 23 -> { holder.setRate(holder.getRate() + 1); TaxGUI.render(inv, holder.getRate()); }
                case 24 -> { holder.setRate(holder.getRate() + 10); TaxGUI.render(inv, holder.getRate()); }
                case 38 -> player.closeInventory();
                case 42 -> nationTreasuryService.openMenu(player, kingdom);
                case 40 -> {
                    if (!kingdom.getLeader().equals(player.getUniqueId())) {
                        player.sendMessage("§c국왕만 세율을 설정할 수 있습니다.");
                        return;
                    }
                    int rate = holder.getRate();
                    kingdom.setTaxRate(rate);
                    me.continent.nation.nationStorage.save(kingdom);
                    player.sendMessage("§a세율이 설정되었습니다: " + rate + "%");
                    player.closeInventory();
                }
            }
        }
    }
}
