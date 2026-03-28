package de.satsuya.ruinsCore.core.module;

public interface PluginModule {

    String getName();

    void onEnable();

    void onDisable();
}

