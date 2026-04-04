package de.satsuya.ruinsCore.core.util;

import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public final class LoggerUtil {

    private final Plugin plugin;

    public LoggerUtil(Plugin plugin) {
        this.plugin = plugin;
    }

    public void info(String message) {
        plugin.getLogger().info(prefix(message));
    }

    public void warning(String message) {
        plugin.getLogger().warning(prefix(message));
    }

    public void severe(String message) {
        plugin.getLogger().severe(prefix(message));
    }

    public void severe(String message, Throwable throwable) {
        plugin.getLogger().log(Level.SEVERE, prefix(message), throwable);
    }

    private String prefix(String message) {
        return "[SYSTEM] " + message;
    }
}

