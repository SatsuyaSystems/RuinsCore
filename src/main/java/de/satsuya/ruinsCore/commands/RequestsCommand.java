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

public final class RequestsCommand implements CoreCommand {

    private final EconomyService economyService;
    private final MoneyTransactionService transactionService;

    public RequestsCommand(RuinsCore plugin) {
        this.economyService = plugin.getEconomyService();
        this.transactionService = plugin.getMoneyTransactionService();
    }

    @Override
    public String getName() {
        return "requests";
    }

    @Override
    public String getDescription() {
        return "Verwalte deine Geldanfragen.";
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

        if (args.length == 0) {
            showPendingRequests(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("accept")) {
            if (args.length < 2) {
                player.sendMessage("§c/requests accept <ID>");
                return true;
            }

            try {
                int requestId = Integer.parseInt(args[1]);
                if (transactionService.acceptRequest(requestId)) {
                    player.sendMessage("§a✓ Anfrage akzeptiert.");

                    // Benachrichtige den Anforderer
                    var request = transactionService.findRequestById(requestId);
                    if (request.isPresent()) {
                        var req = request.get();
                        Player requester = Bukkit.getPlayer(req.getRequesterUuid());
                        if (requester != null && requester.isOnline()) {
                            requester.sendMessage("§a✓ §6" + player.getName() + " §ahat deine Anfrage akzeptiert.");
                        }
                    }
                } else {
                    player.sendMessage("§cAnfrage konnte nicht akzeptiert werden.");
                }
            } catch (NumberFormatException exception) {
                player.sendMessage("§cUngültige ID.");
            }
            return true;
        }

        if (subCommand.equals("decline")) {
            if (args.length < 2) {
                player.sendMessage("§c/requests decline <ID>");
                return true;
            }

            try {
                int requestId = Integer.parseInt(args[1]);
                if (transactionService.declineRequest(requestId)) {
                    player.sendMessage("§a✓ Anfrage abgelehnt.");

                    // Benachrichtige den Anforderer
                    var request = transactionService.findRequestById(requestId);
                    if (request.isPresent()) {
                        var req = request.get();
                        Player requester = Bukkit.getPlayer(req.getRequesterUuid());
                        if (requester != null && requester.isOnline()) {
                            requester.sendMessage("§c✗ §6" + player.getName() + " §chat deine Anfrage abgelehnt.");
                        }
                    }
                } else {
                    player.sendMessage("§cAnfrage konnte nicht abgelehnt werden.");
                }
            } catch (NumberFormatException exception) {
                player.sendMessage("§cUngültige ID.");
            }
            return true;
        }

        player.sendMessage("§c/requests [accept/decline <ID>]");
        return true;
    }

    private void showPendingRequests(Player player) {
        List<MoneyTransactionService.MoneyRequest> requests = transactionService.getPendingRequests(player.getUniqueId());

        if (requests.isEmpty()) {
            player.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            player.sendMessage("§6Du hast keine ausstehenden Anfragen.");
            player.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            return;
        }

        player.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§6Ausstehende Anfragen:");
        player.sendMessage("");

        for (MoneyTransactionService.MoneyRequest request : requests) {
            String requesterName = economyService.getPlayerName(request.getRequesterUuid());
            String amount = economyService.formatBalance(request.getAmount());
            player.sendMessage("§6[ID: " + request.getId() + "] §e" + requesterName + " §6- " + amount);
            player.sendMessage("  §a/requests accept " + request.getId() + " §7| §c/requests decline " + request.getId());
        }

        player.sendMessage("§a━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("accept");
            completions.add("decline");
        }

        return completions;
    }
}

