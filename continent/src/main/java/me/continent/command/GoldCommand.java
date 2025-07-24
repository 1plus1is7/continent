package me.continent.command;

import me.continent.economy.CentralBank;
import me.continent.economy.gui.GoldMenuService;
import org.bukkit.Bukkit;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;
import org.bukkit.inventory.ItemStack;

public class GoldCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
            GoldMenuService.openMenu(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage("§6[골드 명령어 도움말]");
            player.sendMessage("§e/gold convert <수량> §7- 골드를 사용해 금괴를 획득합니다.");
            player.sendMessage("§e/gold exchange [수량] §7- 금을 골드로 환전합니다.");
            player.sendMessage("§e/gold balance §7- 현재 보유 골드를 확인합니다.");
            player.sendMessage("§e/gold pay <플레이어> <금액> §7- 플레이어에게 골드를 송금합니다.");
            player.sendMessage("§e/gold gui §7- 골드 메뉴 열기");
            return true;
        }


        // ✅ 보유 골드 확인
        if (args[0].equalsIgnoreCase("balance")) {
            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            player.sendMessage("§6[골드] §f현재 보유 골드: §e" + data.getGold() + "G");
            return true;
        }

        if (args[0].equalsIgnoreCase("pay")) {
            if (args.length < 3) {
                player.sendMessage("§c사용법: /gold pay <플레이어> <금액>");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage("§c해당 플레이어는 온라인 상태가 아닙니다.");
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0) {
                    player.sendMessage("§c송금 금액은 1 이상이어야 합니다.");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§c금액은 숫자만 입력해야 합니다.");
                return true;
            }

            PlayerData senderData = PlayerDataManager.get(player.getUniqueId());
            if (senderData.getGold() < amount) {
                player.sendMessage("§c보유 골드가 부족합니다.");
                return true;
            }

            PlayerData targetData = PlayerDataManager.get(target.getUniqueId());

            senderData.removeGold(amount);
            targetData.addGold(amount);

            player.sendMessage("§6[송금] §f" + target.getName() + "에게 §e" + amount + "G §f를 송금했습니다.");
            target.sendMessage("§6[입금] §f" + player.getName() + "로부터 §e" + amount + "G §f를 받았습니다!");

            return true;
        }
        if (args[0].equalsIgnoreCase("convert")) {
            if (args.length < 2) {
                player.sendMessage("§c사용법: /gold convert <수량>");
                return true;
            }

            int quantity;
            try {
                quantity = Math.max(1, Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                player.sendMessage("§c수량은 숫자여야 합니다.");
                return true;
            }

            double rate = CentralBank.getExchangeRate();
            int totalCost = (int) Math.round(rate * quantity);

            PlayerData data = PlayerDataManager.get(player.getUniqueId());

            if (data.getGold() < totalCost) {
                player.sendMessage("§c골드가 부족합니다. (필요: " + totalCost + "G)");
                return true;
            }

            ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT, quantity);
            HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(goldIngot);
            if (!leftovers.isEmpty()) {
                player.sendMessage("§c인벤토리에 금괴를 넣을 공간이 부족합니다.");
                return true;
            }

            data.removeGold(totalCost);

            player.sendMessage("§6[중앙은행] §e" + totalCost + "G §f를 소모하여 금괴 " + quantity + "개를 획득했습니다.");
            player.sendMessage("§6[환율] §f1개당 " + rate + "G 기준");
            player.sendMessage("§6[잔액] §f현재 보유 골드: §e" + data.getGold() + "G");

            return true;
        }


        // ✅ 환전 명령어
        if (args[0].equalsIgnoreCase("exchange")) {
            int quantity = 1;
            if (args.length >= 2) {
                try {
                    quantity = Math.max(1, Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    player.sendMessage("§c수량은 1 이상의 숫자여야 합니다.");
                    return true;
                }
            }

            ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT);
            if (!player.getInventory().containsAtLeast(goldIngot, quantity)) {
                player.sendMessage("§c금괴가 부족합니다. (보유 수량 < " + quantity + ")");
                return true;
            }

            // 금 차감
            player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, quantity));

            double rate = CentralBank.getExchangeRate();
            int totalG = (int) Math.round(rate * quantity);

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            data.addGold(totalG);

            player.sendMessage("§6[중앙은행] §f금 " + quantity + "개를 환전하여 §e" + totalG + "G §f를 획득했습니다.");
            player.sendMessage("§6[환율] §f1개당 " + rate + "G 기준");
            player.sendMessage("§6[잔액] §f현재 보유 골드: §e" + data.getGold() + "G");

            CentralBank.recordExchange();
            return true;
        }

        // ✅ 알 수 없는 명령어
        player.sendMessage("§c알 수 없는 하위 명령어입니다. /gold 를 입력해 도움말을 확인하세요.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subs = Arrays.asList("convert", "exchange", "balance", "pay");

        if (args.length == 1) {
            return subs.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("pay")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
