package de.satsuya.ruinsCore.core.wache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WacheRestrainManager {

    private final Map<UUID, Long> restrainedPlayers = new HashMap<>();
    private static final long RESTRAIN_TIMEOUT_MS = 5 * 60 * 1000; // 5 minutes

    public void restrain(UUID playerUuid) {
        restrainedPlayers.put(playerUuid, System.currentTimeMillis());
    }

    public void release(UUID playerUuid) {
        restrainedPlayers.remove(playerUuid);
    }

    public boolean isRestrained(UUID playerUuid) {
        if (!restrainedPlayers.containsKey(playerUuid)) {
            return false;
        }

        long restrainedSince = restrainedPlayers.get(playerUuid);
        long elapsed = System.currentTimeMillis() - restrainedSince;

        if (elapsed > RESTRAIN_TIMEOUT_MS) {
            restrainedPlayers.remove(playerUuid);
            return false;
        }

        return true;
    }

    public void cleanup() {
        restrainedPlayers.entrySet().removeIf(entry -> {
            long elapsed = System.currentTimeMillis() - entry.getValue();
            return elapsed > RESTRAIN_TIMEOUT_MS;
        });
    }
}

