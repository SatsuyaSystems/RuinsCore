package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.auction.gui.AuctionGuiService;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Command zum Öffnen der Auktionen
 */
public final class AuctionCommand implements CoreCommand {

    private final AuctionGuiService auctionGuiService;

    public AuctionCommand(RuinsCore plugin) {
        this.auctionGuiService = plugin.getAuctionGuiService();
    }

    @Override
    public String getName() {
        return "auction";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.AUCTION_USE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Command nutzen.");
            return true;
        }

        auctionGuiService.openAuctionOverviewGui(player, 1);
        player.sendMessage("§6Auktionen-GUI geöffnet!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return List.of();
    }
}

