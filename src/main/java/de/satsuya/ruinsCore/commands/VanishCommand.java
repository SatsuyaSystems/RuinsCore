package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import de.satsuya.ruinsCore.core.vanish.VanishService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Vanish Command - Mache dich oder andere unsichtbar
 */
public final class VanishCommand implements CoreCommand {

    private final VanishService vanishService;

    public VanishCommand(RuinsCore plugin) {
        this.vanishService = plugin.getVanishService();
    }

    @Override
    public String getName() {
        return "vanish";
    }

    @Override
    public String getDescription() {
        return "Mache dich oder andere unsichtbar.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_VANISH;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c/vanish [Spieler]");
                return true;
            }
            player = (Player) sender;
        } else {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§cSpieler nicht gefunden.");
                return true;
            }
        }

        // Prüfe ob bereits vanished
        boolean isVanished = vanishService.isVanished(player.getUniqueId());

        if (isVanished) {
            // Entferne Vanish
            vanishService.setVanished(player, false);

            if (player.equals(sender)) {
                player.sendMessage("§a✓ Du bist nun §bsichtbar§a.");
            } else {
                sender.sendMessage("§a✓ §b" + player.getName() + " §aist nun §bsichtbar§a.");
                player.sendMessage("§b" + sender.getName() + " §ehat dich sichtbar gemacht.");
            }
        } else {
            // Addiere Vanish
            vanishService.setVanished(player, true);

            if (player.equals(sender)) {
                player.sendMessage("§a✓ Du bist nun §bunsichtbar§a.");
            } else {
                sender.sendMessage("§a✓ §b" + player.getName() + " §aist nun §bunsichtbar§a.");
                player.sendMessage("§b" + sender.getName() + " §ehat dich unsichtbar gemacht.");
            }
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

