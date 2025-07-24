package me.continent.command;

import me.continent.specialty.SpecialtyGood;
import me.continent.specialty.SpecialtyManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class SpecialtyCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6[특산품 명령어]");
            player.sendMessage("§e/specialty list §7- 모든 특산품 목록을 표시합니다.");
            player.sendMessage("§e/specialty produce <ID> [수량] §7- 특산품을 생산합니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            Collection<SpecialtyGood> all = SpecialtyManager.getAll();
            if (all.isEmpty()) {
                player.sendMessage("§c등록된 특산품이 없습니다.");
                return true;
            }
            player.sendMessage("§6[특산품 목록]");
            for (SpecialtyGood g : all) {
                player.sendMessage("§e" + g.getId() + " §7- " + g.getName());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("produce")) {
            if (args.length < 2) {
                player.sendMessage("§c사용법: /specialty produce <ID> [수량]");
                return true;
            }
            String id = args[1];
            int amount = 1;
            if (args.length >= 3) {
                try {
                    amount = Math.max(1, Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    player.sendMessage("§c수량은 숫자여야 합니다.");
                    return true;
                }
            }
            SpecialtyGood good = SpecialtyManager.get(id);
            if (good == null) {
                player.sendMessage("§c존재하지 않는 특산품입니다.");
                return true;
            }
            player.getInventory().addItem(good.toItemStack(amount));
            player.sendMessage("§e" + good.getName() + " §f" + amount + "개를 획득했습니다.");
            return true;
        }

        player.sendMessage("§c알 수 없는 하위 명령어입니다.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subs = Arrays.asList("list", "produce");

        if (args.length == 1) {
            return subs.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("produce")) {
            return SpecialtyManager.getAll().stream()
                    .map(SpecialtyGood::getId)
                    .filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
