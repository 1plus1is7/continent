package me.continent.war;

import me.continent.ContinentPlugin;
import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.storage.NationStorage;
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
            player.sendMessage("§e/war declare <국가명> §7- 전쟁 선포");
            player.sendMessage("§e/war status §7- 전쟁 현황 확인");
            player.sendMessage("§e/war surrender §7- 항복");
            return true;
        }

        if (args[0].equalsIgnoreCase("declare")) {
            if (args.length < 2) {
                player.sendMessage("§c사용법: /war declare <국가명>");
                return true;
            }
            
            Nation attacker = NationManager.getByPlayer(player.getUniqueId());
            if (attacker == null) {
                player.sendMessage("§c소속된 국가이 없습니다.");
                return true;
            }
            if (!attacker.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국가 촌장만 전쟁을 선포할 수 있습니다.");
                return true;
            }
            Nation defender = NationManager.getByName(args[1]);
            if (defender == null) {
                player.sendMessage("§c해당 국가이 존재하지 않습니다.");
                return true;
            }
            if (WarManager.isAtWar(attacker.getName(), defender.getName())) {
                player.sendMessage("§c이미 해당 국가과 전쟁 중입니다.");
                return true;
            }

            double cost = ContinentPlugin.getInstance().getConfig()
                    .getDouble("war.declare-cost", 0);
            if (cost > 0 && attacker.getVault() < cost) {
                player.sendMessage("§c국가 금고가 부족합니다. 전쟁 선포 비용: " + cost + "G");
                return true;
            }
            if (cost > 0) {
                attacker.removeGold(cost);
                NationStorage.save(attacker);
            }

            WarManager.declareWar(attacker, defender);
            Bukkit.broadcastMessage("§c[전쟁] §f" + attacker.getName() + " 국가이 " + defender.getName() + " 국가에 전쟁을 선포했습니다!");
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가이 없습니다.");
                return true;
            }
            War war = WarManager.getWar(nation.getName());
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
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가이 없습니다.");
                return true;
            }
            if (!nation.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국가 촌장만 항복할 수 있습니다.");
                return true;
            }
            War war = WarManager.getWar(nation.getName());
            if (war == null) {
                player.sendMessage("§c전쟁 중이 아닙니다.");
                return true;
            }
            WarManager.surrender(nation);
            return true;
        }

        player.sendMessage("§c알 수 없는 하위 명령어입니다. /war 를 입력해 도움말을 확인하세요.");
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
            return NationManager.getAll().stream()
                    .map(Nation::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
