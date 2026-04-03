package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.RuinsCore;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import de.satsuya.ruinsCore.core.support.SupportModeService;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * SupportMode Command - Aktiviere/Deaktiviere Support Mode mit Godmode und Glow
 */
public final class SupportModeCommand implements CoreCommand {

    private final SupportModeService supportModeService;

    public SupportModeCommand(RuinsCore plugin) {
        this.supportModeService = plugin.getSupportModeService();
    }

    @Override
    public String getName() {
        return "supportmode";
    }

    @Override
    public String getDescription() {
        return "Aktiviere oder deaktiviere Support Mode mit Godmode und Glow.";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_SUPPORT_MODE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDieser Command kann nur von Spielern verwendet werden.");
            return true;
        }

        // Prüfe ob bereits im Support Mode
        boolean isAlreadyActive = supportModeService.isInSupportMode(player.getUniqueId());

        if (isAlreadyActive) {
            // Deaktiviere Support Mode
            supportModeService.disableSupportMode(player);
            return true;
        }

        // Aktiviere Support Mode mit Standard-Farbe (Weiß)
        supportModeService.enableSupportMode(player, Color.WHITE);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}

