package me.continent.enterprise;

import me.continent.ContinentPlugin;

/** Central access point for enterprise persistence. */
public class EnterpriseService {
    private static EnterpriseRepository repository;

    /** Initialize default repository. */
    public static void init(ContinentPlugin plugin) {
        repository = new YamlEnterpriseRepository(plugin);
    }

    public static void setRepository(EnterpriseRepository repo) {
        repository = repo;
    }

    public static EnterpriseRepository getRepository() {
        return repository;
    }

    public static void save(Enterprise enterprise) {
        repository.save(enterprise);
    }

    public static void loadAll() {
        repository.loadAll();
    }

    public static void saveAll() {
        repository.saveAll();
    }
}
