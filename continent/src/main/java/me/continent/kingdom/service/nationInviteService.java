package me.continent.kingdom.service;

import me.continent.village.Village;

import java.util.Set;

public class nationInviteService {

    public static void sendInvite(String kingdomName, Village village) {
        village.addnationInvite(kingdomName);
        me.continent.storage.VillageStorage.save(village);
    }

    public static void removeInvite(String kingdomName, Village village) {
        village.removenationInvite(kingdomName);
        me.continent.storage.VillageStorage.save(village);
    }

    public static Set<String> getInvites(Village village) {
        return village.getnationInvites();
    }
}
