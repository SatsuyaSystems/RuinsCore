package de.satsuya.ruinsCore.core.jobs;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum JobType {
    HOLZFAELLER("holzfaeller", "Holzfäller"),
    MINER("miner", "Miner"),
    BUILDER("builder", "Builder"),
    FISCHER("fischer", "Fischer"),
    BAUER("bauer", "Bauer"),
    SCHMIED("schmied", "Schmied"),
    LEUTNANT("leutnant", "Leutnant"),
    RITTER("ritter", "Ritter"),
    LEHRER("lehrer", "Lehrer"),
    BANNERRIST("bannerrist", "Bannerrist"),
    WACHE("wache", "Wache"),
    BERATER("berater", "Berater"),
    SCHANKWIRT("schankwirt", "Schankwirt"),
    VERZAUBERER("verzauberer", "Verzauberer"),
    SENSENMANN("sensenmann", "Sensenmann"),
    PRINZ_PRINZESSIN("prinz_prinzessin", "Prinz/Prinzessin");

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

