package de.satsuya.ruinsCore.core.vanish;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Service zur Verwaltung von vanished Spielern
 */
public final class VanishService {

    private final Plugin plugin;
    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public VanishService(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Setzt einen Spieler auf vanish
     */
    public void setVanished(Player player, boolean vanished) {
        if (vanished) {
            vanishedPlayers.add(player.getUniqueId());
            // Verstecke vor allen (außer sich selbst)
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.equals(player)) {
                    onlinePlayer.hidePlayer(plugin, player);
                }
            }
        } else {
            vanishedPlayers.remove(player.getUniqueId());
            // Zeige allen
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.showPlayer(plugin, player);
            }
        }
    }

    /**
     * Prüft, ob ein Spieler vanished ist
     */
    public boolean isVanished(UUID playerUuid) {
        return vanishedPlayers.contains(playerUuid);
    }

    /**
     * Verstecke vanished Spieler für einen neu joinenden Spieler
     */
    public void hideVanishedPlayersFor(Player newPlayer) {
        for (UUID vanishedUuid : vanishedPlayers) {
            Player vanishedPlayer = Bukkit.getPlayer(vanishedUuid);
            if (vanishedPlayer != null && !vanishedPlayer.equals(newPlayer)) {
                newPlayer.hidePlayer(plugin, vanishedPlayer);
            }
        }
    }

    /**
     * Gibt die Menge aller vanished Spieler zurück
     */
    public Set<UUID> getVanishedPlayers() {
        return new HashSet<>(vanishedPlayers);
    }
}

