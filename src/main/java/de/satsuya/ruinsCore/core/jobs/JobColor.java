package de.satsuya.ruinsCore.core.jobs;

/**
 * Enum für Job-Farben und Prefixe
 */
public enum JobColor {
    HOLZFAELLER("§2[Holzfäller]", "§a"),
    MINER("§1[Minenarbeiter]", "§9"),
    BUILDER("§7[Baumeister]", "§f"),
    FISCHER("§3[Fischer]", "§b"),
    BAUER("§6[Bauer]", "§e"),
    SCHMIED("§8[Schmied]", "§8"),
    LEUTNANT("§c[⚔ Leutnant]", "§c"),
    RITTER("§6[🗡 Ritter]", "§6"),
    LEHRER("§d[Lehrer]", "§d"),
    BANNERRIST("§5[Banner-Künstler]", "§5"),
    WACHE("§4[Wache]", "§4"),
    BERATER("§e[Berater]", "§e"),
    SCHANKWIRT("§6[Schankwirt]", "§6"),
    VERZAUBERER("§b[Verzauberer]", "§b"),
    SENSENMANN("§0[Sensenmann]", "§0"),
    PRINZ_PRINZESSIN("§d[Prinz/Prinzessin]", "§d");

    private final String displayName;
    private final String colorCode;

    JobColor(String displayName, String colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    /**
     * Gebe die JobColor für einen JobType zurück
     */
    public static JobColor fromJobType(JobType jobType) {
        try {
            // Nutze exakt die JobType ID um den entsprechenden Enum zu finden
            String enumName = jobType.getId().toUpperCase();
            return JobColor.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

