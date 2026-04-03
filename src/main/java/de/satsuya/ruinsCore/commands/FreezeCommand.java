package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.freeze.FreezeService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Freeze Command - Friere Spieler ein
 */
public final class FreezeCommand implements CoreCommand {

    private final FreezeService freezeService;

    public FreezeCommand(RuinsCore plugin) {
        this.freezeService = plugin.getFreezeService();
    }

    @Override
    public String getName() {
        return "freeze";
    }

    @Override
    public String getDescription() {
        return "Friere einen Spieler ein oder taue ihn auf.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_FREEZE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c/freeze <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        // Prüfe ob bereits gefroren
        boolean isFrozen = freezeService.isFrozen(target.getUniqueId());

        if (isFrozen) {
            // Taue auf
            freezeService.unfreezePlayer(target);
            sender.sendMessage("§a✓ §b" + target.getName() + " §awurde aufgetaut.");
        } else {
            // Friere ein
            freezeService.freezePlayer(target);
            sender.sendMessage("§a✓ §b" + target.getName() + " §awurde eingefroren.");
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
        }

        return completions;
    }
}

