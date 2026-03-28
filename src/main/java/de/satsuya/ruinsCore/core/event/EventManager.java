package de.satsuya.ruinsCore.core.event;

import de.satsuya.ruinsCore.core.scan.ClassScanner;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Set;

public final class EventManager {

    private final JavaPlugin plugin;
    private final LoggerUtil loggerUtil;

    public EventManager(JavaPlugin plugin, LoggerUtil loggerUtil) {
        this.plugin = plugin;
        this.loggerUtil = loggerUtil;
    }

    public void loadFromPackage(String packageName) {
        try {
            Set<Class<?>> scannedClasses = ClassScanner.scanPackage(plugin.getClass().getClassLoader(), packageName);
            int loaded = 0;

            for (Class<?> scannedClass : scannedClasses) {
                if (!Listener.class.isAssignableFrom(scannedClass)) {
                    continue;
                }
                if (scannedClass.isInterface() || Modifier.isAbstract(scannedClass.getModifiers())) {
                    continue;
                }

                Listener listener = (Listener) instantiate(scannedClass);
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                loaded++;
                loggerUtil.info("Listener registriert: " + scannedClass.getSimpleName());
            }

            loggerUtil.info("Dynamische Listener geladen: " + loaded + " aus " + packageName);
        } catch (Exception exception) {
            loggerUtil.severe("Listener-Package konnte nicht geladen werden: " + packageName, exception);
        }
    }

    public void unregisterAll() {
        HandlerList.unregisterAll(plugin);
    }

    private Object instantiate(Class<?> type) throws ReflectiveOperationException {
        Constructor<?> pluginConstructor = findConstructor(type, plugin.getClass());
        if (pluginConstructor != null) {
            return pluginConstructor.newInstance(plugin);
        }

        Constructor<?> javaPluginConstructor = findConstructor(type, JavaPlugin.class);
        if (javaPluginConstructor != null) {
            return javaPluginConstructor.newInstance(plugin);
        }

        Constructor<?> noArgs = type.getDeclaredConstructor();
        noArgs.setAccessible(true);
        return noArgs.newInstance();
    }

    private Constructor<?> findConstructor(Class<?> type, Class<?> parameterType) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor(parameterType);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}

