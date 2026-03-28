package de.satsuya.ruinsCore.core.jobs;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum JobType {
    HOLZFAELLER("holzfaeller", "Holzfäller"),
    MINER("miner", "Miner"),
    BUILDER("builder", "Builder"),
    FISCHER("fischer", "Fischer");

    private final String id;
    private final String displayName;

    JobType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<JobType> fromInput(String input) {
        String normalized = input.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(jobType -> jobType.id.equals(normalized))
                .findFirst();
    }
}

