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
            player.sendMessage("§6[크라운 명령어 도움말]");
            player.sendMessage("§e/crown convert <금|다이아> <수량> §7- 크라운을 사용해 광물을 구매합니다.");
            player.sendMessage("§e/crown exchange <금|다이아> [수량] §7- 광물을 크라운으로 환전합니다.");
            player.sendMessage("§e/crown rate §7- 현재 환율 확인");
            player.sendMessage("§e/crown balance §7- 현재 보유 크라운을 확인합니다.");
            player.sendMessage("§e/crown pay <플레이어> <금액> §7- 플레이어에게 크라운을 송금합니다.");
            player.sendMessage("§e/crown gui §7- 크라운 메뉴 열기");
            return true;
        }


        // ✅ 보유 크라운 확인
        if (args[0].equalsIgnoreCase("balance")) {
            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            player.sendMessage("§6[크라운] §f현재 보유 크라운: §e" + data.getGold() + "C");
            return true;
        }

        if (args[0].equalsIgnoreCase("rate")) {
            player.sendMessage("§6[환율] §f현재 환율: §e" + CentralBank.getExchangeRate() + "C");
            return true;
        }

        if (args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("continent.admin")) {
                player.sendMessage("§c관리자 권한이 필요합니다.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage("§e/crown admin addgold <수량>");
                player.sendMessage("§e/crown admin adddiamond <수량>");
                player.sendMessage("§e/crown admin setrate <값>");
                player.sendMessage("§e/crown admin setdrate <값>");
                return true;
            }
            if (args[1].equalsIgnoreCase("addgold") && args.length >= 3) {
                try {
                    int amt = Integer.parseInt(args[2]);
                    CentralBank.addGold(amt);
                    player.sendMessage("§6[중앙은행] §f금고에 금 " + amt + "개를 추가했습니다. 현재 보유: " + CentralBank.getGold());
                } catch (NumberFormatException e) {
                    player.sendMessage("§c수량은 숫자여야 합니다.");
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("adddiamond") && args.length >= 3) {
                try {
                    int amt = Integer.parseInt(args[2]);
                    CentralBank.addDiamond(amt);
                    player.sendMessage("§6[중앙은행] §f금고에 다이아 " + amt + "개를 추가했습니다. 현재 보유: " + CentralBank.getDiamond());
                } catch (NumberFormatException e) {
                    player.sendMessage("§c수량은 숫자여야 합니다.");
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("setrate") && args.length >= 3) {
                try {
                    double val = Double.parseDouble(args[2]);
                    CentralBank.setExchangeRate(val);
                    CentralBank.setAutoRate(false);
                    player.sendMessage("§6[중앙은행] §f환율을 " + CentralBank.getExchangeRate() + "G 로 설정했습니다.");
                } catch (NumberFormatException e) {
                    player.sendMessage("§c숫자를 입력해야 합니다.");
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("setdrate") && args.length >= 3) {
                try {
                    double val = Double.parseDouble(args[2]);
                    CentralBank.setDiamondExchangeRate(val);
                    CentralBank.setAutoRate(false);
                    player.sendMessage("§6[중앙은행] §f다이아 환율을 " + CentralBank.getDiamondExchangeRate() + "G 로 설정했습니다.");
                } catch (NumberFormatException e) {
                    player.sendMessage("§c숫자를 입력해야 합니다.");
                }
                return true;
            }
            player.sendMessage("§c사용법: /crown admin <addgold|adddiamond|setrate|setdrate>");
            return true;
        }

        if (args[0].equalsIgnoreCase("pay")) {
            if (args.length < 3) {
                player.sendMessage("§c사용법: /crown pay <플레이어> <금액>");
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
                player.sendMessage("§c보유 크라운이 부족합니다.");
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
                player.sendMessage("§c사용법: /crown convert <금|다이아> <수량>");
                return true;
            }

            boolean useDiamond = false;
            int idx = 1;
            try {
                Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                useDiamond = args[1].equalsIgnoreCase("다이아") || args[1].equalsIgnoreCase("diamond") || args[1].equalsIgnoreCase("dia") || args[1].equalsIgnoreCase("d");
                idx = 2;
            }

            if (args.length < idx + 1) {
                player.sendMessage("§c사용법: /crown convert <금|다이아> <수량>");
                return true;
            }

            int quantity;
            try {
                quantity = Math.max(1, Integer.parseInt(args[idx]));
            } catch (NumberFormatException e) {
                player.sendMessage("§c수량은 숫자여야 합니다.");
                return true;
            }

            double rate = useDiamond ? CentralBank.getDiamondExchangeRate() : CentralBank.getExchangeRate();
            int totalCost = (int) Math.round(rate * quantity);

            PlayerData data = PlayerDataManager.get(player.getUniqueId());

            if (data.getGold() < totalCost) {
                player.sendMessage("§c크라운이 부족합니다. (필요: " + totalCost + "C)");
                return true;
            }

            if (useDiamond) {
                if (!CentralBank.withdrawDiamond(quantity)) {
                    player.sendMessage("§c중앙은행 금고에 다이아가 부족합니다.");
                    return true;
                }
                ItemStack diamondItem = new ItemStack(Material.DIAMOND, quantity);
                HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(diamondItem);
                if (!leftovers.isEmpty()) {
                    CentralBank.addDiamond(quantity);
                    player.sendMessage("§c인벤토리에 다이아를 넣을 공간이 부족합니다.");
                    return true;
                }
            } else {
                if (!CentralBank.withdrawGold(quantity)) {
                    player.sendMessage("§c중앙은행 금고에 금이 부족합니다.");
                    return true;
                }
                ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT, quantity);
                HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(goldIngot);
                if (!leftovers.isEmpty()) {
                    CentralBank.addGold(quantity);
                    player.sendMessage("§c인벤토리에 금괴를 넣을 공간이 부족합니다.");
                    return true;
                }
            }

            data.removeGold(totalCost);

            if (useDiamond) {
                player.sendMessage("§6[중앙은행] §e" + totalCost + "C §f를 소모하여 다이아 " + quantity + "개를 획득했습니다.");
            } else {
                player.sendMessage("§6[중앙은행] §e" + totalCost + "C §f를 소모하여 금괴 " + quantity + "개를 획득했습니다.");
            }
            player.sendMessage("§6[환율] §f1개당 " + rate + "C 기준");
            player.sendMessage("§6[잔액] §f현재 보유 크라운: §e" + data.getGold() + "C");

            return true;
        }


        // ✅ 환전 명령어
        if (args[0].equalsIgnoreCase("exchange")) {
            boolean useDiamond = false;
            int quantity = 1;
            int idx = 1;
            if (args.length >= 2) {
                try {
                    quantity = Math.max(1, Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    useDiamond = args[1].equalsIgnoreCase("다이아") || args[1].equalsIgnoreCase("diamond") || args[1].equalsIgnoreCase("dia") || args[1].equalsIgnoreCase("d");
                    idx = 2;
                }
            }
            if (args.length >= idx + 1) {
                try {
                    quantity = Math.max(1, Integer.parseInt(args[idx]));
                } catch (NumberFormatException e) {
                    player.sendMessage("§c수량은 1 이상의 숫자여야 합니다.");
                    return true;
                }
            }

            ItemStack materialItem = new ItemStack(useDiamond ? Material.DIAMOND : Material.GOLD_INGOT);
            if (!player.getInventory().containsAtLeast(materialItem, quantity)) {
                player.sendMessage(useDiamond ? "§c다이아가 부족합니다. (보유 수량 < " + quantity + ")" : "§c금괴가 부족합니다. (보유 수량 < " + quantity + ")");
                return true;
            }

            // 광물 차감
            player.getInventory().removeItem(new ItemStack(useDiamond ? Material.DIAMOND : Material.GOLD_INGOT, quantity));
            if (useDiamond) {
                CentralBank.addDiamond(quantity);
            } else {
                CentralBank.addGold(quantity);
            }

            double rate = useDiamond ? CentralBank.getDiamondExchangeRate() : CentralBank.getExchangeRate();
            int totalG = (int) Math.round(rate * quantity);

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            data.addGold(totalG);

            if (useDiamond) {
                player.sendMessage("§6[중앙은행] §f다이아 " + quantity + "개를 환전하여 §e" + totalG + "C §f를 획득했습니다.");
            } else {
                player.sendMessage("§6[중앙은행] §f금 " + quantity + "개를 환전하여 §e" + totalG + "C §f를 획득했습니다.");
            }
            player.sendMessage("§6[환율] §f1개당 " + rate + "C 기준");
            player.sendMessage("§6[잔액] §f현재 보유 크라운: §e" + data.getGold() + "C");

            return true;
        }

        // ✅ 알 수 없는 명령어
        player.sendMessage("§c알 수 없는 하위 명령어입니다. /crown 를 입력해 도움말을 확인하세요.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subs = Arrays.asList("convert", "exchange", "balance", "pay", "rate", "admin");

        if (args.length == 1) {
            return subs.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("convert") || args[0].equalsIgnoreCase("exchange"))) {
            return Arrays.asList("gold", "diamond");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("pay")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            return Arrays.asList("addgold", "adddiamond", "setrate", "setdrate").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
