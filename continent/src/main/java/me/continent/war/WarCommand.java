package me.continent.war;

import me.continent.ContinentPlugin;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.storage.VillageStorage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class WarCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6[War 명령어]");
            player.sendMessage("§e/war declare <마을명> §7- 전쟁 선포");
            player.sendMessage("§e/war status §7- 전쟁 현황 확인");
            player.sendMessage("§e/war surrender §7- 항복");
            return true;
        }

        if (args[0].equalsIgnoreCase("declare") && args.length >= 2) {
            Village attacker = VillageManager.getByPlayer(player.getUniqueId());
            if (attacker == null) {
                player.sendMessage("§c소속된 마을이 없습니다.");
                return true;
            }
            if (!attacker.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c마을 촌장만 전쟁을 선포할 수 있습니다.");
                return true;
            }
            Village defender = VillageManager.getByName(args[1]);
            if (defender == null) {
                player.sendMessage("§c해당 마을이 존재하지 않습니다.");
                return true;
            }
            if (WarManager.isAtWar(attacker.getName(), defender.getName())) {
                player.sendMessage("§c이미 해당 마을과 전쟁 중입니다.");
                return true;
            }

            double cost = ContinentPlugin.getInstance().getConfig()
                    .getDouble("war.declare-cost", 0);
            if (cost > 0 && attacker.getVault() < cost) {
                player.sendMessage("§c마을 금고가 부족합니다. 전쟁 선포 비용: " + cost + "G");
                return true;
            }
            if (cost > 0) {
                attacker.removeGold(cost);
                VillageStorage.save(attacker);
            }

            WarManager.declareWar(attacker, defender);
            Bukkit.broadcastMessage("§c[전쟁] §f" + attacker.getName() + " 마을이 " + defender.getName() + " 마을에 전쟁을 선포했습니다!");
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을이 없습니다.");
                return true;
            }
            War war = WarManager.getWar(village.getName());
            if (war == null) {
                player.sendMessage("§c현재 진행 중인 전쟁이 없습니다.");
                return true;
            }
            player.sendMessage("§6[전쟁 현황]");
            player.sendMessage("§f공격국: §e" + war.getAttacker());
            player.sendMessage("§f방어국: §e" + war.getDefender());
            return true;
        }

        if (args[0].equalsIgnoreCase("surrender")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을이 없습니다.");
                return true;
            }
            if (!village.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c마을 촌장만 항복할 수 있습니다.");
                return true;
            }
            War war = WarManager.getWar(village.getName());
            if (war == null) {
                player.sendMessage("§c전쟁 중이 아닙니다.");
                return true;
            }
            WarManager.surrender(village);
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subs = Arrays.asList("declare", "status", "surrender");

        if (args.length == 1) {
            return subs.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("declare")) {
            return VillageManager.getAll().stream()
                    .map(Village::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
