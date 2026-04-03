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
 * Warn Command - Verwarnt einen Spieler
 */
public final class WarnCommand implements CoreCommand {

    private final WarningService warningService;

    public WarnCommand(RuinsCore plugin) {
        this.warningService = plugin.getWarningService();
    }

    @Override
    public String getName() {
        return "warn";
    }

    @Override
    public String getDescription() {
        return "Verwarnt einen Spieler.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_WARN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/warn <Spieler> <Grund...>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        // Grund zusammenbauen
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]);
            if (i < args.length - 1) {
                reasonBuilder.append(" ");
            }
        }
        String reason = reasonBuilder.toString();

        // Bekomme Admin-UUID
        String adminUuid = sender instanceof Player ? ((Player) sender).getUniqueId().toString() : "CONSOLE";

        // Verwarnung hinzufügen
        boolean banned = warningService.warnPlayer(target.getUniqueId(), 
            sender instanceof Player ? ((Player) sender).getUniqueId() : new java.util.UUID(0, 0), 
            reason);

        int warnings = warningService.getWarningCount(target.getUniqueId());

        if (banned) {
            sender.sendMessage("§a✓ §b" + target.getName() + " §awurde verwarnt und wegen 5 Verwarnungen ge-bannt!");
            target.kickPlayer("§cDu wurdest wegen zu vieler Verwarnungen aus dem Server entfernt!");
        } else {
            int remaining = warningService.getWarningsUntilBan(target.getUniqueId());
            sender.sendMessage("§a✓ §b" + target.getName() + " §awurde verwarnt. (§b" + warnings + "§a/§b5§a) - §b" + remaining + " §averbleibend");
            target.sendMessage("§6Du wurdest verwarnt: §c" + reason);
            target.sendMessage("§6Grund: §c" + reason);
            target.sendMessage("§6Verwarnungen: §b" + warnings + "§6/§b5 §7(§b" + remaining + " §7verbleibend)");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 2) {
            completions.add("<Grund...>");
        }

        return completions;
    }
}

