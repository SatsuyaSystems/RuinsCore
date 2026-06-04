package de.satsuya.ruinsCore.core.classes;

public enum RuinClassType {
    FEE("Fee"),
    PRIESTER("Priester"),
    ZWERG("Zwerg"),
    WALDGEIST("Waldgeist"),
    HEXE("Hexe"),
    ASSASINE("Assasine"),
    SCHATTENLAEUFER("Schattenläufer"),
    ORK("Ork"),
    DEMON("Demon");

    private final String displayName;

    RuinClassType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

