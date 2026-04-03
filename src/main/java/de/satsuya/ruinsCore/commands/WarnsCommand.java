package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import de.satsuya.ruinsCore.core.warning.WarningService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Warns Command - Zeige Verwarnungen eines Spielers mit Delete-Funktion
 */
public final class WarnsCommand implements CoreCommand {

    private final WarningService warningService;

    public WarnsCommand(RuinsCore plugin) {
        this.warningService = plugin.getWarningService();
    }

    @Override
    public String getName() {
        return "warns";
    }

    @Override
    public String getDescription() {
        return "Zeige Verwarnungen eines Spielers.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_WARNS;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c/warns <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        int warnings = warningService.getWarningCount(target.getUniqueId());
        int remaining = warningService.getWarningsUntilBan(target.getUniqueId());

        sender.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§6Spieler: §b" + target.getName());
        sender.sendMessage("§6Verwarnungen: §b" + warnings + "§6/§b5");
        
        if (warnings > 0) {
            sender.sendMessage("§6Verbleibend: §b" + remaining);
            if (remaining == 0) {
                sender.sendMessage("§c⚠ SPIELER SOLLTE GEBANNT SEIN!");
            }
            
            sender.sendMessage("§6━━ Verwarnungen ━━");
            
            // Zeige alle Verwarnungen mit Delete-Buttons
            List<WarningService.WarningInfo> allWarnings = warningService.getAllWarnings(target.getUniqueId());
            for (WarningService.WarningInfo warning : allWarnings) {
                // Erstelle Delete-Button für diese Verwarnung
                Component deleteButton = Component.text("§c[X]", NamedTextColor.RED)
                    .clickEvent(ClickEvent.runCommand("/warnsdelete " + target.getName() + " " + warning.id))
                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                        Component.text("§cKlick zum Löschen", NamedTextColor.RED)
                    ));
                
                Component warningLine = Component.text(
                    "§7[§b#" + warning.id + "§7] §cGrund: §f" + warning.reason + " ",
                    NamedTextColor.WHITE
                ).append(deleteButton);
                
                if (sender instanceof Player) {
                    ((Player) sender).sendMessage(warningLine);
                } else {
                    sender.sendMessage("§7[§b#" + warning.id + "§7] §cGrund: §f" + warning.reason);
                }
            }
            
            // Zeige "Alle löschen" Button
            Component deleteAllComponent = Component.text("§c[ALLE LÖSCHEN]", NamedTextColor.RED)
                    .clickEvent(ClickEvent.runCommand("/warnsdelete " + target.getName() + " all"));
            
            if (sender instanceof Player) {
                ((Player) sender).sendMessage(Component.text("§6Oder klicke auf: ").append(deleteAllComponent));
            }
        } else {
            sender.sendMessage("§aKeine Verwarnungen.");
        }
        
        sender.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        return completions;
    }
}

