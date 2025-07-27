package me.continent.command;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.nation.service.*;
import me.continent.scoreboard.ScoreboardService;
import me.continent.player.PlayerData;
import me.continent.player.PlayerDataManager;
import me.continent.storage.NationStorage;
import me.continent.utils.ConfirmationManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;


public class NationCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6[Nation 명령어 도움말]");
            player.sendMessage("§e/nation create <이름> §7- 국가 또는 국가 생성");
            player.sendMessage("§e/nation disband §7- 국가 해산");
            player.sendMessage("§e/nation claim §7- 청크 점령");
            player.sendMessage("§e/nation invite <플레이어> §7- 초대 전송");
            player.sendMessage("§e/nation invites §7- 받은 초대 목록 확인");
            player.sendMessage("§e/nation accept <이름> §7- 초대 수락");
            player.sendMessage("§e/nation deny <이름> §7- 초대 거절");
            player.sendMessage("§e/nation members §7- 국가 구성원 확인");
            player.sendMessage("§e/nation leave §7- 국가 탈퇴");
            player.sendMessage("§e/nation kick <플레이어> §7- 구성원 추방");
            player.sendMessage("§e/nation rename <새이름> §7- 국가 이름 변경");
            player.sendMessage("§e/nation list §7- 서버 내 모든 국가 목록");
            player.sendMessage("§e/nation setspawn §7- 국가 스폰 위치 설정");
            player.sendMessage("§e/nation setcore §7- 코어 위치 이동");
            player.sendMessage("§e/nation spawn §7- 국가 스폰으로 이동");
            player.sendMessage("§e/nation chest §7- 국가 창고 열기");
            player.sendMessage("§e/nation menu §7- 국가 메뉴 열기");
            player.sendMessage("§e/nation setsymbol §7- 상징 아이템 설정");
            player.sendMessage("§e/nation ignite <on|off> §7- 아군 점화 허용 토글");
            player.sendMessage("§e/nation upkeep §7- 현재 유지비 확인");
            player.sendMessage("§e/nation treasury <subcommand> §7- 금고 관리");
            player.sendMessage("§e/nation specialty §7- 특산품 관리");
            player.sendMessage("§e/nation upgrade §7- 국가 등급 승격 시도");
            player.sendMessage("§e/nation tier §7- 현재 국가 등급 확인");
            player.sendMessage("§e/nation tierinfo §7- 등급별 기능 안내");
            player.sendMessage("§e/nation confirm §7- 대기 중인 작업 확인");
            player.sendMessage("§e/nation chat §7- 국가 채팅 토글");
            return true;
        }

        if (args[0].equalsIgnoreCase("confirm")) {
            if (!ConfirmationManager.confirm(player)) {
                player.sendMessage("§c진행 중인 확인 요청이 없습니다.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("disband")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }

            if (!nation.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 국가를 해산할 수 있습니다.");
                return true;
            }
            if (!nation.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 국가를 해산할 수 있습니다.");
                return true;
            }

            ConfirmationManager.request(player, () -> {
                MembershipService.disband(nation);
                player.sendMessage("§c국가가 성공적으로 해산되었습니다.");
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("members")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            player.sendMessage("§6[국가 구성원 목록]");
            for (UUID uuid : nation.getMembers()) {
                OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);

                // 이름이 없는 경우 UUID 일부로 대체
                String name = (member.getName() != null)
                        ? member.getName()
                        : "플레이어(" + uuid.toString().substring(0, 8) + ")";

                String role = uuid.equals(nation.getKing()) ? "§e(국왕)" : "§7(국민)";
                player.sendMessage("§f- " + name + " " + role);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            if (nation.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕은 국가를 탈퇴할 수 없습니다. 해산을 시도하세요.");
                return true;
            }
            ConfirmationManager.request(player, () -> {
                MembershipService.leaveNation(player, nation);
                player.sendMessage("§a국가를 탈퇴했습니다.");
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("kick")) {
            if (args.length < 2) {
                player.sendMessage("§c사용법: /nation kick <플레이어>");
                return true;
            }
            
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null || !nation.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국왕만 구성원을 추방할 수 있습니다.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                player.sendMessage("§c해당 플레이어를 찾을 수 없습니다.");
                return true;
            }
            ConfirmationManager.request(player, () -> {
                if (!MembershipService.kickMember(nation, target.getUniqueId())) {
                    player.sendMessage("§c해당 플레이어는 국가의 구성원이 아닙니다.");
                    return;
                }
                player.sendMessage("§e" + target.getName() + "§f을(를) 추방했습니다.");
                if (target.isOnline()) {
                    target.sendMessage("§c국가에서 추방당했습니다.");
                }
            });
            return true;
        }


        if (args[0].equalsIgnoreCase("rename")) {
            if (args.length < 2) {
                player.sendMessage("§c사용법: /nation rename <새이름>");
                return true;
            }

            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null || !nation.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 국가 이름을 변경할 수 있습니다.");
                return true;
            }
            String newName = args[1];
            if (!MembershipService.renameNation(nation, newName)) {
                player.sendMessage("§c이미 사용 중인 이름입니다.");
                return true;
            }
            player.sendMessage("§a국가 이름이 §e" + newName + "§a(으)로 변경되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("treasury")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }


            if (args.length < 2) {
                player.sendMessage("§e/nation treasury balance§7, §e/nation treasury deposit <금액>§7, §e/nation treasury withdraw <금액>");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());

            if (args[1].equalsIgnoreCase("balance")) {
                player.sendMessage("§6[금고] §f잔액: §e" + nation.getVault() + "C");
                return true;
            }

            if (args[1].equalsIgnoreCase("deposit") && args.length >= 3) {
                if (!nation.isAuthorized(player.getUniqueId())) {
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
                    player.sendMessage("§c보유 크라운이 부족합니다.");
                    return true;
                }
                data.removeGold(amount);
                nation.addGold(amount);
                PlayerDataManager.save(player.getUniqueId());
                NationStorage.save(nation);
                player.sendMessage("§a금고에 " + amount + "C 를 입금했습니다.");
                return true;
            }

            if (args[1].equalsIgnoreCase("withdraw") && args.length >= 3) {
                if (!nation.isAuthorized(player.getUniqueId())) {
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
                if (nation.getVault() < amount) {
                    player.sendMessage("§c금고가 부족합니다.");
                    return true;
                }
                nation.removeGold(amount);
                data.addGold(amount);
                PlayerDataManager.save(player.getUniqueId());
                NationStorage.save(nation);
                player.sendMessage("§a금고에서 " + amount + "C 를 출금했습니다.");
                return true;
            }

            player.sendMessage("§c잘못된 하위 명령어입니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("specialty")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가이 없습니다.");
                return true;
            }
            if (!nation.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국가 촌장만 특산품을 관리할 수 있습니다.");
                return true;
            }
            me.continent.nation.service.NationSpecialtyService.openMenu(player, nation);
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                player.sendMessage("§c/nation accept <nation>");
                return true;
            }

            String targetNationName = args[1];
            Nation targetNation = NationManager.getNationByName(targetNationName);

            if (targetNation == null) {
                player.sendMessage("§c해당 이름의 국가가 존재하지 않습니다.");
                return true;
            }

            UUID playerUUID = player.getUniqueId();

            if (!InviteService.getInvites(playerUUID).contains(targetNationName)) {
                player.sendMessage("§c해당 국가로부터 초대를 받지 않았습니다.");
                return true;
            }

            if (NationManager.getByPlayer(playerUUID) != null) {
                player.sendMessage("§c이미 다른 국가에 소속되어 있습니다.");
                return true;
            }

            if (targetNation.isNation() && targetNation.getMembers().size() >= 15) {
                player.sendMessage("§c국가은 최대 15명까지만 가입할 수 있습니다.");
                return true;
            }

            MembershipService.joinNation(player, targetNation);
            InviteService.removeInvite(playerUUID, targetNationName);

            player.sendMessage("가입 완료!");

            for (UUID member : targetNation.getMembers()) {
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

            boolean current = data.isNationChatEnabled();
            data.setNationChatEnabled(!current);
            player.sendMessage("§a국가 채팅이 " + (data.isNationChatEnabled() ? "§b활성화§a되었습니다." : "§c비활성화§a되었습니다."));

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
                    player.sendMessage("§c[오류] 받은 초대가 여러 개입니다. /nation deny <국가이름> 을 사용하세요.");
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
                player.sendMessage("§f- §e" + kname + " §7(/nation accept " + kname + ")");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("invite")) {
            if (args.length < 2) {
                player.sendMessage("§c사용법: /nation invite <플레이어>");
                return true;
            }
            
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null || !nation.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c초대는 국왕만 가능합니다.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage("§c해당 플레이어를 찾을 수 없습니다.");
                return true;
            }

            if (NationManager.isPlayerInNation(target.getUniqueId())) {
                player.sendMessage("§c해당 플레이어는 이미 다른 국가에 소속되어 있습니다.");
                return true;
            }

            InviteService.sendInvite(target.getUniqueId(), nation.getName());

            player.sendMessage("§a초대장을 보냈습니다.");
            target.sendMessage("§6[국가 초대] §f" + player.getName() + " 님이 당신을 §e" + nation.getName() + "§f에 초대했습니다.");
            target.sendMessage("§7/nation accept " + nation.getName() + " §f또는 §7/nation deny " + nation.getName());
            return true;
        }


        if (args[0].equalsIgnoreCase("list")) {
            if (NationManager.getAll().isEmpty()) {
                player.sendMessage("§7등록된 국가가 없습니다.");
            } else {
                me.continent.nation.gui.NationListGUI.open(player);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("unclaim")) {
            Chunk chunk = player.getLocation().getChunk();
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null || !nation.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 청크를 해제할 수 있습니다.");
                return true;
            }

            String chunkKey = Nation.getChunkKey(player.getLocation().getChunk());
            if (!nation.getClaimedChunks().contains(chunkKey)) {
                player.sendMessage("§c이 청크는 당신의 영토가 아닙니다.");
                return true;
            }

            if (nation.getClaimedChunks().size() <= 1) {
                player.sendMessage("§c최소 1개의 영토는 유지해야 합니다.");
                return true;
            }

            boolean result = ClaimService.unclaim(nation, chunk);
            if (chunkKey.equals(nation.getCoreChunk()) || chunkKey.equals(nation.getSpawnChunk())) {
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
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가 또는 국가이 없습니다.");
                return true;
            }

            if (!nation.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 청크를 점령할 수 있습니다.");
                return true;
            }

            Chunk chunk = player.getLocation().getChunk();

            Nation occupying = NationManager.getByChunk(chunk);
            if (occupying != null) {
                if (occupying.getKing().equals(player.getUniqueId())) {
                    player.sendMessage("§e해당 청크는 이미 귀하의 국가가 점령 중입니다.");
                } else {
                    player.sendMessage("§c해당 청크는 이미 다른 국가가 점령 중입니다.");
                }
                return true;
            }

            if (NationManager.isNearOtherNation(chunk, nation, 2)) {
                player.sendMessage("§c다른 국가와 너무 가까워 점령할 수 없습니다.");
                return true;
            }

            if (!nation.isAdjacent(chunk) && nation.getClaimedChunks().size() > 0) {
                player.sendMessage("§c해당 청크는 기존 영토와 인접하지 않습니다.");
                return true;
            }

            if (nation.isNation() && nation.getClaimedChunks().size() >= 16) {
                player.sendMessage("§c국가은 최대 16청크까지만 점령할 수 있습니다.");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (data.getGold() < 5) {
                player.sendMessage("§c크라운이 부족합니다. (5C 필요)");
                return true;
            }

            data.setGold(data.getGold() - 5);
            ClaimService.claim(nation, chunk);


            player.sendMessage("§a청크를 성공적으로 점령했습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("setspawn")) {

            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null || !nation.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 국가 스폰을 설정할 수 있습니다.");
                return true;
            }
            SpawnService.setSpawn(nation, player.getLocation());
            player.sendMessage("§a국가 스폰 위치가 지면 위로 자동 설정되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            Location spawnLoc = nation.getSpawnLocation();
            if (spawnLoc == null) {
                player.sendMessage("§c국가 스폰이 설정되어 있지 않습니다.");
                return true;
            }
            player.teleport(spawnLoc);
            player.sendMessage("§a국가 스폰으로 이동했습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("chest")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            ChestService.openChest(player, nation);
            return true;
        }

        if (args[0].equalsIgnoreCase("menu")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            NationMenuService.openMenu(player, nation);
            return true;
        }

        if (args[0].equalsIgnoreCase("setsymbol")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null || !nation.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국왕만 상징 아이템을 변경할 수 있습니다.");
                return true;
            }
            org.bukkit.inventory.ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == org.bukkit.Material.AIR || !item.getType().name().endsWith("BANNER")) {
                player.sendMessage("§c손에 배너를 들고 있어야 합니다.");
                return true;
            }
            nation.setSymbol(item.clone());
            NationStorage.save(nation);
            player.sendMessage("§a상징 배너가 업데이트되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("upkeep")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            double amount = MaintenanceService.getWeeklyCost(nation);
            player.sendMessage("§e이번 주 유지비: " + amount + "C");
            return true;
        }

        if (args[0].equalsIgnoreCase("upgrade")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null || !nation.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c촌장만 국가를 승격할 수 있습니다.");
                return true;
            }
            String fail = me.continent.nation.service.NationTierService.checkRequirements(nation);
            if (fail != null) {
                player.sendMessage("§c" + fail);
                return true;
            }
            me.continent.nation.service.NationTierService.upgrade(nation);
            return true;
        }

        if (args[0].equalsIgnoreCase("tier")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null) {
                player.sendMessage("§c소속된 국가가 없습니다.");
                return true;
            }
            String name = me.continent.nation.service.NationTierService.getTierName(nation.getTier());
            player.sendMessage("§e현재 국가 등급: §b" + name);
            return true;
        }

        if (args[0].equalsIgnoreCase("tierinfo")) {
            me.continent.nation.service.NationTierService.openInfo(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("ignite")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null || !nation.isAuthorized(player.getUniqueId())) {
                player.sendMessage("§c국왕만 설정을 변경할 수 있습니다.");
                return true;
            }
            boolean allow;
            if (args.length >= 2) {
                allow = args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true");
            } else {
                allow = !nation.isMemberIgniteAllowed();
            }
            nation.setMemberIgniteAllowed(allow);
            NationStorage.save(nation);
            player.sendMessage("§e아군 점화 허용이 " + (allow ? "켜졌습니다" : "꺼졌습니다"));
            return true;
        }

        if (args[0].equalsIgnoreCase("setcore")) {
            Nation nation = NationManager.getByPlayer(player.getUniqueId());
            if (nation == null || !nation.getKing().equals(player.getUniqueId())) {
                player.sendMessage("§c국왕만 코어 위치를 변경할 수 있습니다.");
                return true;
            }

            String key = Nation.getChunkKey(player.getLocation().getChunk());
            if (!nation.getClaimedChunks().contains(key)) {
                player.sendMessage("§c해당 위치는 당신의 영토가 아닙니다.");
                return true;
            }

            CoreService.removeCore(nation);
            CoreService.placeCore(nation, player.getLocation());
            player.sendMessage("§a코어 위치가 변경되었습니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                player.sendMessage("§c사용법: /nation create <이름>");
                return true;
            }
            String name = args[1];

            if (NationManager.isPlayerInNation(player.getUniqueId())) {
                player.sendMessage("§c이미 국가에 소속되어 있습니다.");
                return true;
            }

            PlayerData data = PlayerDataManager.get(player.getUniqueId());
            if (data.getGold() < 30) {
                player.sendMessage("§c국가를 생성하려면 30G가 필요합니다.");
                return true;
            }

            Chunk chunk = player.getLocation().getChunk();
            if (NationManager.isNearOtherNation(chunk, 2)) {
                player.sendMessage("§c다른 국가와 너무 가까워 국가를 생성할 수 없습니다.");
                return true;
            }

            ConfirmationManager.request(player, () -> {
                Nation nation = MembershipService.createNation(name, player);
                if (nation == null) {
                    player.sendMessage("§c국가 생성에 실패했습니다. (중복 이름 등)");
                    return;
                }
                data.setGold(data.getGold() - 30);
                player.sendMessage("§a국가가 생성되었습니다: §e" + name);
            });
            return true;
        }

        player.sendMessage("§c알 수 없는 하위 명령어입니다. /nation 을 입력해 도움말을 확인하세요.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subs = Arrays.asList(
                "create", "disband", "claim", "invite", "invites", "accept", "deny",
                "members", "leave", "kick", "rename", "list", "setspawn", "setcore",
                "spawn", "chest", "menu", "setsymbol", "ignite", "upkeep", "treasury", "specialty", "upgrade", "tier", "tierinfo", "confirm", "chat"
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
