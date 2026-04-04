package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import de.satsuya.ruinsCore.core.playtime.PlaytimeService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command zur Anzeige der Spielzeit
 */
public final class PlaytimeCommand implements CoreCommand {

    private final PlaytimeService playtimeService;
    private final RuinsCore plugin;

    public PlaytimeCommand(RuinsCore plugin) {
        this.plugin = plugin;
        this.playtimeService = plugin.getPlaytimeService();
    }

    @Override
    public String getName() {
        return "playtime";
    }

    @Override
    public String getDescription() {
        return "Zeige deine oder die Spielzeit eines anderen Spielers an";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_PLAYTIME;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        // Keine Argumente = Eigene Spielzeit anzeigen
        if (args.length == 0) {
            showOwnPlaytime(player);
            return true;
        }

        // Mit Argument = Spielzeit eines anderen Spielers anzeigen
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage("§c✗ Spieler '" + args[0] + "' nicht gefunden!");
            return true;
        }

        // Permission Check
        if (!plugin.getPermissionManager().has(player, PermissionNode.COMMAND_PLAYTIME_OTHER)) {
            player.sendMessage("§c✗ Du hast keine Permission um die Spielzeit von anderen Spielern zu sehen!");
            return true;
        }

        showOtherPlaytime(player, target);
        return true;
    }

    private void showOwnPlaytime(Player player) {
        String playtime = playtimeService.getPlaytimeString(player.getUniqueId());
        player.sendMessage("§d━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§dDeine Spielzeit: §5" + playtime);
        player.sendMessage("§d━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private void showOtherPlaytime(Player player, Player target) {
        String playtime = playtimeService.getPlaytimeString(target.getUniqueId());
        player.sendMessage("§d━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§dSpielzeit von §5" + target.getName() + "§d: §5" + playtime);
        player.sendMessage("§d━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        }

        return completions;
    }
}

