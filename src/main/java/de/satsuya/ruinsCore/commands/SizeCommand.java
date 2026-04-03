package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import de.satsuya.ruinsCore.core.size.SizeService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Size Command - Ändere die Spielergröße
 */
public final class SizeCommand implements CoreCommand {

    private final SizeService sizeService;

    public SizeCommand(RuinsCore plugin) {
        this.sizeService = plugin.getSizeService();
    }

    @Override
    public String getName() {
        return "size";
    }

    @Override
    public String getDescription() {
        return "Ändere die Spielergröße (0.6 - 1.1).";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_SIZE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c/size <Größe> oder /size <Spieler> <Größe>");
            sender.sendMessage("§6Größe: " + sizeService.getMinSize() + " - " + sizeService.getMaxSize());
            return true;
        }

        // Prüfe ob Spieler oder Admin-Command
        if (args.length == 1) {
            // Nur Größe angegeben -> Sender ändern
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
                return true;
            }

            Player player = (Player) sender;
            float size = parseSize(args[0]);

            if (size == -1) {
                sender.sendMessage("§cUngültige Größe. Bereich: " + sizeService.getMinSize() + " - " + sizeService.getMaxSize());
                return true;
            }

            sizeService.setPlayerSize(player.getUniqueId(), size);
            sizeService.applyPlayerSize(player);
            sender.sendMessage("§a✓ Größe auf §b" + size + " §agesetzt.");
            return true;
        }

        // Zwei Argumente: Spieler und Größe
        if (args.length == 2) {
            // Prüfe Permission für andere
            if (!sender.hasPermission("ruinscore.command.size.other")) {
                sender.sendMessage("§cDu hast keine Berechtigung, die Größe anderer Spieler zu ändern.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cSpieler nicht gefunden.");
                return true;
            }

            float size = parseSize(args[1]);
            if (size == -1) {
                sender.sendMessage("§cUngültige Größe. Bereich: " + sizeService.getMinSize() + " - " + sizeService.getMaxSize());
                return true;
            }

            sizeService.setPlayerSize(target.getUniqueId(), size);
            sizeService.applyPlayerSize(target);
            sender.sendMessage("§a✓ Größe von §b" + target.getName() + " §aauf §b" + size + " §agesetzt.");
            target.sendMessage("§b" + sender.getName() + " §ehat deine Größe auf §b" + size + " §egesetzt.");
            return true;
        }

        sender.sendMessage("§cZu viele Argumente.");
        return true;
    }

    /**
     * Versucht eine Größe zu parsen und zu validieren
     */
    private float parseSize(String input) {
        try {
            float size = Float.parseFloat(input);
            if (size >= sizeService.getMinSize() && size <= sizeService.getMaxSize()) {
                return size;
            }
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Könnte Größe oder Spieler sein
            if (sender.hasPermission("ruinscore.command.size.other")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
            completions.add("0.6");
            completions.add("0.8");
            completions.add("1.0");
            completions.add("1.1");
        } else if (args.length == 2) {
            completions.add("0.6");
            completions.add("0.8");
            completions.add("1.0");
            completions.add("1.1");
        }

        return completions;
    }
}

