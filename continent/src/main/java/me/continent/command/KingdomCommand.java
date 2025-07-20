package me.continent.command;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.kingdom.KingdomStorage;
import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.player.PlayerDataManager;
import me.continent.player.PlayerData;
import me.continent.storage.VillageStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class KingdomCommand implements CommandExecutor {
    private static final int CREATE_COST = 100;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6[Kingdom 명령어]");
            player.sendMessage("§e/kingdom create <이름> <수도> §7- 국가 생성");
            player.sendMessage("§e/kingdom disband §7- 국가 해산");
            player.sendMessage("§e/kingdom info §7- 국가 정보");
            player.sendMessage("§e/kingdom list §7- 국가 목록");
            player.sendMessage("§e/kingdom members §7- 소속 마을 목록");
            player.sendMessage("§e/kingdom setcapital <마을명> §7- 수도 변경");
            player.sendMessage("§e/kingdom setking <플레이어> §7- 국왕 위임");
            player.sendMessage("§e/kingdom addvillage <마을명> §7- 마을 초대");
            player.sendMessage("§e/kingdom removevillage <마을명> §7- 마을 제외");
            player.sendMessage("§e/kingdom accept <국가명> §7- 초대 수락");
            player.sendMessage("§e/kingdom deny <국가명> §7- 초대 거절");
            player.sendMessage("§e/kingdom leave §7- 국가 탈퇴");
            player.sendMessage("§e/kingdom treasury <subcommand> §7- 국고 관리");
            player.sendMessage("§e/kingdom specialty §7- 특산품 관리 GUI 열기");
            player.sendMessage("§e/kingdom chat §7- 국가 채팅 토글");
            player.sendMessage("§e/kingdom spawn [마을명] §7- 마을 스폰으로 이동");
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && args.length >= 3) {
            String name = args[1];
            String capitalName = args[2];
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c자신의 마을의 국왕만 국가를 생성할 수 있습니다.");
                return true;
            }
            if (!village.getName().equalsIgnoreCase(capitalName)) {
                player.sendMessage("§c수도 마을은 자신의 마을만 지정할 수 있습니다.");
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
            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (data.getGold() < CREATE_COST) {
                player.sendMessage("§c국가를 생성하려면 " + CREATE_COST + "G가 필요합니다.");
                return true;
            }

            data.removeGold(CREATE_COST);
            Kingdom kingdom = KingdomManager.createKingdom(name, village);
            KingdomStorage.save(kingdom);
            VillageStorage.save(village);
            Bukkit.broadcastMessage("§e[국가] §f" + name + " 국가가 건국되었습니다!");
            return true;
        }

        if (args[0].equalsIgnoreCase("disband")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (kingdom == null || !kingdom.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 국가를 해산할 수 있습니다.");
                return true;
            }

            KingdomManager.unregister(kingdom);
            for (String vName : kingdom.getVillages()) {
                Village v = VillageManager.getByName(vName);
                if (v != null) {
                    v.setKingdom(null);
                    VillageStorage.save(v);
                }
            }
            KingdomStorage.delete(kingdom);
            Bukkit.broadcastMessage("§c[국가] " + kingdom.getName() + " 국가가 해산되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (kingdom == null) {
                player.sendMessage("§c국가 정보를 불러올 수 없습니다.");
                return true;
            }
            OfflinePlayer king = Bukkit.getOfflinePlayer(kingdom.getLeader());
            player.sendMessage("§6[국가 정보]");
            player.sendMessage("§f이름: §e" + kingdom.getName());
            player.sendMessage("§f국왕: §e" + (king.getName() != null ? king.getName() : king.getUniqueId()));
            player.sendMessage("§f수도: §e" + kingdom.getCapital());
            player.sendMessage("§f소속 마을: §e" + String.join(", ", kingdom.getVillages()));
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            player.sendMessage("§6[국가 목록]");
            for (Kingdom k : KingdomManager.getAll()) {
                player.sendMessage("§f- " + k.getName() + " (§e수도: " + k.getCapital() + "§f)");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("members")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            player.sendMessage("§6[국가 소속 마을]");
            for (String vName : kingdom.getVillages()) {
                String prefix = vName.equalsIgnoreCase(kingdom.getCapital()) ? "§e* " : "- ";
                player.sendMessage(prefix + vName);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("setcapital") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (!kingdom.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 수도를 변경할 수 있습니다.");
                return true;
            }

            String targetName = args[1];
            if (!kingdom.getVillages().contains(targetName)) {
                player.sendMessage("§c해당 마을은 국가에 속해 있지 않습니다.");
                return true;
            }

            kingdom.setCapital(targetName);
            KingdomStorage.save(kingdom);
            Bukkit.broadcastMessage("§e[국가] " + kingdom.getName() + "의 수도가 " + targetName + "(으)로 변경되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("setking") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (!kingdom.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 왕위을 위임할 수 있습니다.");
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            UUID tId = target.getUniqueId();
            boolean member = false;
            for (String vName : kingdom.getVillages()) {
                Village v = VillageManager.getByName(vName);
                if (v != null && v.getMembers().contains(tId)) {
                    member = true;
                    break;
                }
            }
            if (!member) {
                player.sendMessage("§c해당 플레이어는 국가 구성원이 아닙니다.");
                return true;
            }

            kingdom.setLeader(tId);
            KingdomStorage.save(kingdom);
            Bukkit.broadcastMessage("§e[국가] " + kingdom.getName() + "의 새로운 국왕은 " + (target.getName() != null ? target.getName() : tId) + "입니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("addvillage") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (!kingdom.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 마을을 초대할 수 있습니다.");
                return true;
            }

            String targetName = args[1];
            Village target = VillageManager.getByName(targetName);
            if (target == null) {
                player.sendMessage("§c해당 마을이 존재하지 않습니다.");
                return true;
            }
            if (target.getKingdom() != null) {
                player.sendMessage("§c이미 다른 국가에 속한 마을입니다.");
                return true;
            }

            target.addKingdomInvite(kingdom.getName());
            VillageStorage.save(target);
            player.sendMessage("§a초대장을 보냈습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("removevillage") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (!kingdom.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 마을을 제외할 수 있습니다.");
                return true;
            }

            String targetName = args[1];
            if (targetName.equalsIgnoreCase(kingdom.getCapital())) {
                player.sendMessage("§c수도 마을은 제외할 수 없습니다.");
                return true;
            }
            Village target = VillageManager.getByName(targetName);
            if (target == null || !kingdom.getVillages().contains(targetName)) {
                player.sendMessage("§c해당 마을은 국가에 속해 있지 않습니다.");
                return true;
            }

            KingdomManager.removeVillage(kingdom, target);
            KingdomStorage.save(kingdom);
            VillageStorage.save(target);
            Bukkit.broadcastMessage("§e[국가] " + targetName + " 마을이 " + kingdom.getName() + "에서 제외되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("accept") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c마을 국왕만 사용할 수 있습니다.");
                return true;
            }

            String kingdomName = args[1];
            if (!village.getKingdomInvites().contains(kingdomName.toLowerCase())) {
                player.sendMessage("§c해당 국가로부터 초대받지 않았습니다.");
                return true;
            }
            if (village.getKingdom() != null) {
                player.sendMessage("§c이미 다른 국가에 속해 있습니다.");
                return true;
            }

            Kingdom kingdom = KingdomManager.getByName(kingdomName);
            if (kingdom == null) {
                player.sendMessage("§c해당 국가가 존재하지 않습니다.");
                return true;
            }

            KingdomManager.addVillage(kingdom, village);
            village.removeKingdomInvite(kingdomName);
            KingdomStorage.save(kingdom);
            VillageStorage.save(village);
            Bukkit.broadcastMessage("§e[국가] " + village.getName() + " 마을이 " + kingdom.getName() + "에 가입했습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("deny") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c마을 국왕만 사용할 수 있습니다.");
                return true;
            }

            String kingdomName = args[1];
            if (!village.getKingdomInvites().contains(kingdomName.toLowerCase())) {
                player.sendMessage("§c해당 국가로부터 초대받지 않았습니다.");
                return true;
            }
            village.removeKingdomInvite(kingdomName);
            VillageStorage.save(village);
            player.sendMessage("§e초대가 거절되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("treasury")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }

            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (args.length < 2) {
                player.sendMessage("§e/kingdom treasury balance§7, §e/kingdom treasury deposit <금액>§7, §e/kingdom treasury withdraw <금액>");
                return true;
            }
            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (args[1].equalsIgnoreCase("balance")) {
                player.sendMessage("§6[국고] §f잔액: §e" + kingdom.getTreasury() + "G");
                return true;
            }
            if (args[1].equalsIgnoreCase("deposit") && args.length >= 3) {
                if (!kingdom.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage("§c국왕만 국고에 입금할 수 있습니다.");
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c금액은 숫자여야 합니다.");
                    return true;
                }
                if (amount <= 0) {
                    player.sendMessage("§c금액은 1 이상이어야 합니다.");
                    return true;
                }
                if (data.getGold() < amount) {
                    player.sendMessage("§c보유 골드가 부족합니다.");
                    return true;
                }
                data.removeGold(amount);
                kingdom.addGold(amount);
                PlayerDataManager.save(player.getUniqueId());
                KingdomStorage.save(kingdom);
                Bukkit.getLogger().info(player.getName() + " deposited " + amount + "G to kingdom " + kingdom.getName());
                player.sendMessage("§a국고에 " + amount + "G 를 입금했습니다.");
                return true;
            }
            if (args[1].equalsIgnoreCase("withdraw") && args.length >= 3) {
                if (!kingdom.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage("§c국왕만 국고에서 출금할 수 있습니다.");
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c금액은 숫자여야 합니다.");
                    return true;
                }
                if (amount <= 0) {
                    player.sendMessage("§c금액은 1 이상이어야 합니다.");
                    return true;
                }
                if (kingdom.getTreasury() < amount) {
                    player.sendMessage("§c국고가 부족합니다.");
                    return true;
                }
                kingdom.removeGold(amount);
                data.addGold(amount);
                PlayerDataManager.save(player.getUniqueId());
                KingdomStorage.save(kingdom);
                Bukkit.getLogger().info(player.getName() + " withdrew " + amount + "G from kingdom " + kingdom.getName());
                player.sendMessage("§a국고에서 " + amount + "G 를 출금했습니다.");
                return true;
            }
            player.sendMessage("§c잘못된 하위 명령어입니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("specialty")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (!kingdom.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 특산품을 관리할 수 있습니다.");
                return true;
            }
            me.continent.kingdom.service.KingdomSpecialtyService.openMenu(player, kingdom);
            return true;
        }

        if (args[0].equalsIgnoreCase("chat")) {
            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            boolean current = data.isKingdomChatEnabled();
            data.setKingdomChatEnabled(!current);
            PlayerDataManager.save(player.getUniqueId());
            player.sendMessage("§a국가 채팅이 " + (data.isKingdomChatEnabled() ? "§b활성화§a되었습니다." : "§c비활성화§a되었습니다."));
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            String targetName = args.length >= 2 ? args[1] : kingdom.getCapital();
            if (!kingdom.getVillages().contains(targetName)) {
                player.sendMessage("§c해당 마을은 같은 국가에 속해 있지 않습니다.");
                return true;
            }
            Village target = VillageManager.getByName(targetName);
            if (target == null || target.getSpawnLocation() == null) {
                player.sendMessage("§c해당 마을의 스폰 위치가 설정되어 있지 않습니다.");
                return true;
            }
            player.teleport(target.getSpawnLocation());
            player.sendMessage("§a" + targetName + " 마을로 이동했습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || village.getKingdom() == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            if (!village.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c마을 국왕만 사용할 수 있습니다.");
                return true;
            }
            Kingdom kingdom = KingdomManager.getByName(village.getKingdom());
            if (kingdom.getCapital().equalsIgnoreCase(village.getName())) {
                player.sendMessage("§c수도 마을은 탈퇴할 수 없습니다.");
                return true;
            }

            KingdomManager.removeVillage(kingdom, village);
            KingdomStorage.save(kingdom);
            VillageStorage.save(village);
            Bukkit.broadcastMessage("§e[국가] " + village.getName() + " 마을이 " + kingdom.getName() + "에서 탈퇴했습니다.");
            return true;
        }

        return true;
    }
}
