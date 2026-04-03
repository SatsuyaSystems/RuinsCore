package de.satsuya.ruinsCore.core.freeze;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Service zur Verwaltung gefrorener Spieler
 */
public final class FreezeService {

    private final Set<UUID> frozenPlayers = new HashSet<>();

    /**
     * Friere einen Spieler ein
     */
    public void freezePlayer(Player player) {
        frozenPlayers.add(player.getUniqueId());
        
        // Gebe Slowness und Blindness, um Bewegung zu verhindern
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 255, false, false));
        
        player.sendMessage("§c§l[FREEZE] §eDu wurdest eingefroren!");
    }

    /**
     * Taue einen Spieler auf
     */
    public void unfreezePlayer(Player player) {
        frozenPlayers.remove(player.getUniqueId());
        
        // Entferne Slowness
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        
        player.sendMessage("§a§l[FREEZE] §eDu wurdest aufgetaut!");
    }

    /**
     * Prüfe ob Spieler gefroren ist
     */
    public boolean isFrozen(UUID playerUuid) {
        return frozenPlayers.contains(playerUuid);
    }

    /**
     * Taue alle Spieler auf
     */
    public void unfreezeAll() {
        for (UUID uuid : new HashSet<>(frozenPlayers)) {
            Player player = org.bukkit.Bukkit.getPlayer(uuid);
            if (player != null) {
                unfreezePlayer(player);
            } else {
                frozenPlayers.remove(uuid);
            }
        }
    }

    /**
     * Gibt alle gefrorenen Spieler zurück
     */
    public Set<UUID> getFrozenPlayers() {
        return new HashSet<>(frozenPlayers);
    }
}

