package de.satsuya.ruinsCore.core.jobs;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class LeutnantHealthService {

    private static final double DEFAULT_MAX_HEALTH = 20.0D;
    private static final double LEUTNANT_HEALTH_BONUS = 14.0D; // 7 extra Herzen

    private final JobService jobService;

    public LeutnantHealthService(JobService jobService) {
        this.jobService = jobService;
    }

    public void sync(Player player) {
        boolean hasLeutnant = jobService.hasJob(player.getUniqueId(), JobType.LEUTNANT);
        if (hasLeutnant) {
            applyBonus(player);
            return;
        }

        removeBonus(player);
    }

    public void syncOnline(UUID playerUuid) {
        Player online = Bukkit.getPlayer(playerUuid);
        if (online == null) {
            return;
        }

        sync(online);
    }

    public void syncAllOnline() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            sync(online);
        }
    }

    private void applyBonus(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        double targetMaxHealth = DEFAULT_MAX_HEALTH + LEUTNANT_HEALTH_BONUS;
        if (maxHealth.getBaseValue() != targetMaxHealth) {
            maxHealth.setBaseValue(targetMaxHealth);
        }
    }

    private void removeBonus(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        if (maxHealth.getBaseValue() != DEFAULT_MAX_HEALTH) {
            maxHealth.setBaseValue(DEFAULT_MAX_HEALTH);
        }

        double currentHealth = player.getHealth();
        double allowedMax = maxHealth.getValue();
        if (currentHealth > allowedMax) {
            player.setHealth(allowedMax);
        }
    }
}


