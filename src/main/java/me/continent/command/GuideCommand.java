package me.continent.command;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class GuideCommand implements TabExecutor {
    private static final Map<String, List<String>> GUIDES = new HashMap<>();

    static {
        GUIDES.put("kingdom", Arrays.asList(
                "Continent 왕국 가이드\n\n국가 시스템의 기본을 소개합니다.",
                "§l국가 관리\n- /kingdom create <이름>\n- /kingdom disband\n- /kingdom setking <플레이어>",
                "§l국가 관리\n- /kingdom addnation <국가>\n- /kingdom removenation <국가>\n- /kingdom setcapital <국가>\n- /kingdom accept|deny <국가명>\n- /kingdom leave",
                "§l재정과 연구\n- /kingdom treasury deposit|withdraw|balance\n- 수도 코어에서 연구 진행\n- /kingdom specialty",
                "§l정보와 이동\n- /kingdom info|list|members\n- /kingdom setflag\n- /kingdom spawn [국가]",
                "전쟁 명령은 /war declare, status, surrender 를 참고하세요."
        ));

        GUIDES.put("nation", Arrays.asList(
                "Continent 국가 가이드\n\n국가 시스템의 기본을 소개합니다.",
                "§l국가 생성과 해산\n- /nation create <이름>\n- /nation disband\n- /nation rename <새이름>\n- /nation color <색상>",
                "§l영토와 거점\n- /nation claim|unclaim\n- /nation setspawn\n- /nation setcore\n- /nation spawn",
                "§l금고와 창고\n- /nation treasury balance|deposit|withdraw\n- /nation chest\n- /nation upkeep",
                "§l구성원 관리\n- /nation invite|kick <플레이어>\n- /nation accept|deny <이름>\n- /nation members|list\n- /nation leave",
                "§l기타 기능\n- /nation setsymbol\n- /nation ignite <on|off>\n- /nation chat\n- /nation confirm"
        ));

        GUIDES.put("war", Arrays.asList(
                "Continent 전쟁 가이드\n\n국가 간 전쟁 시스템을 소개합니다.",
                "§l전쟁 선포\n- 촌장만 /war declare <국가명> (비용: config.yml의 war.declare-cost)\n- 관리자 /admin war start",
                "§l전쟁 진행\n- /war status 로 전황 확인\n- 코어 파괴 또는 /war surrender 로 종료",
                "§l전쟁 중 특징\n- 적 영토 파괴와 코어 공격 허용\n- 코어 파괴 시 패배",
                "추가 정보는 /admin war list|info 명령으로 확인 가능합니다."
        ));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            player.sendMessage("§6[가이드 목록]");
            player.sendMessage("§e/guide kingdom §7- 국가 시스템");
            player.sendMessage("§e/guide nation §7- 국가 시스템");
            player.sendMessage("§e/guide war §7- 전쟁 시스템");
            return true;
        }

        String topic = args[0].toLowerCase();
        List<String> pages = GUIDES.get(topic);
        if (pages == null) {
            player.sendMessage("§c알 수 없는 토픽입니다. /guide list로 확인하세요.");
            return true;
        }

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(Character.toUpperCase(topic.charAt(0)) + topic.substring(1) + " Guide");
        meta.setAuthor("Continent");
        for (String page : pages) {
            meta.addPage(page);
        }

        book.setItemMeta(meta);
        player.getInventory().addItem(book);
        player.sendMessage("§a" + Character.toUpperCase(topic.charAt(0)) + topic.substring(1) + " 가이드북을 받았습니다.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(GUIDES.keySet());
            options.add("list");
            return options.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }
}
