package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.pentagram.PentagramService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command zum Erstellen eines Pentagramms aus Feuer
 */
public final class PentagramCommand implements CoreCommand {

    private final PentagramService pentagramService;
    private final RuinsCore plugin;

    public PentagramCommand(RuinsCore plugin) {
        this.plugin = plugin;
        this.pentagramService = plugin.getPentagramService();
    }

    @Override
    public String getName() {
        return "pentagram";
    }

    @Override
    public String getDescription() {
        return "Erstelle ein Pentagramm aus Feuer an deiner Position";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_PENTAGRAM;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        // Radius (default 5 Blöcke)
        double radius = 5.0;

        if (args.length > 0) {
            try {
                radius = Double.parseDouble(args[0]);
                if (radius < 1.0 || radius > 20.0) {
                    player.sendMessage("§c✗ Der Radius muss zwischen 1 und 20 Blöcken liegen!");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§c✗ Ungültiger Radius: " + args[0]);
                return true;
            }
        }

        pentagramService.createPentagram(player, radius);
        player.sendMessage("§d✨ Pentagramm erstellt! Es bleibt für 10 Sekunden...");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("5");
            completions.add("10");
            completions.add("15");
        }

        return completions;
    }
}

