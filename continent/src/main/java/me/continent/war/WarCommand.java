package me.continent.war;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarCommand implements CommandExecutor {
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
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom attacker = KingdomManager.getByName(village.getKingdom());
            if (!attacker.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 전쟁을 선포할 수 있습니다.");
                return true;
            }
            Kingdom defender = KingdomManager.getByName(args[1]);
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
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            War war = WarManager.getWar(village.getKingdom());
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
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (!kingdom.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 항복할 수 있습니다.");
                return true;
            }
            War war = WarManager.getWar(kingdom.getName());
            if (war == null) {
                player.sendMessage("§c전쟁 중이 아닙니다.");
                return true;
            }
            WarManager.endWar(war);
            Bukkit.broadcastMessage("§e[전쟁] §f" + kingdom.getName() + " 국가가 항복했습니다.");
            return true;
        }

        return true;
    }
}
