package me.continent.command;

import me.continent.kingdom.Kingdom;
import me.continent.kingdom.KingdomManager;
import me.continent.kingdom.service.*;
import me.continent.scoreboard.ScoreboardService;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.storage.KingdomStorage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;


public class KingdomCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6[Kingdom 명령어 도움말]");
            player.sendMessage("§e/kingdom create <이름> §7- 국가 또는 마을 생성");
            player.sendMessage("§e/kingdom disband §7- 국가 해산");
            player.sendMessage("§e/kingdom claim §7- 청크 점령");
            player.sendMessage("§e/kingdom invite <플레이어> §7- 초대 전송");
            player.sendMessage("§e/kingdom invites §7- 받은 초대 목록 확인");
            player.sendMessage("§e/kingdom accept <이름> §7- 초대 수락");
            player.sendMessage("§e/kingdom deny <이름> §7- 초대 거절");
            player.sendMessage("§e/kingdom members §7- 국가 구성원 확인");
            player.sendMessage("§e/kingdom leave §7- 국가 탈퇴");
            player.sendMessage("§e/kingdom kick <플레이어> §7- 구성원 추방");
            player.sendMessage("§e/kingdom rename <새이름> §7- 국가 이름 변경");
            player.sendMessage("§e/kingdom color <HEX> §7- 국가 색상 변경");
            player.sendMessage("§e/kingdom list §7- 서버 내 모든 국가 목록");
            player.sendMessage("§e/kingdom setspawn §7- 국가 스폰 위치 설정");
            player.sendMessage("§e/kingdom chat §7- 국가 채팅 토글");
            return true;
        }

        if (args[0].equalsIgnoreCase("disband")) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            if (!kingdom.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 국가를 해산할 수 있습니다.");
                return true;
            }

            MembershipService.disband(kingdom);
            player.sendMessage("§c국가가 성공적으로 해산되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("members")) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            player.sendMessage("§6[국가 구성원 목록]");
            for (UUID uuid : kingdom.getMembers()) {
                OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);

                // 이름이 없는 경우 UUID 일부로 대체
                String name = (member.getName() != null)
                        ? member.getName()
                        : "플레이어(" + uuid.toString().substring(0, 8) + ")";

                String role = uuid.equals(kingdom.getKing()) ? "§e(국왕)" : "§7(국민)";
                player.sendMessage("§f- " + name + " " + role);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            if (kingdom.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕은 국가를 탈퇴할 수 없습니다. 해산을 시도하세요.");
                return true;
            }
            MembershipService.leaveKingdom(player, kingdom);
            player.sendMessage("§a국가를 탈퇴했습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("kick") && args.length >= 2) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null || !kingdom.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국왕만 구성원을 추방할 수 있습니다.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage("§c해당 플레이어를 찾을 수 없습니다.");
                return true;
            }
            if (!MembershipService.kickMember(kingdom, target.getUniqueId())) {
                player.sendMessage("§c해당 플레이어는 국가의 구성원이 아닙니다.");
                return true;
            }
            player.sendMessage("§e" + target.getName() + "§f을(를) 추방했습니다.");
            if (target.isOnline()) {
                target.sendMessage("§c국가에서 추방당했습니다.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("color") && args.length >= 2) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null || !kingdom.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국왕만 색상을 변경할 수 있습니다.");
                return true;
            }
            String hex = args[1];
            if (!hex.matches("#?[0-9a-fA-F]{6}")) {
                player.sendMessage("§c올바른 HEX 형식이 아닙니다.");
                return true;
            }
            kingdom.setColor(hex.startsWith("#") ? hex : "#" + hex);
            KingdomStorage.save(kingdom);
            player.sendMessage("§a국가 색상이 변경되었습니다: " + hex);
            return true;
        }

        if (args[0].equalsIgnoreCase("rename") && args.length >= 2) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null || !kingdom.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 국가 이름을 변경할 수 있습니다.");
                return true;
            }
            String newName = args[1];
            if (!MembershipService.renameKingdom(kingdom, newName)) {
                player.sendMessage("§c이미 사용 중인 이름입니다.");
                return true;
            }
            player.sendMessage("§a국가 이름이 §e" + newName + "§a(으)로 변경되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                player.sendMessage("§c/kingdom accept <kingdom>");
                return true;
            }

            String targetKingdomName = args[1];
            Kingdom targetKingdom = KingdomManager.getKingdomByName(targetKingdomName);

            if (targetKingdom == null) {
                player.sendMessage("§c해당 이름의 국가가 존재하지 않습니다.");
                return true;
            }

            UUID playerUUID = player.getUniqueId();

            if (!InviteService.getInvites(playerUUID).contains(targetKingdomName)) {
                player.sendMessage("§c해당 국가로부터 초대를 받지 않았습니다.");
                return true;
            }

            if (KingdomManager.getByPlayer(playerUUID) != null) {
                player.sendMessage("§c이미 다른 국가에 소속되어 있습니다.");
                return true;
            }

            MembershipService.joinKingdom(player, targetKingdom);
            InviteService.removeInvite(playerUUID, targetKingdomName);

            player.sendMessage("가입 완료!");

            for (UUID member : targetKingdom.getMembers()) {
                if (!member.equals(playerUUID)) {
                    Player online = Bukkit.getPlayer(member);
                    if (online != null && online.isOnline()) {
                        online.sendMessage("§a[국가 시스템] " + player.getName() + "님이 국가에 가입했습니다!");
                    }
                }
            }

            // 스코어보드 또는 캐시 강제 갱신
            ScoreboardService.update(player); // ← 실제 사용 중인 서비스 명칭에 따라 수정

            return true;
        }


        if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("c")) {
            if (!(sender instanceof Player)) return false;

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (data == null) return false;

            boolean current = data.isKingdomChatEnabled();
            data.setKingdomChatEnabled(!current);
            player.sendMessage("§a국가 채팅이 " + (data.isKingdomChatEnabled() ? "§b활성화§a되었습니다." : "§c비활성화§a되었습니다."));

            return true;
            }



        if (args[0].equalsIgnoreCase("deny")) {
            UUID pid = player.getUniqueId();
            Set<String> invites = InviteService.getInvites(pid);

            if (invites.isEmpty()) {
                player.sendMessage("§c[오류] 받은 초대가 없습니다.");
                return true;
            }

            String targetName;
            if (args.length >= 2) {
                targetName = args[1];
                if (!invites.contains(targetName)) {
                    player.sendMessage("§c[오류] 해당 국가의 초대가 존재하지 않습니다.");
                    return true;
                }
            } else {
                if (invites.size() == 1) {
                    targetName = invites.iterator().next(); // 유일한 초대 자동 거절
                } else {
                    player.sendMessage("§c[오류] 받은 초대가 여러 개입니다. /kingdom deny <국가이름> 을 사용하세요.");
                    return true;
                }
            }

            InviteService.removeInvite(pid, targetName);

            player.sendMessage("§a[시스템] " + targetName + " 국가의 초대를 거절했습니다.");
            return true;
        }


        if (args[0].equalsIgnoreCase("invites")) {
            Set<String> invites = InviteService.getInvites(player.getUniqueId());

            if (invites.isEmpty()) {
                player.sendMessage("§7받은 초대가 없습니다.");
                return true;
            }

            player.sendMessage("§6[받은 초대 목록]");
            for (String kname : invites) {
                player.sendMessage("§f- §e" + kname + " §7(/kingdom accept " + kname + ")");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("invite") && args.length >= 2) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null || !kingdom.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c초대는 국왕만 가능합니다.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage("§c해당 플레이어를 찾을 수 없습니다.");
                return true;
            }

            if (KingdomManager.isPlayerInKingdom(target.getUniqueId())) {
                player.sendMessage("§c해당 플레이어는 이미 다른 국가에 소속되어 있습니다.");
                return true;
            }

            InviteService.sendInvite(target.getUniqueId(), kingdom.getName());

            player.sendMessage("§a초대장을 보냈습니다.");
            target.sendMessage("§6[국가 초대] §f" + player.getName() + " 님이 당신을 §e" + kingdom.getName() + "§f에 초대했습니다.");
            target.sendMessage("§7/kingdom accept " + kingdom.getName() + " §f또는 §7/kingdom deny " + kingdom.getName());
            return true;
        }


        if (args[0].equalsIgnoreCase("list")) {
            Set<Kingdom> all = Set.copyOf(KingdomManager.getAll());
            if (all.isEmpty()) {
                player.sendMessage("§7등록된 국가가 없습니다.");
                return true;
            }
            player.sendMessage("§6[서버 국가 목록]");
            for (Kingdom k : all) {
                player.sendMessage("§f- " + k.getName());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("unclaim")) {
            Chunk chunk = player.getLocation().getChunk();
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null || !kingdom.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 청크를 해제할 수 있습니다.");
                return true;
            }

            String chunkKey = Kingdom.getChunkKey(player.getLocation().getChunk());
            if (!kingdom.getClaimedChunks().contains(chunkKey)) {
                player.sendMessage("§c이 청크는 당신의 영토가 아닙니다.");
                return true;
            }

            if (kingdom.getClaimedChunks().size() <= 1) {
                player.sendMessage("§c최소 1개의 영토는 유지해야 합니다.");
                return true;
            }

            boolean result = ClaimService.unclaim(kingdom, chunk);
            if (chunkKey.equals(kingdom.getCoreChunk()) || chunkKey.equals(kingdom.getSpawnChunk())) {
                player.sendMessage(ChatColor.RED + "스폰이나 코어 지역은 해제할 수 없습니다.");
            }
            if (!result) {
                player.sendMessage("§c이 청크는 해제할 수 없습니다. (코어/스폰 보호 또는 영토 단절 가능성)");
            } else {
                player.sendMessage("§a영토 해제 완료: 현재 위치의 청크가 해제되었습니다.");
            }

            return true;
        }


        if (args[0].equalsIgnoreCase("claim")) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null) {
                player.sendMessage("§c소속된 국가 또는 마을이 없습니다.");
                return true;
            }

            if (!kingdom.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 청크를 점령할 수 있습니다.");
                return true;
            }

            Chunk chunk = player.getLocation().getChunk();

            Kingdom occupying = KingdomManager.getByChunk(chunk);
            if (occupying != null) {
                if (occupying.getKing().equals(player.getUniqueId())) {
                    player.sendMessage("§e해당 청크는 이미 귀하의 국가가 점령 중입니다.");
                } else {
                    player.sendMessage("§c해당 청크는 이미 다른 국가가 점령 중입니다.");
                }
                return true;
            }

            if (KingdomManager.isNearOtherKingdom(chunk, kingdom, 2)) {
                player.sendMessage("§c다른 국가와 너무 가까워 점령할 수 없습니다.");
                return true;
            }

            if (!kingdom.isAdjacent(chunk) && kingdom.getClaimedChunks().size() > 0) {
                player.sendMessage("§c해당 청크는 기존 영토와 인접하지 않습니다.");
                return true;
            }

            if (kingdom.isVillage() && kingdom.getClaimedChunks().size() >= 16) {
                player.sendMessage("§c마을은 최대 16청크까지만 점령할 수 있습니다.");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (data.getGold() < 5) {
                player.sendMessage("§c골드가 부족합니다. (5G 필요)");
                return true;
            }

            data.setGold(data.getGold() - 5);
            ClaimService.claim(kingdom, chunk);


            player.sendMessage("§a청크를 성공적으로 점령했습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("setspawn")) {

            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null || !kingdom.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 국가 스폰을 설정할 수 있습니다.");
                return true;
            }
            SpawnService.setSpawn(kingdom, player.getLocation());
            player.sendMessage("§a국가 스폰 위치가 지면 위로 자동 설정되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && args.length >= 2) {
            String name = args[1];

            if (KingdomManager.isPlayerInKingdom(player.getUniqueId())) {
                player.sendMessage("§c이미 국가에 소속되어 있습니다.");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (data.getGold() < 30) {
                player.sendMessage("§c국가를 생성하려면 30G가 필요합니다.");
                return true;
            }

            Chunk chunk = player.getLocation().getChunk();
            if (KingdomManager.isNearOtherKingdom(chunk, 2)) {
                player.sendMessage("§c다른 국가와 너무 가까워 국가를 생성할 수 없습니다.");
                return true;
            }

            Kingdom kingdom = MembershipService.createKingdom(name, player);
            if (kingdom == null) {
                player.sendMessage("§c국가 생성에 실패했습니다. (중복 이름 등)");
                return true;
            }

            data.setGold(data.getGold() - 30);
            player.sendMessage("§a국가가 생성되었습니다: §e" + name);
            return true;
        }
        return true;
    }
}
