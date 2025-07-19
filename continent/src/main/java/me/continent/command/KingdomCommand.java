package me.continent.command;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.kingdom.KingdomStorage;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KingdomCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6[Kingdom 명령어]");
            player.sendMessage("§e/kingdom create <이름> §7- 국가 생성");
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && args.length >= 2) {
            String name = args[1];
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c자신의 마을의 국왕만 국가를 생성할 수 있습니다.");
                return true;
            }
            if (village.getKingdom() != null) {
                player.sendMessage("§c이미 다른 국가에 속한 마을입니다.");
                return true;
            }
            if (KingdomManager.getByName(name) != null) {
                player.sendMessage("§c이미 존재하는 국가 이름입니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.createKingdom(name, village);
            KingdomStorage.save(kingdom);
            player.sendMessage("§a국가가 생성되었습니다: §e" + name);
            return true;
        }

        return true;
    }
}
