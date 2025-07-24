package me.continent.command;

import me.continent.enterprise.*;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
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
            player.sendMessage("§6[Enterprise 명령어 도움말]");
            player.sendMessage("§e/enterprise register <이름> <업종> §7- 새 기업 설립");
            return true;
        }

        if (args[0].equalsIgnoreCase("register")) {
            if (args.length < 3) {
                player.sendMessage("§c사용법: /enterprise register <이름> <업종>");
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
            double cost = 100; // simple flat registration cost
            if (data.getGold() < cost) {
                player.sendMessage("§c골드가 부족합니다. 비용: " + cost + "G");
                return true;
            }
            data.removeGold(cost);
            String id = UUID.randomUUID().toString();
            Enterprise ent = new Enterprise(id, name, type, player.getUniqueId(), System.currentTimeMillis());
            EnterpriseManager.register(ent);
            EnterpriseStorage.save(ent);
            player.sendMessage("§a기업이 설립되었습니다: " + name + "(" + type + ")");
            return true;
        }

        player.sendMessage("§c알 수 없는 하위 명령입니다.");
        return true;
    }
}
