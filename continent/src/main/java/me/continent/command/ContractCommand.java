package me.continent.command;

import me.continent.enterprise.Enterprise;
import me.continent.enterprise.EnterpriseManager;
import me.continent.enterprise.contract.Contract;
import me.continent.enterprise.contract.ContractManager;
import me.continent.enterprise.contract.ContractState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/** Simple command to view and accept contracts. */
public class ContractCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6[계약 목록]");
            for (Contract c : ContractManager.getAvailable()) {
                player.sendMessage("§e" + c.getId() + " §f" + c.getDescription() + " (" + c.getGrade() + ") 보상:" + c.getReward());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("accept") && args.length >= 2) {
            UUID cid;
            try {
                cid = UUID.fromString(args[1]);
            } catch (IllegalArgumentException e) {
                player.sendMessage("§c잘못된 계약 ID입니다.");
                return true;
            }
            if (!EnterpriseManager.hasEnterprise(player.getUniqueId())) {
                player.sendMessage("§c기업이 없습니다.");
                return true;
            }
            Enterprise ent = EnterpriseManager.getByOwner(player.getUniqueId()).iterator().next();
            if (ContractManager.accept(cid, ent.getId())) {
                player.sendMessage("§a계약을 수락했습니다.");
            } else {
                player.sendMessage("§c해당 계약을 찾을 수 없습니다.");
            }
            return true;
        }

        player.sendMessage("§c사용법: /contract [accept <id>]");
        return true;
    }
}
