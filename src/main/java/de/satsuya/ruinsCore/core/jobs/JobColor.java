package de.satsuya.ruinsCore.core.jobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Enum für Job-Farben und Prefixe
 */
public enum JobColor {
    HOLZFAELLER("§2[Holzfäller]", "§a", NamedTextColor.DARK_GREEN),
    MINER("§1[Minenarbeiter]", "§9", NamedTextColor.DARK_BLUE),
    BUILDER("§7[Baumeister]", "§f", NamedTextColor.WHITE),
    FISCHER("§3[Fischer]", "§b", NamedTextColor.DARK_AQUA),
    BAUER("§6[Bauer]", "§e", NamedTextColor.GOLD),
    SCHMIED("§8[Schmied]", "§8", NamedTextColor.DARK_GRAY),
    LEUTNANT("§c[⚔ Leutnant]", "§c", NamedTextColor.RED),
    RITTER("§6[🗡 Ritter]", "§6", NamedTextColor.GOLD),
    LEHRER("§d[Lehrer]", "§d", NamedTextColor.LIGHT_PURPLE),
    BANNERRIST("§5[Banner-Künstler]", "§5", NamedTextColor.DARK_PURPLE),
    WACHE("§4[Wache]", "§4", NamedTextColor.DARK_RED),
    BERATER("§e[Berater]", "§e", NamedTextColor.YELLOW),
    SCHANKWIRT("§6[Schankwirt]", "§6", NamedTextColor.GOLD),
    VERZAUBERER("§b[Verzauberer]", "§b", NamedTextColor.AQUA),
    SENSENMANN("§0[Sensenmann]", "§0", NamedTextColor.BLACK),
    PRINZ_PRINZESSIN("§d[Prinz/Prinzessin]", "§d", NamedTextColor.LIGHT_PURPLE);

    private final String displayName;
    private final String colorCode;
    private final NamedTextColor adventureColor;

    JobColor(String displayName, String colorCode, NamedTextColor adventureColor) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.adventureColor = adventureColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public NamedTextColor getAdventureColor() {
        return adventureColor;
    }

    /**
     * Gebe die Adventure Component für den Job-Namen zurück
     */
    public Component getDisplayComponent() {
        String jobName = displayName.replaceAll("§.", "").trim(); // Entferne Farbcodes
        return Component.text(jobName).color(adventureColor);
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




