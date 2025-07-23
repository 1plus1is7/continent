package me.continent.command;

import me.continent.war.War;
import me.continent.war.WarManager;
import me.continent.nation.NationManager;
import me.continent.nation.Nation;
import me.continent.economy.CentralBank;
import me.continent.nation.service.MaintenanceService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;

public class AdminCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("continent.admin")) {
            sender.sendMessage("§c관리자 권한이 필요합니다.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§6[Admin 명령어]");
            sender.sendMessage("§e/admin war list §7- 진행 중인 전쟁 목록");
            sender.sendMessage("§e/admin war start <A> <B> §7- 전쟁 시작");
            sender.sendMessage("§e/admin war end <국가명> §7- 전쟁 강제 종료");
            sender.sendMessage("§e/admin war info <국가명> §7- 전쟁 정보");
            sender.sendMessage("§e/admin rate get §7- 현재 환율 확인");
            sender.sendMessage("§e/admin rate set <값> §7- 환율 설정");
            sender.sendMessage("§e/admin rate reset §7- 환율 초기화");
            sender.sendMessage("§e/admin rate min|max <값> §7- 환율 범위 설정");
            sender.sendMessage("§e/admin maintenance get §7- 유지비 정보");
            sender.sendMessage("§e/admin maintenance setcost <값> §7- 기본 유지비 설정");
            sender.sendMessage("§e/admin maintenance setperchunk <값> §7- 청크당 비용 설정");
            sender.sendMessage("§e/admin maintenance setlimit <값> §7- 미납 한도 설정");
            return true;
        }

        if (args[0].equalsIgnoreCase("war")) {
            if (args.length >= 2 && args[1].equalsIgnoreCase("list")) {
                Collection<War> wars = WarManager.getWars();
                if (wars.isEmpty()) {
                    sender.sendMessage("§6[전쟁] §f현재 진행 중인 전쟁이 없습니다.");
                } else {
                    sender.sendMessage("§6[전쟁 목록]");
                    for (War war : wars) {
                        sender.sendMessage("§f" + war.getAttacker() + " vs " + war.getDefender());
                    }
                }
                return true;
            }
            if (args.length >= 4 && args[1].equalsIgnoreCase("start")) {
                Nation atk = NationManager.getByName(args[2]);
                Nation def = NationManager.getByName(args[3]);
                if (atk == null || def == null) {
                    sender.sendMessage("§c국가을 찾을 수 없습니다.");
                    return true;
                }
                if (WarManager.isAtWar(atk.getName(), def.getName())) {
                    sender.sendMessage("§c이미 전쟁 중입니다.");
                    return true;
                }
                WarManager.declareWar(atk, def);
                sender.sendMessage("§e[전쟁] §f전쟁을 시작했습니다: " + atk.getName() + " vs " + def.getName());
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("end")) {
                War war = WarManager.getWar(args[2]);
                if (war == null) {
                    sender.sendMessage("§c해당 국가의 전쟁이 존재하지 않습니다.");
                    return true;
                }
                WarManager.endWar(war);
                sender.sendMessage("§e[전쟁] §f전쟁을 강제 종료했습니다.");
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("info")) {
                War war = WarManager.getWar(args[2]);
                if (war == null) {
                    sender.sendMessage("§c전쟁 정보가 없습니다.");
                    return true;
                }
                sender.sendMessage("§6[전쟁 정보]");
                sender.sendMessage("§f공격국: §e" + war.getAttacker());
                sender.sendMessage("§f방어국: §e" + war.getDefender());
                sender.sendMessage("§f파괴된 국가: §e" + war.getDestroyedNations().size());
                return true;
            }
            sender.sendMessage("§c사용법: /admin war <list|start|end|info>");
            return true;
        }

        if (args[0].equalsIgnoreCase("rate")) {
            if (args.length >= 2 && args[1].equalsIgnoreCase("get")) {
                sender.sendMessage("§6[환율] §f현재 환율: §e" + CentralBank.getExchangeRate() + "G");
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("set")) {
                try {
                    double value = Double.parseDouble(args[2]);
                    CentralBank.setExchangeRate(value);
                    sender.sendMessage("§6[환율] §f환율을 " + CentralBank.getExchangeRate() + "G 로 설정했습니다.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c숫자를 입력해야 합니다.");
                }
                return true;
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("reset")) {
                CentralBank.resetExchangeRate();
                sender.sendMessage("§6[환율] §f환율을 초기화했습니다. 현재 환율: §e" + CentralBank.getExchangeRate() + "G");
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("min")) {
                try {
                    double v = Double.parseDouble(args[2]);
                    CentralBank.setMinRate(v);
                    sender.sendMessage("§6[환율] §f최소 환율을 " + CentralBank.getMinRate() + "G 로 설정했습니다.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c숫자를 입력해야 합니다.");
                }
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("max")) {
                try {
                    double v = Double.parseDouble(args[2]);
                    CentralBank.setMaxRate(v);
                    sender.sendMessage("§6[환율] §f최대 환율을 " + CentralBank.getMaxRate() + "G 로 설정했습니다.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c숫자를 입력해야 합니다.");
                }
                return true;
            }
            sender.sendMessage("§c사용법: /admin rate <get|set|reset|min|max>");
            return true;
        }

        if (args[0].equalsIgnoreCase("maintenance")) {
            if (args.length >= 2 && args[1].equalsIgnoreCase("get")) {
                sender.sendMessage("§6[유지비] §f기본: §e" + MaintenanceService.getCost() + "G");
                sender.sendMessage("§6[유지비] §f청크당: §e" + MaintenanceService.getPerChunkCost() + "G");
                sender.sendMessage("§6[유지비] §f미납 허용 주수: §e" + MaintenanceService.getUnpaidLimit());
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("setcost")) {
                try {
                    double v = Double.parseDouble(args[2]);
                    MaintenanceService.setCost(v);
                    sender.sendMessage("§6[유지비] §f기본 유지비를 " + MaintenanceService.getCost() + "G 로 설정했습니다.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c숫자를 입력해야 합니다.");
                }
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("setperchunk")) {
                try {
                    double v = Double.parseDouble(args[2]);
                    MaintenanceService.setPerChunkCost(v);
                    sender.sendMessage("§6[유지비] §f청크당 비용을 " + MaintenanceService.getPerChunkCost() + "G 로 설정했습니다.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c숫자를 입력해야 합니다.");
                }
                return true;
            }
            if (args.length >= 3 && args[1].equalsIgnoreCase("setlimit")) {
                try {
                    int v = Integer.parseInt(args[2]);
                    MaintenanceService.setUnpaidLimit(v);
                    sender.sendMessage("§6[유지비] §f미납 허용 주수를 " + MaintenanceService.getUnpaidLimit() + "주 로 설정했습니다.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c숫자를 입력해야 합니다.");
                }
                return true;
            }
            sender.sendMessage("§c사용법: /admin maintenance <get|setcost|setperchunk|setlimit>");
            return true;
        }

        sender.sendMessage("§c알 수 없는 하위 명령어입니다.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("war", "rate", "maintenance").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("war")) {
            return Arrays.asList("list", "start", "end", "info").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("war") && args[1].equalsIgnoreCase("start")) {
            return NationManager.getAll().stream()
                    .map(Nation::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("war") && args[1].equalsIgnoreCase("start")) {
            return NationManager.getAll().stream()
                    .map(Nation::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[3].toLowerCase()))
                    .toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("war") && (args[1].equalsIgnoreCase("end") || args[1].equalsIgnoreCase("info"))) {
            return NationManager.getAll().stream()
                    .map(Nation::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("rate")) {
            return Arrays.asList("get", "set", "reset", "min", "max").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("maintenance")) {
            return Arrays.asList("get", "setcost", "setperchunk", "setlimit").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("maintenance")) {
            return List.of();
        }

        return Collections.emptyList();
    }
}
