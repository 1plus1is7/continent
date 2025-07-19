package me.continent;

import me.continent.chat.VillageChatListener;
import me.continent.chat.GlobalChatListener;
import me.continent.chat.KingdomChatListener;
import me.continent.command.GoldCommand;
import me.continent.command.VillageCommand;
import me.continent.command.KingdomCommand;
import me.continent.economy.CentralBankDataManager;
import me.continent.listener.TerritoryListener;
import me.continent.listener.MaintenanceJoinListener;
import me.continent.protection.TerritoryProtectionListener;
import me.continent.protection.CoreProtectionListener;
import me.continent.protection.ProtectionStateListener;
import me.continent.village.service.ChestListener;
import me.continent.village.service.MaintenanceService;
import org.bukkit.plugin.java.JavaPlugin;
import me.continent.player.PlayerDataManager;
import me.continent.storage.VillageStorage;
import  me.continent.scoreboard.ScoreboardService;

public class ContinentPlugin extends JavaPlugin {
    private static ContinentPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        MaintenanceService.init(getConfig());
        MaintenanceService.schedule();

        // 명령어 등록
        getCommand("gold").setExecutor(new GoldCommand());
        getCommand("village").setExecutor(new VillageCommand());
        getCommand("kingdom").setExecutor(new KingdomCommand());

        // 중앙은행 데이터 로딩
        CentralBankDataManager.load();
        VillageStorage.loadAll(); // 저장된 모든 마을 불러오기
        me.continent.kingdom.KingdomStorage.loadAll();
        PlayerDataManager.loadAll();

        ScoreboardService.schedule();

        getServer().getPluginManager().registerEvents(new TerritoryListener(), this);
        getServer().getPluginManager().registerEvents(new VillageChatListener(), this);
        getServer().getPluginManager().registerEvents(new GlobalChatListener(), this);
        getServer().getPluginManager().registerEvents(new KingdomChatListener(), this);
        getServer().getPluginManager().registerEvents(new MaintenanceJoinListener(), this);
        getServer().getPluginManager().registerEvents(new TerritoryProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new CoreProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new ChestListener(), this);
        getServer().getPluginManager().registerEvents(new ProtectionStateListener(), this);



        getLogger().info("Continent 플러그인 활성화됨");
    }

    @Override
    public void onDisable() {
        // 중앙은행 데이터 저장
        CentralBankDataManager.save();
        me.continent.kingdom.KingdomStorage.saveAll();
        PlayerDataManager.saveAll();

        getLogger().info("Continent 플러그인 비활성화됨");
    }

    public static ContinentPlugin getInstance() {
        return instance;
    }
}
