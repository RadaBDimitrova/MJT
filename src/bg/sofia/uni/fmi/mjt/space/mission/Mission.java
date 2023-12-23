package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public record Mission(String id, String company, String location, LocalDate date, Detail detail,
                      RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E MMM dd, yyyy");

    public Mission {
        if (id == null || company == null || location == null || id.isEmpty() || id.isBlank() ||
                company.isEmpty() || company.isBlank() || location.isEmpty() || location.isBlank() ||
                date == null || detail == null || detail.rocketName() == null || detail.payload() == null ||
                detail.rocketName().isEmpty() || detail.rocketName().isBlank() || detail.payload().isEmpty() ||
                detail.payload().isBlank() || rocketStatus == null ||
                cost.isPresent() && cost.get().compareTo(BigDecimal.ZERO.doubleValue()) <= 0
                || missionStatus == null) {
            throw new IllegalArgumentException("Invalid fields");
        }
    }

    public static Mission of(String line) {
        String[] arr = line.split("(?:^|,)(?:\\s*\"((?:[^\"]|\\\\\")*)\"\\s*|([^,]*))(?=,|$)");
        int i = 0;
        String id = arr[i++];
        String company = arr[i++];
        String location = arr[i++];
        LocalDate date = LocalDate.parse(arr[i].substring(1, arr[i].length() - 1), DATE_FORMATTER);
        i++;
        String[] details = arr[i++].split("\\|");
        int j = 0;
        Detail detail = new Detail(details[j++].trim(), details[j].trim());

        RocketStatus rocketStatus = RocketStatus.valueOf(arr[i++]);
        Optional<Double> cost = arr[i].isEmpty() ? Optional.empty() : Optional.of(Double.parseDouble(arr[i].trim()));
        i++;
        MissionStatus missionStatus = MissionStatus.valueOf(arr[i].trim());

        return new Mission(id, company, location, date, detail, rocketStatus, cost, missionStatus);
    }
}
