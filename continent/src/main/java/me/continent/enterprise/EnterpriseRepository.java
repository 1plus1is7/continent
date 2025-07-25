package me.continent.enterprise;

import java.io.File;

/** Repository interface for enterprise persistence. */
public interface EnterpriseRepository {
    void save(Enterprise enterprise);
    Enterprise load(File file);
    void loadAll();
    void saveAll();
}
