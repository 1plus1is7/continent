package me.continent;

import me.continent.chat.KingdomChatListener;
import me.continent.command.GoldCommand;
import me.continent.command.KingdomCommand;
import me.continent.economy.CentralBankDataManager;
import me.continent.listener.TerritoryListener;
import me.continent.protection.TerritoryProtectionListener;
import me.continent.protection.CoreProtectionListener;
import org.bukkit.plugin.java.JavaPlugin;
import me.continent.player.PlayerDataManager;
import me.continent.storage.KingdomStorage;
import  me.continent.scoreboard.ScoreboardService;

public class ContinentPlugin extends JavaPlugin {
    private static ContinentPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        // 명령어 등록
        getCommand("gold").setExecutor(new GoldCommand());
        getCommand("kingdom").setExecutor(new KingdomCommand());

        // 중앙은행 데이터 로딩
        CentralBankDataManager.load();
        KingdomStorage.loadAll(); // 저장된 모든 국가 불러오기
        PlayerDataManager.loadAll();

        ScoreboardService.schedule();

        getServer().getPluginManager().registerEvents(new TerritoryListener(), this);
        getServer().getPluginManager().registerEvents(new KingdomChatListener(), this);
        getServer().getPluginManager().registerEvents(new TerritoryProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new CoreProtectionListener(), this);



        getLogger().info("Continent 플러그인 활성화됨");
    }

    @Override
    public void onDisable() {
        // 중앙은행 데이터 저장
        CentralBankDataManager.save();

        PlayerDataManager.saveAll();

        getLogger().info("Continent 플러그인 비활성화됨");
    }

    public static ContinentPlugin getInstance() {
        return instance;
    }
}
