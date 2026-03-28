package de.satsuya.ruinsCore.core.command;

import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public interface CoreCommand extends CommandExecutor, TabCompleter {

    String getName();

    default String getDescription() {
        return "";
    }

    default String getUsage() {
        return "/" + getName();
    }

    default List<String> getAliases() {
        return Collections.emptyList();
    }

    default PermissionNode getPermission() {
        return null;
    }

    @Override
    default List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}

