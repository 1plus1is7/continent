package me.continent.command;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.kingdom.service.UpgradeService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VillageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§e/village upgrade §7- 마을을 국가로 승격");
            return true;
        }

        if (args[0].equalsIgnoreCase("upgrade")) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null) {
                player.sendMessage("§c소속된 마을이 없습니다.");
                return true;
            }
            if (!kingdom.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 업그레이드할 수 있습니다.");
                return true;
            }
            if (kingdom.isNation()) {
                player.sendMessage("§c이미 국가입니다.");
                return true;
            }
            if (kingdom.getMembers().size() < UpgradeService.getRequiredMembers()) {
                player.sendMessage("§c구성원이 " + UpgradeService.getRequiredMembers() + "명 이상 필요합니다.");
                return true;
            }
            if (kingdom.getTreasury() < UpgradeService.getRequiredTreasury()) {
                player.sendMessage("§c국고가 부족합니다. 필요 금액: " + UpgradeService.getRequiredTreasury() + "G");
                return true;
            }
            if (UpgradeService.upgrade(kingdom)) {
                player.sendMessage("§a마을이 국가로 승격되었습니다!");
            } else {
                player.sendMessage("§c업그레이드에 실패했습니다.");
            }
            return true;
        }
        return true;
    }
}
