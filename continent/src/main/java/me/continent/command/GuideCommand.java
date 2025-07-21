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
                "§l마을 관리\n- /kingdom addvillage <마을>\n- /kingdom removevillage <마을>\n- /kingdom setcapital <마을>\n- /kingdom accept|deny <국가명>\n- /kingdom leave",
                "§l재정과 연구\n- /kingdom treasury deposit|withdraw|balance\n- 수도 코어에서 연구 진행\n- /kingdom specialty",
                "§l정보와 이동\n- /kingdom info|list|members\n- /kingdom setflag\n- /kingdom spawn [마을]",
                "전쟁 명령은 /war declare, status, surrender 를 참고하세요."
        ));

        GUIDES.put("village", Arrays.asList(
                "Continent 마을 가이드\n\n마을 시스템의 기본을 소개합니다.",
                "§l마을 생성과 해산\n- /village create <이름>\n- /village disband\n- /village rename <새이름>\n- /village color <색상>",
                "§l영토와 거점\n- /village claim|unclaim\n- /village setspawn\n- /village setcore\n- /village spawn",
                "§l금고와 창고\n- /village treasury balance|deposit|withdraw\n- /village chest\n- /village upkeep",
                "§l구성원 관리\n- /village invite|kick <플레이어>\n- /village accept|deny <이름>\n- /village members|list\n- /village leave",
                "§l기타 기능\n- /village setsymbol\n- /village ignite <on|off>\n- /village chat\n- /village confirm"
        ));

        GUIDES.put("war", Arrays.asList(
                "Continent 전쟁 가이드\n\n국가 간 전쟁 시스템을 소개합니다.",
                "§l전쟁 선포\n- 국왕만 /war declare <국가명>\n- 관리자 /admin war start",
                "§l전쟁 진행\n- /war status 로 전황 확인\n- 코어 파괴 또는 /war surrender 로 종료",
                "§l전쟁 중 특징\n- 적 영토 파괴와 코어 공격 허용\n- 수도 코어 파괴 시 즉시 종전",
                "§l마을 점령\n- 파괴된 마을의 소유권은 전쟁 후 공격국으로 이전",
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
            player.sendMessage("§e/guide village §7- 마을 시스템");
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
