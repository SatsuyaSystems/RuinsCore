package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.endersee.EnderseeService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Endersee Command - Schau dir den Ender-Chest eines Spielers an
 */
public final class EnderseeCommand implements CoreCommand {

    private final EnderseeService enderseeService;

    public EnderseeCommand(RuinsCore plugin) {
        this.enderseeService = plugin.getEnderseeService();
    }

    @Override
    public String getName() {
        return "endersee";
    }

    @Override
    public String getDescription() {
        return "Schau dir den Ender-Chest eines anderen Spielers an.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_ENDERSEE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§c/endersee <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cDu kannst dir deinen eigenen Ender-Chest nicht anschauen.");
            return true;
        }

        // Öffne den Ender-Chest des Ziel-Spielers
        enderseeService.openEnderChest(player, target);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender instanceof Player senderPlayer) {
                // Wenn Sender ein Spieler ist: zeige alle außer sich selbst
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.equals(senderPlayer)) {
                        completions.add(player.getName());
                    }
                }
            } else {
                // Wenn Sender kein Spieler ist (z.B. Console): zeige alle Spieler
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}

