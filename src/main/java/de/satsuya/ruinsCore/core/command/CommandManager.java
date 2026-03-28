package de.satsuya.ruinsCore.core.command;

import de.satsuya.ruinsCore.core.scan.ClassScanner;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CommandManager {

    private final JavaPlugin plugin;
    private final LoggerUtil loggerUtil;
    private final Set<String> registeredCommandKeys = new HashSet<>();

    public CommandManager(JavaPlugin plugin, LoggerUtil loggerUtil) {
        this.plugin = plugin;
        this.loggerUtil = loggerUtil;
    }

    public void loadFromPackage(String packageName) {
        try {
            Set<Class<?>> scannedClasses = ClassScanner.scanPackage(plugin.getClass().getClassLoader(), packageName);
            int loaded = 0;

            for (Class<?> scannedClass : scannedClasses) {
                if (!CoreCommand.class.isAssignableFrom(scannedClass)) {
                    continue;
                }
                if (scannedClass.isInterface() || Modifier.isAbstract(scannedClass.getModifiers())) {
                    continue;
                }

                CoreCommand command = (CoreCommand) instantiate(scannedClass);
                register(command);
                loaded++;
            }

            loggerUtil.info("Dynamische Commands geladen: " + loaded + " aus " + packageName);
        } catch (Exception exception) {
            loggerUtil.severe("Command-Package konnte nicht geladen werden: " + packageName, exception);
        }
    }

    public void register(CoreCommand command) {
        try {
            CommandMap commandMap = getCommandMap();
            PluginCommand pluginCommand = createPluginCommand(command.getName());

            pluginCommand.setDescription(command.getDescription());
            pluginCommand.setUsage(command.getUsage());
            pluginCommand.setAliases(command.getAliases());
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);

            if (command.getPermission() != null) {
                pluginCommand.setPermission(command.getPermission().getNode());
            }

            commandMap.register(plugin.getName().toLowerCase(), pluginCommand);
            registeredCommandKeys.add(command.getName().toLowerCase());
            for (String alias : command.getAliases()) {
                registeredCommandKeys.add(alias.toLowerCase());
            }

            loggerUtil.info("Command registriert: /" + command.getName());
        } catch (Exception exception) {
            loggerUtil.severe("Command konnte nicht registriert werden: " + command.getName(), exception);
        }
    }

    public void unregisterAll() {
        try {
            CommandMap commandMap = getCommandMap();
            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, org.bukkit.command.Command> knownCommands =
                    (Map<String, org.bukkit.command.Command>) knownCommandsField.get(commandMap);

            List<String> keysToRemove = new ArrayList<>();
            for (String key : knownCommands.keySet()) {
                String normalized = key.contains(":") ? key.substring(key.indexOf(':') + 1) : key;
                if (registeredCommandKeys.contains(normalized.toLowerCase())) {
                    keysToRemove.add(key);
                }
            }

            for (String key : keysToRemove) {
                knownCommands.remove(key);
            }

            registeredCommandKeys.clear();
            loggerUtil.info("Alle dynamischen Commands wurden deregistriert.");
        } catch (Exception exception) {
            loggerUtil.severe("Dynamische Commands konnten nicht deregistriert werden.", exception);
        }
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

    private PluginCommand createPluginCommand(String name) throws ReflectiveOperationException {
        Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        constructor.setAccessible(true);
        return constructor.newInstance(name, plugin);
    }

    private CommandMap getCommandMap() throws ReflectiveOperationException {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        return (CommandMap) commandMapField.get(Bukkit.getServer());
    }
}


