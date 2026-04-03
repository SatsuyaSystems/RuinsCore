package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import de.satsuya.ruinsCore.core.warning.WarningService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * WarnsDelete Command - Lösche Verwarnungen eines Spielers
 */
public final class WarnsDeleteCommand implements CoreCommand {

    private final WarningService warningService;

    public WarnsDeleteCommand(RuinsCore plugin) {
        this.warningService = plugin.getWarningService();
    }

    @Override
    public String getName() {
        return "warnsdelete";
    }

    @Override
    public String getDescription() {
        return "Lösche Verwarnungen eines Spielers.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_WARNS;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/warnsdelete <Spieler> <all|ID>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        String action = args[1].toLowerCase();

        if (action.equals("all")) {
            // Lösche alle Verwarnungen
            int count = warningService.getWarningCount(target.getUniqueId());
            warningService.removeAllWarnings(target.getUniqueId());
            sender.sendMessage("§a✓ §b" + count + " §aVerwarnungen von §b" + target.getName() + " §agelöscht.");
            target.sendMessage("§6Deine Verwarnungen wurden gelöscht!");
            return true;
        }

        // Versuche ID zu parsen
        try {
            int warningId = Integer.parseInt(action);
            warningService.removeWarning(warningId);
            sender.sendMessage("§a✓ Verwarnung §b#" + warningId + " §agelöscht.");
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cUngültige ID oder Aktion. Nutze: all oder eine Nummer.");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 2) {
            completions.add("all");
        }

        return completions;
    }
}

