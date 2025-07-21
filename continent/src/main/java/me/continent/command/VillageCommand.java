package me.continent.command;

import me.continent.village.Village;
import me.continent.village.VillageManager;
import me.continent.village.service.*;
import me.continent.scoreboard.ScoreboardService;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.storage.VillageStorage;
import me.continent.utils.ConfirmationManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;


public class VillageCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6[Village 명령어 도움말]");
            player.sendMessage("§e/village create <이름> §7- 마을 또는 마을 생성");
            player.sendMessage("§e/village disband §7- 마을 해산");
            player.sendMessage("§e/village claim §7- 청크 점령");
            player.sendMessage("§e/village invite <플레이어> §7- 초대 전송");
            player.sendMessage("§e/village invites §7- 받은 초대 목록 확인");
            player.sendMessage("§e/village accept <이름> §7- 초대 수락");
            player.sendMessage("§e/village deny <이름> §7- 초대 거절");
            player.sendMessage("§e/village members §7- 마을 구성원 확인");
            player.sendMessage("§e/village leave §7- 마을 탈퇴");
            player.sendMessage("§e/village kick <플레이어> §7- 구성원 추방");
            player.sendMessage("§e/village rename <새이름> §7- 마을 이름 변경");
            player.sendMessage("§e/village list §7- 서버 내 모든 마을 목록");
            player.sendMessage("§e/village setspawn §7- 마을 스폰 위치 설정");
            player.sendMessage("§e/village setcore §7- 코어 위치 이동");
            player.sendMessage("§e/village spawn §7- 마을 스폰으로 이동");
            player.sendMessage("§e/village chest §7- 마을 창고 열기");
            player.sendMessage("§e/village setsymbol §7- 상징 아이템 설정");
            player.sendMessage("§e/village ignite <on|off> §7- 아군 점화 허용 토글");
            player.sendMessage("§e/village upkeep §7- 현재 유지비 확인");
            player.sendMessage("§e/village treasury <subcommand> §7- 금고 관리");
            player.sendMessage("§e/village confirm §7- 대기 중인 작업 확인");
            player.sendMessage("§e/village chat §7- 마을 채팅 토글");
            return true;
        }

        if (args[0].equalsIgnoreCase("confirm")) {
            if (!ConfirmationManager.confirm(player)) {
                player.sendMessage("§c진행 중인 확인 요청이 없습니다.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("disband")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을가 없습니다.");
                return true;
            }

            if (!village.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 마을를 해산할 수 있습니다.");
                return true;
            }
            if (!village.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 마을를 해산할 수 있습니다.");
                return true;
            }

            ConfirmationManager.request(player, () -> {
                MembershipService.disband(village);
                player.sendMessage("§c마을가 성공적으로 해산되었습니다.");
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("members")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을가 없습니다.");
                return true;
            }
            player.sendMessage("§6[마을 구성원 목록]");
            for (UUID uuid : village.getMembers()) {
                OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);

                // 이름이 없는 경우 UUID 일부로 대체
                String name = (member.getName() != null)
                        ? member.getName()
                        : "플레이어(" + uuid.toString().substring(0, 8) + ")";

                String role = uuid.equals(village.getKing()) ? "§e(국왕)" : "§7(국민)";
                player.sendMessage("§f- " + name + " " + role);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을가 없습니다.");
                return true;
            }
            if (village.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕은 마을를 탈퇴할 수 없습니다. 해산을 시도하세요.");
                return true;
            }
            ConfirmationManager.request(player, () -> {
                MembershipService.leaveVillage(player, village);
                player.sendMessage("§a마을를 탈퇴했습니다.");
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("kick") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국왕만 구성원을 추방할 수 있습니다.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage("§c해당 플레이어를 찾을 수 없습니다.");
                return true;
            }
            ConfirmationManager.request(player, () -> {
                if (!MembershipService.kickMember(village, target.getUniqueId())) {
                    player.sendMessage("§c해당 플레이어는 마을의 구성원이 아닙니다.");
                    return;
                }
                player.sendMessage("§e" + target.getName() + "§f을(를) 추방했습니다.");
                if (target.isOnline()) {
                    target.sendMessage("§c마을에서 추방당했습니다.");
                }
            });
            return true;
        }


        if (args[0].equalsIgnoreCase("rename") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 마을 이름을 변경할 수 있습니다.");
                return true;
            }
            String newName = args[1];
            if (!MembershipService.renameVillage(village, newName)) {
                player.sendMessage("§c이미 사용 중인 이름입니다.");
                return true;
            }
            player.sendMessage("§a마을 이름이 §e" + newName + "§a(으)로 변경되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("treasury")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을가 없습니다.");
                return true;
            }

            if (village.getnation() != null) {
                player.sendMessage("§c국가에 속한 마을의 금고는 국가 국고에 포함되어 개별 관리할 수 없습니다.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage("§e/village treasury balance§7, §e/village treasury deposit <금액>§7, §e/village treasury withdraw <금액>");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());

            if (args[1].equalsIgnoreCase("balance")) {
                player.sendMessage("§6[금고] §f잔액: §e" + village.getVault() + "G");
                return true;
            }

            if (args[1].equalsIgnoreCase("deposit") && args.length >= 3) {
                if (!village.isAuthorized(player.getUniqueId())) {
                    player.sendMessage("§c국왕만 금고에 입금할 수 있습니다.");
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
                village.addGold(amount);
                PlayerDataManager.save(player.getUniqueId());
                VillageStorage.save(village);
                player.sendMessage("§a금고에 " + amount + "G 를 입금했습니다.");
                return true;
            }

            if (args[1].equalsIgnoreCase("withdraw") && args.length >= 3) {
                if (!village.isAuthorized(player.getUniqueId())) {
                    player.sendMessage("§c국왕만 금고에서 출금할 수 있습니다.");
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
                if (village.getVault() < amount) {
                    player.sendMessage("§c금고가 부족합니다.");
                    return true;
                }
                village.removeGold(amount);
                data.addGold(amount);
                PlayerDataManager.save(player.getUniqueId());
                VillageStorage.save(village);
                player.sendMessage("§a금고에서 " + amount + "G 를 출금했습니다.");
                return true;
            }

            player.sendMessage("§c잘못된 하위 명령어입니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                player.sendMessage("§c/village accept <village>");
                return true;
            }

            String targetVillageName = args[1];
            Village targetVillage = VillageManager.getVillageByName(targetVillageName);

            if (targetVillage == null) {
                player.sendMessage("§c해당 이름의 마을가 존재하지 않습니다.");
                return true;
            }

            UUID playerUUID = player.getUniqueId();

            if (!InviteService.getInvites(playerUUID).contains(targetVillageName)) {
                player.sendMessage("§c해당 마을로부터 초대를 받지 않았습니다.");
                return true;
            }

            if (VillageManager.getByPlayer(playerUUID) != null) {
                player.sendMessage("§c이미 다른 마을에 소속되어 있습니다.");
                return true;
            }

            if (targetVillage.isVillage() && targetVillage.getMembers().size() >= 15) {
                player.sendMessage("§c마을은 최대 15명까지만 가입할 수 있습니다.");
                return true;
            }

            MembershipService.joinVillage(player, targetVillage);
            InviteService.removeInvite(playerUUID, targetVillageName);

            player.sendMessage("가입 완료!");

            for (UUID member : targetVillage.getMembers()) {
                if (!member.equals(playerUUID)) {
                    Player online = Bukkit.getPlayer(member);
                    if (online != null && online.isOnline()) {
                        online.sendMessage("§a[마을 시스템] " + player.getName() + "님이 마을에 가입했습니다!");
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

            boolean current = data.isVillageChatEnabled();
            data.setVillageChatEnabled(!current);
            player.sendMessage("§a마을 채팅이 " + (data.isVillageChatEnabled() ? "§b활성화§a되었습니다." : "§c비활성화§a되었습니다."));

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
                    player.sendMessage("§c[오류] 해당 마을의 초대가 존재하지 않습니다.");
                    return true;
                }
            } else {
                if (invites.size() == 1) {
                    targetName = invites.iterator().next(); // 유일한 초대 자동 거절
                } else {
                    player.sendMessage("§c[오류] 받은 초대가 여러 개입니다. /village deny <마을이름> 을 사용하세요.");
                    return true;
                }
            }

            InviteService.removeInvite(pid, targetName);

            player.sendMessage("§a[시스템] " + targetName + " 마을의 초대를 거절했습니다.");
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
                player.sendMessage("§f- §e" + kname + " §7(/village accept " + kname + ")");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("invite") && args.length >= 2) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c초대는 국왕만 가능합니다.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage("§c해당 플레이어를 찾을 수 없습니다.");
                return true;
            }

            if (VillageManager.isPlayerInVillage(target.getUniqueId())) {
                player.sendMessage("§c해당 플레이어는 이미 다른 마을에 소속되어 있습니다.");
                return true;
            }

            InviteService.sendInvite(target.getUniqueId(), village.getName());

            player.sendMessage("§a초대장을 보냈습니다.");
            target.sendMessage("§6[마을 초대] §f" + player.getName() + " 님이 당신을 §e" + village.getName() + "§f에 초대했습니다.");
            target.sendMessage("§7/village accept " + village.getName() + " §f또는 §7/village deny " + village.getName());
            return true;
        }


        if (args[0].equalsIgnoreCase("list")) {
            Set<Village> all = Set.copyOf(VillageManager.getAll());
            if (all.isEmpty()) {
                player.sendMessage("§7등록된 마을가 없습니다.");
                return true;
            }
            player.sendMessage("§6[서버 마을 목록]");
            for (Village k : all) {
                player.sendMessage("§f- " + k.getName());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("unclaim")) {
            Chunk chunk = player.getLocation().getChunk();
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 청크를 해제할 수 있습니다.");
                return true;
            }

            String chunkKey = Village.getChunkKey(player.getLocation().getChunk());
            if (!village.getClaimedChunks().contains(chunkKey)) {
                player.sendMessage("§c이 청크는 당신의 영토가 아닙니다.");
                return true;
            }

            if (village.getClaimedChunks().size() <= 1) {
                player.sendMessage("§c최소 1개의 영토는 유지해야 합니다.");
                return true;
            }

            boolean result = ClaimService.unclaim(village, chunk);
            if (chunkKey.equals(village.getCoreChunk()) || chunkKey.equals(village.getSpawnChunk())) {
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
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을 또는 마을이 없습니다.");
                return true;
            }

            if (!village.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 청크를 점령할 수 있습니다.");
                return true;
            }

            Chunk chunk = player.getLocation().getChunk();

            Village occupying = VillageManager.getByChunk(chunk);
            if (occupying != null) {
                if (occupying.getKing().equals(player.getUniqueId())) {
                    player.sendMessage("§e해당 청크는 이미 귀하의 마을가 점령 중입니다.");
                } else {
                    player.sendMessage("§c해당 청크는 이미 다른 마을가 점령 중입니다.");
                }
                return true;
            }

            if (VillageManager.isNearOtherVillage(chunk, village, 2)) {
                player.sendMessage("§c다른 마을와 너무 가까워 점령할 수 없습니다.");
                return true;
            }

            if (!village.isAdjacent(chunk) && village.getClaimedChunks().size() > 0) {
                player.sendMessage("§c해당 청크는 기존 영토와 인접하지 않습니다.");
                return true;
            }

            if (village.isVillage() && village.getClaimedChunks().size() >= 16) {
                player.sendMessage("§c마을은 최대 16청크까지만 점령할 수 있습니다.");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (data.getGold() < 5) {
                player.sendMessage("§c골드가 부족합니다. (5G 필요)");
                return true;
            }

            data.setGold(data.getGold() - 5);
            ClaimService.claim(village, chunk);


            player.sendMessage("§a청크를 성공적으로 점령했습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("setspawn")) {

            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 마을 스폰을 설정할 수 있습니다.");
                return true;
            }
            SpawnService.setSpawn(village, player.getLocation());
            player.sendMessage("§a마을 스폰 위치가 지면 위로 자동 설정되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을가 없습니다.");
                return true;
            }
            Location spawnLoc = village.getSpawnLocation();
            if (spawnLoc == null) {
                player.sendMessage("§c마을 스폰이 설정되어 있지 않습니다.");
                return true;
            }
            player.teleport(spawnLoc);
            player.sendMessage("§a마을 스폰으로 이동했습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("chest")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을가 없습니다.");
                return true;
            }
            ChestService.openChest(player, village);
            return true;
        }

        if (args[0].equalsIgnoreCase("setsymbol")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국왕만 상징 아이템을 변경할 수 있습니다.");
                return true;
            }
            org.bukkit.inventory.ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == org.bukkit.Material.AIR) {
                player.sendMessage("§c손에 아이템을 들고 있어야 합니다.");
                return true;
            }
            village.setSymbol(item.clone());
            VillageStorage.save(village);
            player.sendMessage("§a상징 아이템이 업데이트되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("upkeep")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null) {
                player.sendMessage("§c소속된 마을가 없습니다.");
                return true;
            }
            double amount = MaintenanceService.getWeeklyCost(village);
            player.sendMessage("§e이번 주 유지비: " + amount + "G");
            return true;
        }

        if (args[0].equalsIgnoreCase("ignite")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국왕만 설정을 변경할 수 있습니다.");
                return true;
            }
            boolean allow;
            if (args.length >= 2) {
                allow = args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true");
            } else {
                allow = !village.isMemberIgniteAllowed();
            }
            village.setMemberIgniteAllowed(allow);
            VillageStorage.save(village);
            player.sendMessage("§e아군 점화 허용이 " + (allow ? "켜졌습니다" : "꺼졌습니다"));
            return true;
        }

        if (args[0].equalsIgnoreCase("setcore")) {
            Village village = VillageManager.getByPlayer(player.getUniqueId());
            if (village == null || !village.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 코어 위치를 변경할 수 있습니다.");
                return true;
            }

            String key = Village.getChunkKey(player.getLocation().getChunk());
            if (!village.getClaimedChunks().contains(key)) {
                player.sendMessage("§c해당 위치는 당신의 영토가 아닙니다.");
                return true;
            }

            CoreService.removeCore(village);
            CoreService.placeCore(village, player.getLocation());
            player.sendMessage("§a코어 위치가 변경되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && args.length >= 2) {
            String name = args[1];

            if (VillageManager.isPlayerInVillage(player.getUniqueId())) {
                player.sendMessage("§c이미 마을에 소속되어 있습니다.");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (data.getGold() < 30) {
                player.sendMessage("§c마을를 생성하려면 30G가 필요합니다.");
                return true;
            }

            Chunk chunk = player.getLocation().getChunk();
            if (VillageManager.isNearOtherVillage(chunk, 2)) {
                player.sendMessage("§c다른 마을와 너무 가까워 마을를 생성할 수 없습니다.");
                return true;
            }

            ConfirmationManager.request(player, () -> {
                Village village = MembershipService.createVillage(name, player);
                if (village == null) {
                    player.sendMessage("§c마을 생성에 실패했습니다. (중복 이름 등)");
                    return;
                }
                data.setGold(data.getGold() - 30);
                player.sendMessage("§a마을가 생성되었습니다: §e" + name);
            });
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subs = Arrays.asList(
                "create", "disband", "claim", "invite", "invites", "accept", "deny",
                "members", "leave", "kick", "rename", "list", "setspawn", "setcore",
                "spawn", "chest", "setsymbol", "ignite", "upkeep", "treasury", "confirm", "chat"
        );

        if (args.length == 1) {
            return subs.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("kick")
                || args[0].equalsIgnoreCase("pay"))) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("ignite")) {
            return Arrays.asList("on", "off").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
