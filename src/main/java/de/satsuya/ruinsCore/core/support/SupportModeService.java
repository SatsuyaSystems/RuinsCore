package de.satsuya.ruinsCore.core.support;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service zur Verwaltung des Support Modes
 */
public final class SupportModeService {

    private final Map<UUID, SupportModeData> supportModePlayers = new HashMap<>();

    /**
     * Aktiviere Support Mode für einen Spieler
     */
    public void enableSupportMode(Player player, Color glowColor) {
        // Speichere den ursprünglichen Zustand
        SupportModeData data = new SupportModeData(
                player.isInvulnerable(),
                player.getAllowFlight(),
                glowColor
        );
        supportModePlayers.put(player.getUniqueId(), data);

        // Aktiviere Godmode (Invulnerability + Unverwundbarkeit)
        player.setInvulnerable(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 255, false, false));

        // Aktiviere Fly
        player.setAllowFlight(true);
        player.setFlying(true);

        // Aktiviere Glow-Effekt
        player.setGlowing(true);

        // Gebe Feedback
        player.sendMessage("§6§l[SUPPORT] §aSupport Mode aktiviert!");
        player.sendMessage("§6Godmode: §a✓ | §6Fly: §a✓ | §6Glow: §a✓");
    }

    /**
     * Deaktiviere Support Mode für einen Spieler
     */
    public void disableSupportMode(Player player) {
        SupportModeData data = supportModePlayers.remove(player.getUniqueId());
        if (data == null) {
            return;
        }

        // Stelle ursprüngliche Zustände wieder her
        player.setInvulnerable(data.wasInvulnerable());
        player.removePotionEffect(PotionEffectType.RESISTANCE);

        player.setAllowFlight(data.hadFlyEnabled());
        player.setFlying(false);

        player.setGlowing(false);

        // Gebe Feedback (nur wenn Spieler noch online)
        if (player.isOnline()) {
            player.sendMessage("§6§l[SUPPORT] §cSupport Mode deaktiviert.");
        }
    }

    /**
     * Prüfe ob Spieler im Support Mode ist
     */
    public boolean isInSupportMode(UUID playerUuid) {
        return supportModePlayers.containsKey(playerUuid);
    }

    /**
     * Gibt alle Support Mode Daten
     */
    public Map<UUID, SupportModeData> getAllSupportModePlayers() {
        return new HashMap<>(supportModePlayers);
    }

    /**
     * Deaktiviere alle Support Modes (beim Plugin Disable)
     */
    public void disableAllSupportModes() {
        for (UUID uuid : new HashMap<>(supportModePlayers).keySet()) {
            Player player = org.bukkit.Bukkit.getPlayer(uuid);
            if (player != null) {
                disableSupportMode(player);
            } else {
                supportModePlayers.remove(uuid);
            }
        }
    }

    /**
     * Innere Klasse für Support Mode Daten
     */
    public static class SupportModeData {
        private final boolean wasInvulnerable;
        private final boolean hadFlyEnabled;
        private final Color glowColor;

        public SupportModeData(boolean wasInvulnerable, boolean hadFlyEnabled, Color glowColor) {
            this.wasInvulnerable = wasInvulnerable;
            this.hadFlyEnabled = hadFlyEnabled;
            this.glowColor = glowColor;
        }

        public boolean wasInvulnerable() {
            return wasInvulnerable;
        }

        public boolean hadFlyEnabled() {
            return hadFlyEnabled;
        }

        public Color getGlowColor() {
            return glowColor;
        }
    }
}

