package dec;

public enum Spoluprace {
    SPATNA,
    PRUMERNA,
    DOBRA;

    @Override
    public String toString() {
        switch (this) {
            case SPATNA: return "špatná";
            case PRUMERNA: return "průměrná";
            case DOBRA: return "dobrá";
            default: return super.toString();
        }
    }
}