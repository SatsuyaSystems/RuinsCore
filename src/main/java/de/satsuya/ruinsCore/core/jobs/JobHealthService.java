package de.satsuya.ruinsCore.core.jobs;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class JobHealthService {

    private static final double DEFAULT_MAX_HEALTH = 20.0D;

    private final JobService jobService;
    private final Map<JobType, Double> healthBonuses;

    public JobHealthService(JobService jobService) {
        this.jobService = jobService;
        this.healthBonuses = new EnumMap<>(JobType.class);

        healthBonuses.put(JobType.LEUTNANT, 14.0D); // 7 extra Herzen
        healthBonuses.put(JobType.RITTER, 2.0D); // 1 extra Herz
        healthBonuses.put(JobType.WACHE, 8.0D); // 4 extra Herzen
        healthBonuses.put(JobType.SENSENMANN, 10.0D); // 5 extra Herzen
        healthBonuses.put(JobType.PRINZ_PRINZESSIN, 10.0D); // 5 extra Herzen
    }

    public void sync(Player player) {
        Optional<JobType> currentJob = jobService.getJob(player.getUniqueId());
        double bonus = currentJob.map(jobType -> healthBonuses.getOrDefault(jobType, 0.0D)).orElse(0.0D);

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        double targetMaxHealth = DEFAULT_MAX_HEALTH + bonus;
        if (maxHealth.getBaseValue() != targetMaxHealth) {
            maxHealth.setBaseValue(targetMaxHealth);
        }

        double currentHealth = player.getHealth();
        if (currentHealth > targetMaxHealth) {
            player.setHealth(targetMaxHealth);
        }
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
}

