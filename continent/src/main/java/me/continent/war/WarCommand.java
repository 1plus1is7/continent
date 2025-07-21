package me.continent.war;

import me.continent.nation.nation;
import me.continent.nation.nationManager;
import me.continent.village.Village;
import me.continent.village.VillageManager;
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

        if (args[0].equalsIgnoreCase("declare") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getnation() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            nation attacker = nationManager.getByName(village.getnation());
            if (!attacker.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 전쟁을 선포할 수 있습니다.");
                return true;
            }
            nation defender = nationManager.getByName(args[1]);
            if (defender == null) {
                player.sendMessage("§c해당 국가가 존재하지 않습니다.");
                return true;
            }
            if (WarManager.isAtWar(attacker.getName(), defender.getName())) {
                player.sendMessage("§c이미 해당 국가와 전쟁 중입니다.");
                return true;
            }
            WarManager.declareWar(attacker, defender);
            Bukkit.broadcastMessage("§c[전쟁] §f" + attacker.getName() + " 국가가 " + defender.getName() + " 국가에 전쟁을 선포했습니다!");
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getnation() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            War war = WarManager.getWar(village.getnation());
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
            if (village == null || village.getnation() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            nation kingdom = nationManager.getByName(village.getnation());
            if (!kingdom.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 항복할 수 있습니다.");
                return true;
            }
            War war = WarManager.getWar(kingdom.getName());
            if (war == null) {
                player.sendMessage("§c전쟁 중이 아닙니다.");
                return true;
            }
            WarManager.surrender(kingdom);
            String winner = war.getAttacker().equalsIgnoreCase(kingdom.getName()) ? war.getDefender() : war.getAttacker();
            Bukkit.broadcastMessage("§e[전쟁] §f" + kingdom.getName() + " 국가가 항복했습니다. 승자는 " + winner + " 국가입니다.");
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
            return nationManager.getAll().stream()
                    .map(nation::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
