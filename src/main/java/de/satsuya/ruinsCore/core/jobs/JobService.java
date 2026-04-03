package de.satsuya.ruinsCore.core.jobs;

import de.satsuya.ruinsCore.core.database.DatabaseManager;
import de.satsuya.ruinsCore.core.util.LoggerUtil;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class JobService {

    private final DatabaseManager databaseManager;
    private final LoggerUtil loggerUtil;

    public JobService(DatabaseManager databaseManager, LoggerUtil loggerUtil) {
        this.databaseManager = databaseManager;
        this.loggerUtil = loggerUtil;
    }

    public boolean assignJob(UUID playerUuid, JobType jobType) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return false;
        }

        String sql = "INSERT INTO player_jobs (player_uuid, job_type) VALUES (?, ?) "
                + "ON CONFLICT(player_uuid) DO UPDATE SET job_type = excluded.job_type";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUuid.toString());
            statement.setString(2, jobType.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            loggerUtil.severe("Job konnte nicht zugewiesen werden.", exception);
            return false;
        }
    }

    public boolean removeJob(UUID playerUuid) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return false;
        }

        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM player_jobs WHERE player_uuid = ?")) {
            statement.setString(1, playerUuid.toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            loggerUtil.severe("Job konnte nicht entfernt werden.", exception);
            return false;
        }
    }

    public Optional<JobType> getJob(UUID playerUuid) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return Optional.empty();
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT job_type FROM player_jobs WHERE player_uuid = ?")) {
            statement.setString(1, playerUuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                String id = resultSet.getString("job_type");
                return JobType.fromInput(id);
            }
        } catch (SQLException exception) {
            loggerUtil.severe("Job konnte nicht gelesen werden.", exception);
            return Optional.empty();
        }
    }

    /**
     * Gibt den Job-Namen eines Spielers als String zurück
     */
    public String getPlayerJob(UUID playerUuid) {
        Optional<JobType> job = getJob(playerUuid);
        if (job.isPresent()) {
            return job.get().getDisplayName();
        }
        return "Arbeitslos";
    }

    public List<OfflinePlayer> getMembers(JobType jobType) {
        Connection connection = databaseManager.getConnectionOrNull();
        List<OfflinePlayer> members = new ArrayList<>();

        if (connection == null) {
            return members;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT player_uuid FROM player_jobs WHERE job_type = ? ORDER BY player_uuid ASC")) {
            statement.setString(1, jobType.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String rawUuid = resultSet.getString("player_uuid");
                    try {
                        UUID uuid = UUID.fromString(rawUuid);
                        members.add(org.bukkit.Bukkit.getOfflinePlayer(uuid));
                    } catch (IllegalArgumentException ignored) {
                        // Invalid rows are skipped to keep loading robust.
                    }
                }
            }
        } catch (SQLException exception) {
            loggerUtil.severe("Job-Mitglieder konnten nicht geladen werden.", exception);
        }

        return members;
    }

    public boolean hasJob(UUID playerUuid, JobType jobType) {
        return getJob(playerUuid)
                .map(existing -> existing == jobType)
                .orElse(false);
    }

    public boolean addLeader(UUID playerUuid, JobType jobType) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return false;
        }

        String sql = "INSERT INTO job_leaders (job_type, player_uuid) VALUES (?, ?)"
                + "ON CONFLICT(job_type, player_uuid) DO NOTHING";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, jobType.getId());
            statement.setString(2, playerUuid.toString());
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            loggerUtil.severe("Leader konnte nicht hinzugefügt werden.", exception);
            return false;
        }
    }

    public boolean removeLeader(UUID playerUuid, JobType jobType) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return false;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM job_leaders WHERE job_type = ? AND player_uuid = ?")) {
            statement.setString(1, jobType.getId());
            statement.setString(2, playerUuid.toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            loggerUtil.severe("Leader konnte nicht entfernt werden.", exception);
            return false;
        }
    }

    public boolean isLeader(UUID playerUuid, JobType jobType) {
        Connection connection = databaseManager.getConnectionOrNull();
        if (connection == null) {
            return false;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM job_leaders WHERE job_type = ? AND player_uuid = ? LIMIT 1")) {
            statement.setString(1, jobType.getId());
            statement.setString(2, playerUuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            loggerUtil.severe("Leader-Status konnte nicht geladen werden.", exception);
            return false;
        }
    }

    public Set<JobType> getLeaderJobs(UUID playerUuid) {
        Connection connection = databaseManager.getConnectionOrNull();
        Set<JobType> jobs = EnumSet.noneOf(JobType.class);
        if (connection == null) {
            return jobs;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT job_type FROM job_leaders WHERE player_uuid = ?")) {
            statement.setString(1, playerUuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    JobType.fromInput(resultSet.getString("job_type")).ifPresent(jobs::add);
                }
            }
        } catch (SQLException exception) {
            loggerUtil.severe("Leader-Jobs konnten nicht geladen werden.", exception);
        }

        return jobs;
    }

    public List<OfflinePlayer> getLeaders(JobType jobType) {
        Connection connection = databaseManager.getConnectionOrNull();
        List<OfflinePlayer> leaders = new ArrayList<>();
        if (connection == null) {
            return leaders;
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT player_uuid FROM job_leaders WHERE job_type = ? ORDER BY player_uuid ASC")) {
            statement.setString(1, jobType.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String rawUuid = resultSet.getString("player_uuid");
                    try {
                        UUID uuid = UUID.fromString(rawUuid);
                        leaders.add(org.bukkit.Bukkit.getOfflinePlayer(uuid));
                    } catch (IllegalArgumentException ignored) {
                        // Invalid rows are skipped to keep loading robust.
                    }
                }
            }
        } catch (SQLException exception) {
            loggerUtil.severe("Leader-Liste konnte nicht geladen werden.", exception);
        }

        return leaders;
    }
}

