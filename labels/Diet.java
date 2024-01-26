package bg.sofia.uni.fmi.mjt.labels;

public enum Diet {
    BALANCED("balanced"),
    HIGH_PROTEIN("high-protein"),
    HIGH_FIBER("high-fiber"),
    LOW_FAT("low-fat"),
    LOW_CARB("low-carb"),
    LOW_SODIUM("low-sodium");

    private final String label;

    Diet(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}


