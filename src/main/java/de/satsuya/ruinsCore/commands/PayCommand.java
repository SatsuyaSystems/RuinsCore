package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.economy.EconomyService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class PayCommand implements CoreCommand {

    private final EconomyService economyService;

    public PayCommand(RuinsCore plugin) {
        this.economyService = plugin.getEconomyService();
    }

    @Override
    public String getName() {
        return "pay";
    }

    @Override
    public String getDescription() {
        return "Sende Geld an einen anderen Spieler.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_PAY;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§c/pay <Spieler> <Betrag>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cDu kannst dir selbst kein Geld senden.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                player.sendMessage("§cDer Betrag muss größer als 0 sein.");
                return true;
            }
        } catch (NumberFormatException exception) {
            player.sendMessage("§cUngültiger Betrag.");
            return true;
        }

        // Guthaben prüfen
        if (!economyService.hasBalance(player.getUniqueId(), amount)) {
            player.sendMessage("§cDu hast nicht genug Geld! Du brauchst: §a" + economyService.formatBalance(amount));
            return true;
        }

        // Geld transferieren
        if (!economyService.removeBalance(player.getUniqueId(), amount)) {
            player.sendMessage("§cTransfer fehlgeschlagen.");
            return true;
        }

        economyService.addBalance(target.getUniqueId(), amount);

        // Nachrichten
        player.sendMessage("§a✓ Du hast §6" + economyService.formatBalance(amount) + " §azur §6" + target.getName() + " §agesendet.");
        target.sendMessage("§a✓ Du hast §6" + economyService.formatBalance(amount) + " §avon §6" + player.getName() + " §aerhalten.");

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
            completions.add("<Betrag>");
        }

        return completions;
    }
}

