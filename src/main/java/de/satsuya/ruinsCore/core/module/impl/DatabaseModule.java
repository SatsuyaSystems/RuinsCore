package de.satsuya.ruinsCore.core.module.impl;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.module.PluginModule;

public final class DatabaseModule implements PluginModule {

    private final DatabaseManager databaseManager;

    public DatabaseModule(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public String getName() {
        return "DatabaseModule";
    }

    @Override
    public void onEnable() {
        databaseManager.connect();
        databaseManager.initializeSchema();
    }

    @Override
    public void onDisable() {
        databaseManager.close();
    }
}

