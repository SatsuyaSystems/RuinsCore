package de.satsuya.ruinsCore.commands;

import de.satsuya.ruinsCore.core.classes.RuinClassService;
import de.satsuya.ruinsCore.core.classes.RuinClassType;
import de.satsuya.ruinsCore.core.command.CoreCommand;
import de.satsuya.ruinsCore.core.permission.PermissionNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class GhostCommand implements CoreCommand {

    private final RuinClassService classService;

    public GhostCommand(RuinClassService classService) {
        this.classService = classService;
    }

    @Override
    public String getName() {
        return "ghost";
    }

    @Override
    public String getDescription() {
        return "Schaltet die Geistform um (Nur für Waldgeist).";
    }

    @Override
    public String getUsage() {
        return "/ghost";
    }

    @Override
    public PermissionNode getPermission() {
        return PermissionNode.COMMAND_GHOST;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen.");
            return true;
        }

        Player player = (Player) sender;
        Optional<RuinClassType> playerClass = classService.getClass(player.getUniqueId());

        if (playerClass.isEmpty() || playerClass.get() != RuinClassType.WALDGEIST) {
            player.sendMessage("§cNur Waldgeister können in die Geistform wechseln.");
            return true;
        }

        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.sendMessage("§aDu bist nun wieder sichtbar.");
        } else {
            // Unendliche Unsichtbarkeit und Partikel über Listener
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            player.sendMessage("§8Du bist nun ein Geist.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
