package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.invsee.InvseeService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Invsee Command - Schau dir das Inventar eines Spielers an
 */
public final class InvseeCommand implements CoreCommand {

    private final InvseeService invseeService;

    public InvseeCommand(RuinsCore plugin) {
        this.invseeService = plugin.getInvseeService();
    }

    @Override
    public String getName() {
        return "invsee";
    }

    @Override
    public String getDescription() {
        return "Schau dir das Inventar eines anderen Spielers an.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_INVSEE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§c/invsee <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cDu kannst dir dein eigenes Inventar nicht anschauen.");
            return true;
        }

        // Öffne das Inventar des Ziel-Spielers
        invseeService.openInventory(player, target);
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

