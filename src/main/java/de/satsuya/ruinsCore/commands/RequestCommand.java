package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.economy.EconomyService;
import de.satsuya.ruinsCore.core.economy.MoneyTransactionService;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class RequestCommand implements CoreCommand {

    private final EconomyService economyService;
    private final MoneyTransactionService transactionService;

    public RequestCommand(RuinsCore plugin) {
        this.economyService = plugin.getEconomyService();
        this.transactionService = plugin.getMoneyTransactionService();
    }

    @Override
    public String getName() {
        return "request";
    }

    @Override
    public String getDescription() {
        return "Fordere Geld von einem anderen Spieler an.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_REQUEST;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§c/request <Spieler> <Betrag>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cDu kannst dir selbst kein Geld anfordern.");
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

        // Geldanfrage erstellen
        if (!transactionService.createMoneyRequest(player.getUniqueId(), target.getUniqueId(), amount)) {
            player.sendMessage("§cAnfrage konnte nicht erstellt werden.");
            return true;
        }

        player.sendMessage("§a✓ Du hast §6" + economyService.formatBalance(amount) + " §avon §6" + target.getName() + " §aangefordert.");
        target.sendMessage("§6" + player.getName() + " §efordert §6" + economyService.formatBalance(amount) + " §ean.");
        target.sendMessage("§eVerwende §6/requests§e um deine Anfragen zu sehen.");

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

