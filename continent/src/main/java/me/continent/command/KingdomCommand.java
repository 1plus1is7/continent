package me.continent.command;

import me.continent.kingdom.*;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.kingdom.KingdomManager;
import me.continent.chat.KingdomChatManager;
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
            player.sendMessage("§e/kingdom list §7- 서버 내 모든 국가 목록");
            player.sendMessage("§e/kingdom setspawn §7- 국가 스폰 위치 설정");
            player.sendMessage("§e/kingdom chat <메시지> §7- 국가 채팅 전송");
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

            // 모든 구성원 소속 제거
            for (UUID member : kingdom.getMembers()) {
                PlayerData data = PlayerDataManager.get(member);
                data.setKingdom(null);
            }

            // Kingdom 제거 및 파일 삭제
            KingdomManager.unregister(kingdom);
            KingdomStorage.delete(kingdom);

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

        if (args[0].equalsIgnoreCase("accept")) {
            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            Set<String> invites = data.getPendingInvites();

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
                    targetName = invites.iterator().next(); // 유일한 초대 자동 수락
                } else {
                    player.sendMessage("§c[오류] 받은 초대가 여러 개입니다. /kingdom accept <국가이름> 을 사용하세요.");
                    return true;
                }
            }

            Kingdom targetKingdom = KingdomManager.getByName(targetName);
            if (targetKingdom == null) {
                player.sendMessage("§c[오류] 해당 국가가 존재하지 않습니다.");
                return true;
            }

            if (data.getKingdom() != null) {
                player.sendMessage("§c[오류] 이미 국가에 속해 있습니다.");
                return true;
            }

            // 처리
            data.getPendingInvites().remove(targetName);
            data.setKingdom(targetKingdom);
            targetKingdom.addMember(player.getUniqueId());

            KingdomStorage.save(targetKingdom);
            PlayerDataManager.save(player.getUniqueId());

            player.sendMessage("§a[시스템] " + targetName + " 국가에 가입했습니다!");
            return true;
        }


        if (args[0].equalsIgnoreCase("deny")) {
            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            Set<String> invites = data.getPendingInvites();

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

            data.getPendingInvites().remove(targetName);
            PlayerDataManager.save(player.getUniqueId());

            player.sendMessage("§a[시스템] " + targetName + " 국가의 초대를 거절했습니다.");
            return true;
        }


        if (args[0].equalsIgnoreCase("invites")) {
            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            Set<String> invites = data.getPendingInvites();

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

            PlayerData targetData = PlayerDataManager.get(target.getUniqueId());
            targetData.getPendingInvites().add(kingdom.getName());

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

        if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("c")) {
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage("§c메시지를 입력하세요.");
                return true;
            }

            String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            KingdomChatManager.sendMessage(kingdom, player.getUniqueId(), message);
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

            boolean result = KingdomUtils.unclaimChunk(kingdom, chunk);
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
            kingdom.addChunk(chunk);
            KingdomStorage.save(kingdom);


            player.sendMessage("§a청크를 성공적으로 점령했습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("setspawn")) {

            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null || !kingdom.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 국가 스폰을 설정할 수 있습니다.");
                return true;
            }
            Location loc = player.getLocation();
            World world = loc.getWorld();
            int x = loc.getBlockX();
            int z = loc.getBlockZ();
            int y = world.getHighestBlockYAt(x, z); // 지면 기준 Y 보정

            Location spawnLoc = new Location(world, x + 0.5, y, z + 0.5); // 중앙 정렬

            kingdom.setSpawnLocation(spawnLoc);
            KingdomStorage.save(kingdom); // ← 저장 반영
            Chunk chunk = spawnLoc.getChunk();
            kingdom.setSpawnChunk(chunk);
            player.sendMessage("§a국가 스폰 위치가 지면 위로 자동 설정되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && args.length >= 2) {
            String name = args[1];

            if (KingdomManager.isPlayerInKingdom(player.getUniqueId())) {
                player.sendMessage("§c이미 국가에 소속되어 있습니다.");
                return true;
            }

            Chunk chunk = player.getLocation().getChunk();
            if (KingdomManager.isChunkClaimed(chunk)) {
                player.sendMessage("§c해당 지역은 이미 다른 국가가 점령 중입니다.");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (data.getGold() < 30) {
                player.sendMessage("§c국가를 생성하려면 30G가 필요합니다.");
                return true;
            }

            boolean success = KingdomManager.createKingdom(name, player.getUniqueId(), chunk);
            if (!success) {
                player.sendMessage("§c국가 생성에 실패했습니다. (중복 이름 등)");
                return true;
            }

// 생성된 kingdom 객체를 다시 조회
            Kingdom kingdom = KingdomManager.getByPlayer(player.getUniqueId());
            if (kingdom == null) {
                player.sendMessage("§c알 수 없는 오류로 인해 국가 정보가 조회되지 않았습니다.");
                return true;
            }

            data.setGold(data.getGold() - 30);
            PlayerDataManager.get(player.getUniqueId()).setKingdom(kingdom);
            // 국가 생성 성공 이후
            Location loc = kingdom.getCoreLocation();
            loc.getBlock().setType(Material.BEACON);
            kingdom.setCoreChunk(chunk);
            player.sendMessage("§a국가가 성공적으로 생성되었습니다: §e" + name);
            return true;
        }

        return true;
    }
}
