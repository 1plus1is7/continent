package me.continent.command;

import me.continent.enterprise.*;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.enterprise.contract.Contract;
import me.continent.enterprise.contract.ContractManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/** Handles /enterprise commands. */
public class EnterpriseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            if (EnterpriseManager.hasEnterprise(player.getUniqueId())) {
                Enterprise ent = EnterpriseManager.getByOwner(player.getUniqueId()).iterator().next();
                me.continent.enterprise.gui.EnterpriseMenuService.openMain(player, ent);
            } else {
                player.sendMessage("§e[기업] 아직 기업이 없습니다. 지금 설립하시겠습니까?");
                me.continent.enterprise.gui.EnterpriseMenuService.openRegister(player);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            me.continent.enterprise.gui.EnterpriseListGUI.open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("setsymbol")) {
            if (!EnterpriseManager.hasEnterprise(player.getUniqueId())) {
                player.sendMessage("§c보유한 기업이 없습니다.");
                return true;
            }
            Enterprise ent = EnterpriseManager.getByOwner(player.getUniqueId()).iterator().next();
            var item = player.getInventory().getItemInMainHand();
            if (item == null || !item.getType().name().endsWith("BANNER")) {
                player.sendMessage("§c손에 배너를 들고 있어야 합니다.");
                return true;
            }
            ent.setSymbol(item.clone());
            EnterpriseService.save(ent);
            player.sendMessage("§a기업 상징이 업데이트되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("register")) {
            if (args.length < 3) {
                me.continent.enterprise.gui.EnterpriseMenuService.openRegister(player);
                return true;
            }
            String name = args[1];
            EnterpriseType type;
            try {
                type = EnterpriseType.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException ex) {
                player.sendMessage("§c업종 종류: " + java.util.Arrays.toString(EnterpriseType.values()));
                return true;
            }
            if (EnterpriseManager.nameExists(name)) {
                player.sendMessage("§c이미 존재하는 기업 이름입니다.");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            double cost = 100;
            var info = me.continent.enterprise.EnterpriseTypeConfig.get(type);
            if (info != null) cost = info.getCost();
            if (data.getGold() < cost) {
                player.sendMessage("§c골드가 부족합니다. 비용: " + cost + "G");
                return true;
            }
            data.removeGold(cost);
            String id = UUID.randomUUID().toString();
            Enterprise ent = new Enterprise(id, name, type, player.getUniqueId(), System.currentTimeMillis());
            EnterpriseManager.register(ent);
            EnterpriseService.save(ent);
            player.sendMessage("§a기업이 설립되었습니다: " + name + "(" + type + ")");
            return true;
        }

        if (args[0].equalsIgnoreCase("contract")) {
            if (args.length == 1) {
                player.sendMessage("§6[계약 목록]");
                for (Contract c : ContractManager.getAvailable()) {
                    player.sendMessage("§e" + c.getId() + " §f" + c.getDescription() +
                            " (" + c.getGrade() + ") 보상:" + c.getReward());
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("accept") && args.length >= 3) {
                java.util.UUID cid;
                try {
                    cid = java.util.UUID.fromString(args[2]);
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
            player.sendMessage("§c사용법: /enterprise contract [accept <id>]");
            return true;
        }

        player.sendMessage("§c알 수 없는 하위 명령입니다.");
        return true;
    }
}
