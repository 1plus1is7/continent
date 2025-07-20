package me.continent.command;

import org.bukkit.Material;
import org.bukkit.command.*;
import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class SeasonGuideCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.");
            return true;
        }

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("계절 가이드");
        meta.setAuthor("Continent");

        meta.addPage("Continent 계절 가이드\n\n봄, 여름, 가을, 겨울 순으로 진행됩니다.");
        meta.addPage("봄\n- 작물 성장 속도 +20%\n- 기온 디버프 저항 단계 +1\n- 체온 회복 속도 +1.0");
        meta.addPage("여름\n- 기온 ≥ 35도: 기절\n- 기온 ≥ 33도: Blindness 등\n- ShelterLevel 보정 가능");
        meta.addPage("가을\n- 작물 성장 속도 정상화\n- 기온 ≤ 25도: 디버프 없음\n- 철 갑옷 착용 시 더위 완화");
        meta.addPage("겨울\n- 기온 ≤ -5도: 기절\n- 기온 ≤ -3도: Blindness, Slowness II, Mining Fatigue\n- 물 속/가루눈: 체온 추가 하강");
        meta.addPage("세부 설정은 config.yml 참고\n/season info로 현재 계절을 확인하세요.");

        book.setItemMeta(meta);
        player.getInventory().addItem(book);
        player.sendMessage("§a계절 가이드 책을 받았습니다.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
