package de.satsuya.ruinsCore.core.wache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WacheRestrainManager {

    private final Map<UUID, Long> restrainedPlayers = new HashMap<>();
    private final Map<String, Long> recentToggleActions = new HashMap<>();
    private static final long RESTRAIN_TIMEOUT_MS = 5 * 60 * 1000; // 5 minutes
    private static final long TOGGLE_DEBOUNCE_MS = 300;

    public void restrain(UUID playerUuid) {
        restrainedPlayers.put(playerUuid, System.currentTimeMillis());
    }

    public void release(UUID playerUuid) {
        restrainedPlayers.remove(playerUuid);
    }

    public boolean shouldProcessToggle(UUID wacheUuid, UUID targetUuid) {
        long now = System.currentTimeMillis();
        String key = wacheUuid.toString() + "->" + targetUuid;
        Long lastAction = recentToggleActions.get(key);

        if (lastAction != null && now - lastAction < TOGGLE_DEBOUNCE_MS) {
            return false;
        }

        recentToggleActions.put(key, now);
        return true;
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

        long now = System.currentTimeMillis();
        recentToggleActions.entrySet().removeIf(entry -> now - entry.getValue() > TOGGLE_DEBOUNCE_MS);
    }
}

