package bg.sofia.uni.fmi.mjt.labels;

public enum MealType {
    BREAKFAST("Breakfast"),
    DINNER("Dinner"),
    LUNCH("Lunch"),
    SNACK("Snack"),
    TEATIME("Teatime");
    private final String label;

    MealType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
