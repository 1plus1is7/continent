package me.continent.command;

import me.continent.nation.Nation;
import me.continent.nation.NationManager;
import me.continent.storage.NationStorage;
import me.continent.storage.UnionStorage;
import me.continent.union.Union;
import me.continent.union.UnionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Basic command executor for union management.
 */
public class UnionCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6[Union 명령어]");
            player.sendMessage("§e/union create <이름> §7- 연합 생성");
            player.sendMessage("§e/union invite <nation> §7- 국가 초대");
            player.sendMessage("§e/union join §7- 초대 수락");
            player.sendMessage("§e/union deny §7- 초대 거절");
            player.sendMessage("§e/union leave §7- 연합 탈퇴");
            player.sendMessage("§e/union disband §7- 연합 해산");
            player.sendMessage("§e/union members §7- 소속 국가 목록");
            player.sendMessage("§e/union setleader <nation> §7- 리더 변경");
            return true;
        }

        String sub = args[0].toLowerCase();
        Nation nation = NationManager.getByPlayer(player.getUniqueId());
        if (nation == null) {
            player.sendMessage("§c소속된 국가가 없습니다.");
            return true;
        }

        Union union = UnionManager.getByNation(nation.getName());

        switch (sub) {
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage("§c/union create <이름>");
                    return true;
                }
                if (union != null) {
                    player.sendMessage("§c이미 연합에 소속되어 있습니다.");
                    return true;
                }
                String name = args[1];
                Union u = UnionManager.createUnion(name, nation.getName());
                if (u == null) {
                    player.sendMessage("§c이미 사용 중인 이름이거나 생성할 수 없습니다.");
                } else {
                    UnionStorage.saveAll();
                    player.sendMessage("§a연합이 생성되었습니다: " + name);
                }
                return true;
            }
            case "invite" -> {
                if (union == null || !union.getLeader().equalsIgnoreCase(nation.getName())) {
                    player.sendMessage("§c연합 리더만 초대할 수 있습니다.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§c/union invite <nation>");
                    return true;
                }
                Nation target = NationManager.getByName(args[1]);
                if (target == null) {
                    player.sendMessage("§c국가을 찾을 수 없습니다.");
                    return true;
                }
                if (UnionManager.getByNation(target.getName()) != null) {
                    player.sendMessage("§c해당 국가는 이미 다른 연합에 속해 있습니다.");
                    return true;
                }
                union.getInvites().add(target.getName());
                UnionStorage.saveAll();
                player.sendMessage("§a초대장을 보냈습니다.");
                return true;
            }
            case "join" -> {
                if (union != null) {
                    player.sendMessage("§c이미 다른 연합에 속해 있습니다.");
                    return true;
                }
                for (Union u : UnionManager.getAll()) {
                    if (u.getInvites().remove(nation.getName())) {
                        UnionManager.addNation(u, nation.getName());
                        UnionStorage.saveAll();
                        Bukkit.broadcastMessage("§e[연합] §f" + nation.getName() + " 국가가 " + u.getName() + " 연합에 가입했습니다.");
                        return true;
                    }
                }
                player.sendMessage("§c받은 초대가 없습니다.");
                return true;
            }
            case "deny" -> {
                boolean found = false;
                for (Union u : UnionManager.getAll()) {
                    if (u.getInvites().remove(nation.getName())) {
                        found = true;
                    }
                }
                if (found) {
                    UnionStorage.saveAll();
                    player.sendMessage("§a초대를 거절했습니다.");
                } else {
                    player.sendMessage("§c받은 초대가 없습니다.");
                }
                return true;
            }
            case "leave" -> {
                if (union == null) {
                    player.sendMessage("§c연합에 소속되어 있지 않습니다.");
                    return true;
                }
                if (union.getLeader().equalsIgnoreCase(nation.getName())) {
                    player.sendMessage("§c리더는 연합을 탈퇴할 수 없습니다. 먼저 리더를 변경하세요.");
                    return true;
                }
                UnionManager.removeNation(union, nation.getName());
                UnionStorage.saveAll();
                Bukkit.broadcastMessage("§e[연합] §f" + nation.getName() + " 국가가 " + union.getName() + " 연합을 탈퇴했습니다.");
                return true;
            }
            case "disband" -> {
                if (union == null || !union.getLeader().equalsIgnoreCase(nation.getName())) {
                    player.sendMessage("§c연합 리더만 해산할 수 있습니다.");
                    return true;
                }
                UnionManager.unregister(union);
                UnionStorage.saveAll();
                Bukkit.broadcastMessage("§c[연합] " + union.getName() + " 연합이 해산되었습니다.");
                return true;
            }
            case "members" -> {
                if (union == null) {
                    player.sendMessage("§c연합에 소속되어 있지 않습니다.");
                    return true;
                }
                player.sendMessage("§6[연합 국가 목록]");
                for (String n : union.getNations()) {
                    player.sendMessage("§f- " + n + (n.equalsIgnoreCase(union.getLeader()) ? " §e(리더)" : ""));
                }
                return true;
            }
            case "setleader" -> {
                if (union == null || !union.getLeader().equalsIgnoreCase(nation.getName())) {
                    player.sendMessage("§c연합 리더만 변경할 수 있습니다.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§c/union setleader <nation>");
                    return true;
                }
                if (!union.hasNation(args[1])) {
                    player.sendMessage("§c해당 국가는 연합에 속해 있지 않습니다.");
                    return true;
                }
                union.setLeader(args[1]);
                UnionStorage.saveAll();
                Bukkit.broadcastMessage("§e[연합] §f" + union.getName() + " 연합의 리더가 " + args[1] + " 국가로 변경되었습니다.");
                return true;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create", "invite", "join", "deny", "leave", "disband", "members", "setleader");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {
            return NationManager.getAll().stream()
                    .map(Nation::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("setleader")) {
            Union union = null;
            if (sender instanceof Player p) {
                Nation n = NationManager.getByPlayer(p.getUniqueId());
                if (n != null) union = UnionManager.getByNation(n.getName());
            }
            if (union != null) {
                return union.getNations().stream()
                        .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }
        }
        return Collections.emptyList();
    }
}
