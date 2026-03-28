package de.satsuya.ruinsCore.core.module.impl;

import de.satsuya.ruinsCore.core.event.EventManager;
import de.satsuya.ruinsCore.core.module.PluginModule;

public final class EventModule implements PluginModule {

    private final EventManager eventManager;
    private final String listenerPackage;

    public EventModule(EventManager eventManager, String listenerPackage) {
        this.eventManager = eventManager;
        this.listenerPackage = listenerPackage;
    }

    @Override
    public String getName() {
        return "EventModule";
    }

    @Override
    public void onEnable() {
        eventManager.loadFromPackage(listenerPackage);
    }

    @Override
    public void onDisable() {
        eventManager.unregisterAll();
    }
}

