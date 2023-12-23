package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Optional;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {

    public Rocket {
        if (id == null || id.isEmpty() || id.isBlank() ||
                name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Invalid id or name.");
        }

    }

    public static Rocket of(String line) {
        String[] arr = line.split("(?:^|,)(?:\\s*\"((?:[^\"]|\\\\\")*)\"\\s*|([^,]*))(?=,|$)");
        int i = 0;
        String id = arr[i++];
        String name = arr[i++];
        Optional<String> wiki = arr[i].isEmpty() ? Optional.empty() : Optional.of(arr[i].trim());
        i++;
        Optional<Double> height = arr[i].isEmpty() ? Optional.empty() :
                Optional.of(Double.parseDouble(arr[i].substring(0, arr[i].length() - 2)));
        return new Rocket(id, name, wiki, height);
    }
}
